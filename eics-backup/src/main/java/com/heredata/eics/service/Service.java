package com.heredata.eics.service;

import com.heredata.eics.task.ScheduledUpload;
import com.heredata.exception.ServiceException;
import com.heredata.hos.HOS;
import com.heredata.hos.model.*;
import com.heredata.hos.model.bucket.Bucket;
import com.heredata.hos.model.bucket.BucketList;
import com.heredata.hos.model.bucket.BucketVersioningConfiguration;
import com.heredata.model.VoidResult;
import com.heredata.swift.model.DownloadFileRequest;
import com.sitech.cmap.fw.core.wsg.WsgPageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 业务层
 * @author wuzz
 * @since 2024/6/21
 */
@org.springframework.stereotype.Service
@Slf4j
public class Service {

    /**
     * 桶前缀
     */
    @Value("${bucketPrefix}")
    private String bucketPrefix;

    @Resource
    private HOS hos;

    @Value("${fileLimit}")
    private long fileLimt;

    @Value("${partSize}")
    private long partSize;

    @Value("${threadNum}")
    private int threadNum;

    @Value("${scannerPath}")
    private String scannerPath;

    /**
     * 是否删除历史数据
     * true：上传完成后，会将以前（15天）日期中的改文件删除掉
     */
    @Value("${isDeleteHistory}")
    private boolean isDeleteHistory;

    @Value("${accessKey}")
    private String accessKey;

    @Value("${secretKey}")
    private String secretKey;

    /**
     * 获取对象列表
     * 按照目前的推理来看  一天150~200个文件，
     * 如果需要保存15天，那么2250~3000个文件，
     *
     * @param bucketName
     * @param fileName
     * @param date
     * @return
     */
    public WsgPageResult<List<HOSVersionSummary>> objectList(String bucketName, String fileName, String date, int pageSize, int curPage
    ) {

        List<HOSVersionSummary> list = new ArrayList<>();
        int total = 0;
        if (bucketName == null) {
            // 查询所有的备份信息
            ListBucketsRequest listBucketsRequest = new ListBucketsRequest();
            listBucketsRequest.setPrefix(bucketPrefix);
            BucketList bucketList = hos.listBuckets(listBucketsRequest);
            for (Bucket bukcet : bucketList.getBuckets()) {
                VersionListing versionListing = hos.listVersions(bukcet.getBucketName(), fileName);
                list.addAll(versionListing.getVersionSummaries());
            }
        } else {
            // 条件查询
            VersionListing versionListing = hos.listVersions(bucketName, fileName);
            list.addAll(versionListing.getVersionSummaries());
        }
        if (pageSize == -1 && curPage == -1) {
            return new WsgPageResult(list, 1, list.size(), list.size());
        }
        return new WsgPageResult<>(list, curPage, pageSize, total);
    }

