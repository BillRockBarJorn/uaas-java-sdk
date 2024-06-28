package com.heredata.eics.task;

import com.alibaba.fastjson.JSON;
import com.heredata.eics.entity.MailDTO;
import com.heredata.eics.entity.MailEntity;
import com.heredata.hos.HOS;
import com.heredata.hos.model.*;
import com.heredata.hos.model.bucket.Bucket;
import com.heredata.hos.model.bucket.BucketList;
import com.heredata.hos.model.bucket.BucketVersioningConfiguration;
import com.heredata.model.VoidResult;
import com.heredata.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.URISyntaxException;
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
import java.util.stream.Collectors;

/**
 * 定时上传任务
 * @author wuzz
 * @since 2024/6/20
 */
@Slf4j
@Component
public class ScheduledUpload {

    @Value("${scannerPath}")
    private String scannerPath;

    /**
     * 是否删除源数据
     * true：上传完成后，会将系统的中的源文件删除
     */
    @Value("${isDeleteOrigin}")
    private boolean isDeleteOrigin;

    /**
     * 桶前缀
     * 每天生成一个桶   并且后缀为20240624
     */
    @Value("${bucketPrefix}")
    private String bucketPrefix;

    /**
     * 是否删除历史数据
     * true：上传完成后，会将以前（15天）日期中的改文件删除掉
     */
    @Value("${isDeleteHistory}")
    private boolean isDeleteHistory;

    @Value("${expirationDays}")
    private int expirationDays;

    @Resource
    private HOS hos;


    SimpleDateFormat yearMonthDay = new SimpleDateFormat("yyyyMMdd");

    SimpleDateFormat chineseFormat = new SimpleDateFormat("yyyy年MM月dd日");

    SimpleDateFormat chineseHMSFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");

    DecimalFormat df = new DecimalFormat("#.00");

    long startTime = System.currentTimeMillis();

