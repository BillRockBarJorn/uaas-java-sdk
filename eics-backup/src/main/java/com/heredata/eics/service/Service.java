package com.heredata.eics.service;

import com.alibaba.fastjson.JSON;
import com.heredata.eics.entity.MailDTO;
import com.heredata.eics.entity.MailEntity;
import com.heredata.eics.task.ScheduledUpload;
import com.heredata.eics.utils.EicsUtils;
import com.heredata.exception.ServiceException;
import com.heredata.hos.HOS;
import com.heredata.hos.model.*;
import com.heredata.hos.model.bucket.Bucket;
import com.heredata.hos.model.bucket.BucketList;
import com.heredata.hos.model.bucket.BucketVersioningConfiguration;
import com.heredata.model.VoidResult;
import com.heredata.swift.model.DownloadFileRequest;
import com.heredata.utils.StringUtils;
import com.sitech.cmap.fw.core.wsg.WsgPageResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.heredata.eics.utils.EicsUtils.converseFileSize;

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
     * 获取对象列表
     * 按照目前的推理来看  一天150~200个文件，
     * 如果需要保存15天，那么2250~3000个文件，
     *
     * @param bucketName
     * @param fileName
     * @param date
     * @return
     */
    public WsgPageResult<List<HOSVersionSummary>> objectList(String bucketName, String fileName, String date, int pageSize, int curPage) {

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
        int i = fileName.lastIndexOf("_");
        String date = "";
        if (fileName.contains(".xbstream.gz")) {
            date = fileName.substring(i + 1, i + 9);
        } else {
            date = fileName.substring(i - 8, i);
        }
        bucketName = bucketPrefix + date;
        try {
            VersionListing versionListing = hos.listVersions(bucketName, fileName);
            if (versionListing == null || versionListing.getVersionSummaries() == null || versionListing.getVersionSummaries().isEmpty()) {
                throw new RuntimeException("bucketName:" + bucketName + " fileName:" + fileName + "not find");
            }
            HOSVersionSummary hosVersionSummary = null;
            for (HOSVersionSummary versionSummary : versionListing.getVersionSummaries()) {
                if (versionSummary.isLatest()) {
                    hosVersionSummary = versionSummary;
                    break;
                }
            }
            DownloadFileRequest downloadFileRequest = new DownloadFileRequest(hosVersionSummary.getBucketName(), hosVersionSummary.getKey());
            downloadFileRequest.setVersionId(hosVersionSummary.getVersionId());
            // 下载本地的文件
            downloadFileRequest.setDownloadFile(filePath);
            if (hosVersionSummary.getSize() > fileLimt * 1024 * 1024) {
                // 走断点下载
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
            log.info("bucket:{},object:{},fileSize:{},耗时:{} ms", bucketName, hosVersionSummary.getKey()
                    , converseFileSize(hosVersionSummary.getSize()), System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("bucket:{},object:{},error:{}", bucketName, fileName, e.getMessage());
        } catch (Throwable throwable) {
            log.error("bucket:{},object:{},error:{}", bucketName, fileName, throwable.getMessage());
        }
        return true;
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

    DecimalFormat df = new DecimalFormat("#.00");

    @Resource
    EicsUtils eicsUtils;

    /**
     * 上传指定日期到对象存储中
     * @param date
     * @return
     */
    public Boolean upload(String date) {
        startTime = System.currentTimeMillis();
        /**
         *组装今天的日期形式   如果有日期，采用参数的，如果没有组装今天的
         */
        // 获取目前的日期
        String dateStr = date;

        for (int i = 0; i < 3; i++) {
            // 先查询桶是否存在
            if (!hos.doesBucketExist(bucketPrefix + dateStr)) {
                try {
                    hos.createBucket(bucketPrefix + dateStr);
                    // 设置桶的生命周期
                    SetBucketLifecycleRequest setBucketLifecycleRequest = new SetBucketLifecycleRequest();
                    LifecycleRule lifecycleRule = new LifecycleRule();
                    lifecycleRule.setStatus(LifecycleRule.RuleStatus.Enabled);
                    LifecycleRule.Filter filter = new LifecycleRule.Filter();
                    filter.setPrefix("%");
                    lifecycleRule.setFilter(filter);
                    LifecycleRule.Expiration expiration = new LifecycleRule.Expiration(expirationDays);
                    lifecycleRule.setExpiration(expiration);
                    setBucketLifecycleRequest.getLifecycleRules().add(lifecycleRule);
                    setBucketLifecycleRequest.setBucketName(bucketPrefix + dateStr);
                    hos.setBucketLifecycle(setBucketLifecycleRequest);
                    break;
                } catch (Exception e) {
                    log.error("桶创建失败:{}", bucketPrefix + dateStr);
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    continue;
                }
            }
        }

        /**
         * 遍历目录进行上传
         */
        log.info("执行上传任务,扫描路径为：" + scannerPath);
        File file = new File(scannerPath);
        File[] files = file.listFiles();
        // 过滤出名字只含有dateStr的文件
        // 将本天的数据查询出来
        List<File> collect1 = Arrays.stream(files).filter(item -> {
            long l = item.lastModified();
            String format = EicsUtils.format_yyyyMMdd.format(new Date(l));
            log.info("format:{},dateStr:{},fileName:{}", format, dateStr, item.getName());
            if (dateStr.equals(format) && item.getName().contains(dateStr)) {
                return true;
            } else {
                return false;
            }
        }).collect(Collectors.toList());
        // 每上传一个文件就将上传的大小加到该变量中
        long totalSize = 0;
        // 邮件实体内容
        List<MailEntity> ans = new ArrayList<>();
        // 遍历需要上传的文件集合
        for (File file1 : collect1) {
            log.info("扫描到文件：" + file1.toString());
            log.info("开始上传=====================================================================================================================");
            long start = System.currentTimeMillis();
            MailEntity mailEntity = new MailEntity();
            mailEntity.setBucketName(bucketPrefix + dateStr);
            mailEntity.setFileName(file1.getName());
            mailEntity.setSize(file1.length());
            try {
                // 代表当天首次上传
                eicsUtils.uploadFile(bucketPrefix + dateStr, file1, mailEntity);
                totalSize += file1.length();
            } catch (Exception e) {
                log.error("上传文件发生异常，错误信息：" + e.getMessage());
            } finally {
                ans.add(mailEntity);
            }
            log.info("该文件file:{}备份耗时：{} ms", file1.getName(), System.currentTimeMillis() - start);
        }
        double number = ((double) totalSize / (double) (1024 * 1024 * 1024));
        log.info("本次备份总耗时：{},文件总大小为：{} GB", (System.currentTimeMillis() - startTime), df.format(number));
        // 开始组装邮件内容
        eicsUtils.generateMailContentAndSend(ans);
        return true;
    }

    long startTime = System.currentTimeMillis();

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

    public List<HOSVersionSummary> objectList(String fileName, String startTime, String endTime, String orderCondition) {
        // 将所有数据查询出来
        BucketList bucketList = hos.listBuckets(new ListBucketsRequest().withPrefix(bucketPrefix));
        List<HOSVersionSummary> ans = new ArrayList<>();
        for (Bucket bucket : bucketList.getBuckets()) {
            VersionListing versionListing = hos.listVersions(bucket.getBucketName(), null);
            ans.addAll(versionListing.getVersionSummaries());
        }

        // 进行筛选过滤
        // 名称过滤
        if (!StringUtils.isNullOrEmpty(fileName)) {
            ans = ans.stream().filter(item -> item.getKey().contains(fileName)).collect(Collectors.toList());
        }

        // startTime时间过滤
        if (!StringUtils.isNullOrEmpty(startTime)) {
            try {
                Date parse = EicsUtils.format_yyyyMMdd.parse(startTime);
                ans = ans.stream().filter(item -> item.getLastModified().getTime() >= parse.getTime()).collect(Collectors.toList());
            } catch (ParseException e) {
                throw new RuntimeException("startTime格式错误，请遵循\"20240501\"");
            }
        }

        // endTime时间过滤
        if (!StringUtils.isNullOrEmpty(endTime)) {
            try {
                Date parse = EicsUtils.format_yyyyMMdd.parse(endTime);
                ans = ans.stream().filter(item -> item.getLastModified().getTime() <= parse.getTime()).collect(Collectors.toList());
            } catch (ParseException e) {
                throw new RuntimeException("startTime格式错误，请遵循\"20240501\"");
            }
        }
//        // 因为有一些文件是含有旧版本文件，此时再过滤一层取到最新文件
//        ans = ans.stream().filter(item -> item.isLatest()).collect(Collectors.toList());
        return ans;
    }

    public boolean deleteObject(String bucketName, String key, String versionID) {
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName);
        deleteObjectsRequest.setKey(key);
        deleteObjectsRequest.setVersionId(versionID);
        hos.deleteObject(deleteObjectsRequest);
        return false;
    }

    /**
     * 就出旧版本的对象
     * @param bucketName
     * @return
     */
    public boolean deleteOldVersion(String bucketName) {
        VersionListing versionListing = hos.listVersions(bucketName, null);
        for (HOSVersionSummary versionSummary : versionListing.getVersionSummaries()) {
            log.info("bucketName:{}  key:{}  fileSize:{}  isLast:{}"
                    , versionSummary.getBucketName(), versionSummary.getKey(), versionSummary.getSize(), versionSummary.isLatest());
            if (versionSummary.getSize() == 0 && versionSummary.isLatest()) {
                DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(versionSummary.getBucketName());
                deleteObjectsRequest.setKey(versionSummary.getKey());
                deleteObjectsRequest.setVersionId(versionSummary.getVersionId());
                hos.deleteObject(deleteObjectsRequest);
            }
        }
        return false;
    }
}