    /**
     * 下载文件到指定路径
     * @param bucketName
     * @param fileName
     * @param filePath
     * @return
     */
    public Boolean downLoadObject(String bucketName, String fileName, String filePath) {
        long start = System.currentTimeMillis();
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, fileName);
        try {
            HOSObject object = hos.getObject(getObjectRequest);
            DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucketName, fileName);
            // 下载本地的文件
            downloadFileRequest.setDownloadFile(filePath);
            if (object.getSize() > fileLimt * 1024 * 1024) {
                // 走断点下载
                // 开启断点下载
                downloadFileRequest.setEnableCheckpoint(true);
                // 每片5M的进行下载
                downloadFileRequest.setPartSize(1024 * 1024 * partSize);
                downloadFileRequest.setTaskNum(threadNum);
                // 断点下载
                hos.downloadObject(downloadFileRequest);
            } else {
                // 走普通下载
                hos.downloadObject(downloadFileRequest);
            }
            log.info("bucket:{},object:{},fileSize:{},耗时:{} ms", bucketName, object
                    , converseFileSize(object.getSize()), System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("bucket:{},object:{},error:{}", bucketName, fileName, e.getMessage());
        } catch (Throwable throwable) {
            log.error("bucket:{},object:{},error:{}", bucketName, fileName, throwable.getMessage());
        }
        return true;
    }

    private String converseFileSize(Long size) {
        String[] arr = new String[]{"B", "KB", "MB", "GB", "TB"};
        int index = 0;
        while (size > 1024) {
            size /= 1024;
            index++;
        }
        return size + arr[index];
    }

    /**
     * 删除桶
     * @param bucket
     * @param isForce
     * @return
     */
    public Boolean deleteBucket(String bucket, boolean isForce) {
        if (hos.doesBucketExist(bucket)) {
            VersionListing versionListing = hos.listVersions(bucket, "");
            // 列出以初始化分片的列表信息
            ListMultipartUploadsRequest listMultipartUploadsRequest = new ListMultipartUploadsRequest(bucket);
            MultipartUploadListing multipartUploadListing = hos.listMultipartUploads(listMultipartUploadsRequest);
            if (!isForce) {
                if (versionListing.getVersionSummaries() != null && versionListing.getVersionSummaries().isEmpty()
                        && multipartUploadListing.getMultipartUploads() != null && multipartUploadListing.getMultipartUploads().isEmpty()) {
                    // 没有文件，直接删除即可
                    hos.deleteBucket(bucket);
                }
            } else {
                // 删除对象
                for (HOSVersionSummary versionSummary : versionListing.getVersionSummaries()) {
                    log.info("deleteBucket(),fileName:{}", versionSummary.getKey());
                    DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucket);
                    deleteObjectsRequest.withKey(versionSummary.getKey()).withVersionId(versionSummary.getVersionId());
                    hos.deleteObject(deleteObjectsRequest);
                    log.info("deleteBucket(), delete file success", versionSummary.getKey());
                }
                for (MultipartUpload multipartUpload : multipartUploadListing.getMultipartUploads()) {
                    AbortMultipartUploadRequest abortMultipartUploadRequest = new AbortMultipartUploadRequest(multipartUpload.getUploadId());
                    abortMultipartUploadRequest.setBucketName(bucket);
                    abortMultipartUploadRequest.setKey(multipartUpload.getKey());
                    hos.abortMultipartUpload(abortMultipartUploadRequest);
                }
                hos.deleteBucket(bucket);
            }
        }
        return true;
    }


    /**
     * 上传指定日期到对象存储中
     * @param date
     * @return
     */
    public Boolean upload(String date) {

        long start0 = System.currentTimeMillis();
        String dateStr = date;

        // 先查询桶是否存在
        if (!hos.doesBucketExist(bucketPrefix + dateStr)) {
            try {
                hos.createBucket(bucketPrefix + dateStr);
            } catch (Exception e) {
                log.error("桶创建失败:{}", bucketPrefix + dateStr);
                throw new RuntimeException("桶创建失败");
            }
        }

        /**
         * 遍历目录进行上传
         */
        log.info("执行上传任务,扫描路径为：" + scannerPath);
        File file = new File(scannerPath);
        File[] files = file.listFiles();
        long totalSize = 0;
        for (File file1 : files) {
            log.info("扫描到文件：" + file1.toString());
            log.info("开始上传=====================================================================================================================");
            long start = System.currentTimeMillis();
            if (file1.getName().contains(dateStr)) {
                totalSize += file1.length();
                try {
                    // 执行上传任务
                    if (hos.doesObjectExist(bucketPrefix + dateStr, file1.getName())) {
                        // 说明当前文件今天已经更新过了，已经存在了，开启桶的版本管理
                        log.info("当前文件已经存在,bucket：{}，key:{}", bucketPrefix + dateStr, file1.getName());
                        log.info("开启bucket：{}生命周期为ENABLED", bucketPrefix + dateStr);
                        BucketVersioningConfiguration bucketVersioningConfiguration =
                                new BucketVersioningConfiguration(BucketVersioningConfiguration.ENABLED);
                        SetBucketVersioningRequest setBucketVersioningRequest = new SetBucketVersioningRequest(
                                bucketPrefix + dateStr, bucketVersioningConfiguration);
                        VoidResult result = hos.setBucketVersioning(setBucketVersioningRequest);
                        if (result.getResponse().isSuccessful()) {
                            log.info("开启bucket:{}生命周期为ENABLED成功", bucketPrefix + dateStr);
                            // 组装对象，上传到对象存储中
                            uploadFile(bucketPrefix + dateStr, file1);
                            // 删除当前旧版本对象
                            ListVersionsRequest listVersionsRequest = new ListVersionsRequest()
                                    .withBucketName(bucketPrefix + dateStr)
                                    .withPrefix(file1.getName());
                            VersionListing versionListing = hos.listVersions(listVersionsRequest);
                            log.info("查询到file:{}含有的版本对象列表为：{}", file1.getName(), versionListing.getVersionSummaries().toArray());
                            //删除历史版本号的文件
                            List<HOSVersionSummary> collect = versionListing.getVersionSummaries()
                                    .stream()
                                    .filter(item -> !item.isLatest())
                                    .collect(Collectors.toList());
                            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketPrefix + dateStr);
                            deleteObjectsRequest.setKey(file1.getName());
                            for (HOSVersionSummary hosVersionSummary : collect) {
                                deleteObjectsRequest.setVersionId(hosVersionSummary.getVersionId());
                                VoidResult result1 = hos.deleteObject(deleteObjectsRequest);
                                if (result1.getResponse().isSuccessful()) {
                                    log.info("删除旧版本对象成功，bucketName:{},fileName:{},versionId:{}"
                                            , bucketPrefix + dateStr, file1.getName(), hosVersionSummary.getVersionId());
                                } else {
                                    log.error("删除旧版本对象失败，bucketName:{},fileName:{},versionId:{},err:{}"
                                            , bucketPrefix + dateStr, file1.getName(), hosVersionSummary.getVersionId(), result1.getResponse().getErrorResponseAsString());
                                }
                            }
                        } else {
                            throw new RuntimeException("开启版本管理失败：" + result.getResponse().getErrorResponseAsString());
                        }
                    } else {
                        // 代表当天首次上传
                        uploadFile(bucketPrefix + dateStr, file1);
                    }
                } catch (Exception e) {
                    log.error("上传文件发生异常，错误信息：" + e.getMessage());
                }
            }
            log.info("该文件file:{}备份耗时：{} ms", file1.getName(), System.currentTimeMillis() - start);
        }
        log.info("本次备份总耗时：{},文件总大小为：{} GB", (System.currentTimeMillis() - start0), totalSize / 1024 / 1024 / 1024);
        return true;
    }


    /**
     * 上传该路径下的文件
     * @param file
     */
    private void uploadFile(String bucket, File file) throws IOException {

        if (!file.exists() && !file.isFile()) {
            throw new FileNotFoundException(file.getAbsolutePath() + " not found.");
        }

        if (file.length() > fileLimt * 1024 * 1024) {
            // 断点续传
            log.info("断点续传：文件:{},大小：{},bucket:{}", file.getName(), file.length(), bucket);

            UploadObjectRequest uploadObjectRequest = new UploadObjectRequest(bucket, file.getName());
            // 通过UploadFileRequest设置单个参数。
            uploadObjectRequest.setUploadFile(file.getAbsolutePath());
            // 指定上传并发线程数，默认值为1。
            uploadObjectRequest.setTaskNum(threadNum);
            // 指定上传的分片大小，单位为字节，取值范围为100 KB~5 GB。默认值为100 KB。
            uploadObjectRequest.setPartSize(partSize * 1024 * 1024);
            // 开启断点续传，默认关闭。
            uploadObjectRequest.setEnableCheckpoint(true);
            // 断点续传上传。
            try {
                CompleteMultipartUploadResult uploadFileResult = hos.uploadFile(uploadObjectRequest);
                if (uploadFileResult.getResponse().isSuccessful()) {
                    log.info("断点续传成功：文件:{},大小：{},bucket:{}", file.getName(), file.length(), bucket);
                } else {
                    log.info("断点续传失败：文件:{},大小：{},bucket:{}，errorStr:{}", file.getName(), file.length(), bucket
                            , uploadFileResult.getResponse().getErrorResponseAsString());
                }
            } catch (Throwable throwable) {
                log.error("断点续传出现异常：文件:{},大小：{},bucket:{}，errorStr:{}", file.getName(), file.length(), bucket
                        , throwable.getMessage());
                throwable.printStackTrace();
                throw new RemoteException(throwable.getMessage());
            }
        } else {
            // 直接上传
            log.info("直接上传：文件:{},大小：{},bucket:{}", file.getName(), file.length(), bucket);
            PutObjectResult putObjectResult = hos.putObject(bucket, file.getName(), file);
            if (putObjectResult.getResponse().isSuccessful()) {
                log.info("直接上传成功,bucket:{},key:{},versionId:{}", bucket, file.getName(), putObjectResult.getVersionId());
            } else {
                log.error("直接上传成功,bucket:{},key:{},errorStr:{}", bucket, file.getName(), putObjectResult.getResponse().getErrorResponseAsString());
                throw new RemoteException(putObjectResult.getResponse().getErrorResponseAsString());
            }
        }
    }

    public Bucket getBucketInfo(String bucketName) {
        Bucket bucketInfo = hos.getBucketInfo(bucketName);
        return bucketInfo;
    }

    @Value("${expirationDays}")
    private int expirationDays;

    /**
     * 设置桶的生命周期
     * @param bucketName
     * @return
     */
    public Boolean setBucketLife(String bucketName) {

        // 设置桶的生命周期
        SetBucketLifecycleRequest setBucketLifecycleRequest = new SetBucketLifecycleRequest();
        setBucketLifecycleRequest.setBucketName(bucketName);
        LifecycleRule lifecycleRule = new LifecycleRule();
        lifecycleRule.setStatus(LifecycleRule.RuleStatus.Enabled);
        lifecycleRule.setFilter(new LifecycleRule.Filter());
        LifecycleRule.Expiration expiration = new LifecycleRule.Expiration(expirationDays);
        lifecycleRule.setExpiration(expiration);
        setBucketLifecycleRequest.getLifecycleRules().add(lifecycleRule);
        hos.setBucketLifecycle(setBucketLifecycleRequest);

        return true;
    }

    public Boolean upoadSingleObject(String fileName) throws Throwable {

        File file = new File(fileName);
        long l = file.length() / 150;

        UploadObjectRequest uploadObjectRequest = new UploadObjectRequest(bucketPrefix + "20240625", file.getName());
        // 通过UploadFileRequest设置单个参数。
        uploadObjectRequest.setUploadFile(file.getAbsolutePath());
        // 指定上传并发线程数，默认值为1。
        uploadObjectRequest.setTaskNum(threadNum);
        // 指定上传的分片大小，单位为字节，取值范围为100 KB~5 GB。默认值为100 KB。
        uploadObjectRequest.setPartSize(l);
        // 开启断点续传，默认关闭。
        uploadObjectRequest.setEnableCheckpoint(true);
        hos.uploadFile(uploadObjectRequest);
        return false;
    }
}