    /**
     * 256GB  耗时1128s
     * @throws ParseException
     */
    @Scheduled(cron = "${cron}")
    public void upload() throws ParseException {
        long start0 = System.currentTimeMillis();
        startTime = start0;
        /**
         *组装今天的日期形式   如果有日期，采用参数的，如果没有组装今天的
         */
        String dateStr = "";
        // 获取目前的日期
        dateStr = DateUtil.formatIso8601Date(new Date());
        // 2024-06-20T08:45:22.009  ==> 2024-06-20
        dateStr = dateStr.substring(0, 10);
        // 2024-06-20==> 20240620
        dateStr = dateStr.replace("-", "");

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
        // 过滤出名字只含有dateStr的文件
        final String dateStr2 = dateStr;
        // 将本天的数据查询出来
        List<File> collect1 = Arrays.stream(files).filter(item -> item.getName().contains(dateStr2)).collect(Collectors.toList());
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
                        uploadFile(bucketPrefix + dateStr, file1, mailEntity);
                        totalSize += file1.length();
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
                    uploadFile(bucketPrefix + dateStr, file1, mailEntity);
                    totalSize += file1.length();
                    // 今天文件上传完成，是否删除以前（向前推15天）的文件同名文件，空间不多的情况下可以设置为true
                    if (isDeleteHistory) {
                        deleteHistory(convertFileName(file1.getName()), dateStr);
                    }
                }
            } catch (Exception e) {
                log.error("上传文件发生异常，错误信息：" + e.getMessage());
            } finally {
                ans.add(mailEntity);
            }
            log.info("该文件file:{}备份耗时：{} ms", file1.getName(), System.currentTimeMillis() - start);
        }
        double number = ((double) totalSize / (double) (1024 * 1024 * 1024));
        log.info("本次备份总耗时：{},文件总大小为：{} GB", (System.currentTimeMillis() - start0), df.format(number));
        // 开始组装邮件内容
        generateMailContentAndSend(ans);
    }

    @Value("${endPoint}")
    private String endPoint;
    @Value("${accountId}")
    private String accountId;

    @Value("${receptors}")
    private String receptors;


    /**
     * 组装邮件内容并且发送
     * @param ans
     */
    private void generateMailContentAndSend(List<MailEntity> ans) {
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.warn("获取主机ip地址错误");
            e.printStackTrace();
        }
        // 组装邮件内容
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(localHost + "EICS数据备份至对象存储\n\n");
        stringBuffer.append("基础信息如下：\n");
        stringBuffer.append("对象存储地址：").append(endPoint).append("\n");
        stringBuffer.append("租户id：").append(accountId).append("\n");
        stringBuffer.append("桶前缀：").append(bucketPrefix).append("\n\n");


        AccountInfo accountInfo = hos.getAccountInfo();
        stringBuffer.append("账户使用详情：").append('\n')
                .append("\t\t").append("桶数量：").append(accountInfo.getBucketCount()).append("\n")
                .append("\t\t").append("对象数量：").append(accountInfo.getObjCount()).append("\n")
                .append("\t\t").append("使用容量(字节)：").append(accountInfo.getBytesCount()).append("    ")
                .append("约").append(converseFileSize(accountInfo.getBytesCount())).append("\n");

        stringBuffer.append("\n");

        stringBuffer.append("桶基础信息如下：\n");
        List<Bucket> list = getBucketInfo();
        for (Bucket bucket : list) {
            stringBuffer.append("bucketName=").append(bucket.getBucketName()).append(",")
                    .append("used=").append(bucket.getBytesUsed()).append(",")
                    .append("aboutUsed=").append(converseFileSize(bucket.getBytesUsed()))
                    .append("\n");
        }

        stringBuffer.append("\n");

        String format = chineseFormat.format(new Date());

        stringBuffer.append(format).append("文件备份总结：").append("\n");
        stringBuffer.append("上传文件数量：").append(ans.size()).append("\n");
        int successCount = 0;
        long totalSize = 0;
        long time = 0;
        for (MailEntity an : ans) {
            if (an.isSuccess()) {
                successCount++;
                totalSize += an.getSize();
                time += an.getConsumerTime();
            }
        }
        stringBuffer.append("上传成功数量：").append(successCount).append("\n");
        stringBuffer.append("上传失败数量：").append(ans.size() - successCount).append("\n");
        stringBuffer.append("上传成功总大小（字节）：").append(totalSize).append("   ").append(converseFileSize(totalSize)).append("\n");
        stringBuffer.append("开始时间：").append(chineseHMSFormat.format(new Date(startTime))).append("\n");
        stringBuffer.append("结束时间：").append(chineseHMSFormat.format(new Date())).append("\n");
        stringBuffer.append("上传成功总耗时：").append(time).append("  ms").append('\n');


        stringBuffer.append("\n\n");

        stringBuffer.append("上传详情如下：").append("\n");
        for (MailEntity mailEntities : ans) {
            stringBuffer.append(mailEntities).append("\n");
        }
        stringBuffer.append("\n\n");


        MailDTO mailDTO = new MailDTO();
        // 设置邮件主题
        mailDTO.setSubject("EICS数据备份至对象存储");
        mailDTO.setCcRecipient(receptors.split(";"));
        mailDTO.setContent(stringBuffer.toString());

        Object o = JSON.toJSON(mailDTO);
        try {
            HttpPost httpPost = new HttpPost("http://172.18.232.195:9994/portal-alarm-service/api/mail");
            httpPost.setEntity(new StringEntity(o.toString(), StandardCharsets.UTF_8));
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept", "application/json");

            HttpClient client = HttpClients.createDefault();
            HttpResponse execute = client.execute(httpPost);
            if (execute.getStatusLine().getStatusCode() / 200 == 1) {
                log.info("发送邮件成功");
            }
        } catch (Exception e) {
            log.error("发送邮件失败");
        }

    }

    /**
     * 查询所有桶详情
     * @return
     */
    private List<Bucket> getBucketInfo() {
        BucketList bucketList = hos.listBuckets(bucketPrefix, null, null);
        List<Bucket> ans = new ArrayList<>();
        for (Bucket bucket : bucketList.getBuckets()) {
            ans.add(hos.getBucketInfo(bucket.getBucketName()));
        }
        return ans;
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
     * 删除小于dateStr的15天的数据历史文件
     * @param fileName
     * @param dateStr  20240624
     */
    private void deleteHistory(String fileName, String dateStr) {

        // 根据当前dataStr生成前15天的日期
        List<String> historyDates = generateHistoryDate(dateStr);

        for (String historyDate : historyDates) {
            // 删除以前日期中的同样的文件
            if (hos.doesBucketExist(bucketPrefix + historyDate)) {
                // 该日期的桶存在
                VersionListing versionListing = hos.listVersions(bucketPrefix + historyDate, fileName);
                log.info("查询结果。bucket:{},fileName:{},objectList:{}", bucketPrefix + historyDate, fileName
                        , versionListing.getVersionSummaries());

                // 进行删除操作
                versionListing.getVersionSummaries().forEach(item -> {
                    DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(item.getBucketName());
                    deleteObjectsRequest.setKey(item.getKey());
                    deleteObjectsRequest.setVersionId(item.getVersionId());
                    VoidResult result1 = hos.deleteObject(deleteObjectsRequest);
                    if (result1.getResponse().isSuccessful()) {
                        log.info("删除文件成功，bucket：{}，文件名：{}，versionId:{}", item.getBucketName(), item.getKey(), item.getVersionId());
                    } else {
                        log.error("删除文件失败，errorMsg:" + result1.getResponse().getErrorResponseAsString());
                    }
                });
            }
        }
    }

    /**
     * 生成当前日期的前15天数据
     * @param dateStr
     * @return
     */
    private List<String> generateHistoryDate(String dateStr) {
        List<String> ans = new ArrayList<>();
        long currentTimeMillis = 0;
        try {
            currentTimeMillis = yearMonthDay.parse(dateStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (int i = 1; i <= 15; i++) {
            String format = yearMonthDay.format(new Date(currentTimeMillis - 1000));
            ans.add(format);
            currentTimeMillis = currentTimeMillis - 1000 * 60 * 60 * 24;
        }
        return ans;
    }


    /**
     * 如果文件>100MB 就走断点续传，否则直接上传
     */
    @Value("${fileLimit}")
    private long fileLimt;

    /**
     * 默认上传和下载分片大小
     */
    @Value("${partSize}")
    private long partSize;

    /**
     * 断点续传和断点下载开启的线程数量
     */
    @Value("${threadNum}")
    private int threadNum;

    /**
     * 上传该路径下的文件
     * @param file
     * @param mailEntity
     */
    private void uploadFile(String bucket, File file, MailEntity mailEntity) throws IOException {
        if (!file.exists() && !file.isFile()) {
            throw new FileNotFoundException(file.getAbsolutePath() + " not found.");
        }
        int count = 1;
        long start0 = System.currentTimeMillis();
        while (count <= 3) {
            log.info("当前文件file:{},第 {} 次上传", file.getName(), count++);
            if (file.length() > fileLimt * 1024 * 1024) {
                // 断点续传
                log.info("断点续传：文件:{},大小：{},bucket:{}", file.getName(), file.length(), bucket);

                /**
                 * 由于底层分片太多，合并的时候会报错，而且底层目前无法通过修改配置文件进行调整，我这边就进行分片大小限制
                 */
                long newPartSize = -1;
                if (file.length() / (partSize * 1024 * 1024) > 150) {
                    // 如果该文件的分片大于200片，就不行，需要限制在200片
                    log.info("该文件太大，需要调整切片，按照150片上传该文件");
                    newPartSize = file.length() / 200;
                }

                UploadObjectRequest uploadObjectRequest = new UploadObjectRequest(bucket, file.getName());
                // 通过UploadFileRequest设置单个参数。
                uploadObjectRequest.setUploadFile(file.getAbsolutePath());
                // 指定上传并发线程数，默认值为1。
                uploadObjectRequest.setTaskNum(threadNum);
                // 指定上传的分片大小，单位为字节，取值范围为100 KB~5 GB。默认值为100 KB。
                uploadObjectRequest.setPartSize(newPartSize != -1 ? newPartSize : (partSize * 1024 * 1024));
                // 开启断点续传，默认关闭。
                uploadObjectRequest.setEnableCheckpoint(true);
                // 断点续传上传。
                try {
                    CompleteMultipartUploadResult uploadFileResult = hos.uploadFile(uploadObjectRequest);
                    if (uploadFileResult.getResponse().isSuccessful()) {
                        log.info("断点续传成功：文件:{},大小：{},bucket:{}", file.getName(), file.length(), bucket);
                        mailEntity.setCount(count - 1);
                        mailEntity.setSuccess(true);
                        mailEntity.setConsumerTime(System.currentTimeMillis() - start0);
                        return;
                    } else {
                        log.info("断点续传失败：文件:{},大小：{},bucket:{}，errorStr:{}", file.getName(), file.length(), bucket
                                , uploadFileResult.getResponse().getErrorResponseAsString());
                    }
                } catch (Throwable throwable) {
                    log.error("断点续传出现异常：文件:{},大小：{},bucket:{}，errorStr:{}", file.getName(), file.length(), bucket
                            , throwable.getMessage());
                }
            } else {
                // 直接上传
                log.info("直接上传：文件:{},大小：{},bucket:{}", file.getName(), file.length(), bucket);
                try {
                    PutObjectResult putObjectResult = hos.putObject(bucket, file.getName(), file);
                    if (putObjectResult.getResponse().isSuccessful()) {
                        log.info("直接上传成功,bucket:{},key:{},versionId:{}", bucket, file.getName(), putObjectResult.getVersionId());
                        mailEntity.setCount(count - 1);
                        mailEntity.setSuccess(true);
                        mailEntity.setConsumerTime(System.currentTimeMillis() - start0);
                        return;
                    } else {
                        log.error("直接上传失败,bucket:{},key:{},errorStr:{}", bucket, file.getName(), putObjectResult.getResponse().getErrorResponseAsString());
                        throw new RemoteException(putObjectResult.getResponse().getErrorResponseAsString());
                    }
                } catch (Exception e) {
                    log.error("直接上传失败,bucket:{},key:{},errorStr:{}", bucket, file.getName(), e.getMessage());
                }
            }
        }
        mailEntity.setSuccess(false);
        mailEntity.setCount(count - 1);
    }

    /**
     * 提取出文件名称
     * 172.18.238.104_3317_crm220240623_11:00.sql  ==》  172.18.238.104_3317_crm2
     * @param name
     * @return
     */
    private String convertFileName(String name) {
        /**
         * myyule_account20240620_02:34.sql
         * easy_take88020240620_02:34.sql
         * publicplatform20240620_02:34.sql
         * UMOB_APP20240620_02:32.sql
         */
        // 定位最后一个"_"
        int lastChar = name.lastIndexOf('_');
        // 从0位置截取到lastChar-8
        return name.substring(0, lastChar - 8);
    }

    public static void main(String[] args) throws URISyntaxException, IOException {

        System.out.println(InetAddress.getLocalHost());


        ScheduledUpload scheduledUpload = new ScheduledUpload();
        System.out.println(scheduledUpload.convertFileName("172.18.238.104_3317_crm220240623_11:00.sql"));
        System.out.println(scheduledUpload.convertFileName("easy_take88020240620_02:34.sql"));
        List<String> list = scheduledUpload.generateHistoryDate("20240614");
        System.out.println("aaaaa");


        MailDTO mailDTO = new MailDTO();
        String[] strs = new String[]{"wuzz@heredata.com.cn"};
        mailDTO.setSubject("codeTest");
        mailDTO.setCcRecipient(strs);
        mailDTO.setContent("codetest");

        Object o = JSON.toJSON(mailDTO);
//
//        JSONObject object = new JSONObject();
//        object.put("subject", "codeTest");
//        object.put("content", "codeTest");
//        object.put("ccRecipient", strs);

//        HttpPost httpPost = new HttpPost("http://172.18.232.195:9994/portal-alarm-service/api/mail");
//        httpPost.setEntity(new StringEntity(o.toString()));
//        httpPost.setHeader("Content-Type", "application/json");
//        httpPost.setHeader("Accept", "application/json");
//
//        HttpClient client = HttpClients.createDefault();
//        HttpResponse execute = client.execute(httpPost);
//        System.out.println(execute.getStatusLine());


        String filePath = "aaa.txt"; // 指定文件路径
        long fileSizeInBytes = 1024 * 1024 * 1024; // 指定文件大小，这里为 1MB
        try {
            // 创建文件对象
            File file = new File(filePath);
            // 创建 RandomAccessFile 对象并设置文件大小
            RandomAccessFile raf = new RandomAccessFile(file, "rw");  //rw代表给这个文件读写权限
            raf.setLength(fileSizeInBytes);
            // 关闭 RandomAccessFile
            raf.close();
            System.out.println("文件创建成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
