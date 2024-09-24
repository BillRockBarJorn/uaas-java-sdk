package com.heredata.eics.task;

import com.alibaba.fastjson.JSON;
import com.heredata.eics.entity.MailDTO;
import com.heredata.eics.entity.MailEntity;
import com.heredata.eics.utils.EicsUtils;
import com.heredata.hos.HOS;
import com.heredata.hos.model.*;
import com.heredata.hos.model.bucket.BucketVersioningConfiguration;
import com.heredata.model.VoidResult;
import com.heredata.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.heredata.eics.utils.EicsUtils.convertFileName;


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

    @Value("${isStartAutoUpload:false}")
    private boolean isStartAutoUpload;

    @Value("${isSendMail:false}")
    private boolean isSendMail;

    @Value("${expirationDays}")
    private int expirationDays;

    @Resource
    private HOS hos;


    DecimalFormat df = new DecimalFormat("#.00");

    @Resource
    EicsUtils eicsUtils;

    long startTime = System.currentTimeMillis();

    /**
     * 256GB  耗时1128s
     * @throws ParseException
     */
    @Scheduled(cron = "${cron}")
    public void upload() throws ParseException {
        // 如果不开启自动定时任务，直接返回，开启是true，不开启是false
        if (!isStartAutoUpload) return;
        long start0 = System.currentTimeMillis();
        startTime = start0;
        /**
         *组装今天的日期形式   如果有日期，采用参数的，如果没有组装今天的
         */
        // 获取目前的日期
        String dateStr = EicsUtils.format_yyyyMMdd.format(new Date());

        for (int i = 0; i < 3; i++) {
            // 先查询桶是否存在
            if (!hos.doesBucketExist(bucketPrefix + dateStr)) {
                try {
                    // 创建桶
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
                    // 说明有其他服务创建了该桶，存在并发创建，如果其他服务成功创建，当前服务进行下一次循环将不走创建桶的逻辑
                    log.error("桶创建失败:{}", bucketPrefix + dateStr);
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    continue;
                }
            } else break;
        }

        /**
         * 遍历目录进行上传
         */
        log.info("执行上传任务,扫描路径为：" + scannerPath);
        File file = new File(scannerPath);
        File[] files = file.listFiles();

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
            log.info("扫描到文件：{}，文件大小:{}", file1.toString(), file1.getTotalSpace());
            log.info("开始上传=====================================================================================================================");
            long start = System.currentTimeMillis();
            MailEntity mailEntity = new MailEntity();
            mailEntity.setBucketName(bucketPrefix + dateStr);
            mailEntity.setFileName(file1.getName());
            mailEntity.setSize(file1.length());
            try {
                boolean isSuccess = eicsUtils.uploadFile(bucketPrefix + dateStr, file1, mailEntity);
                totalSize += file1.length();
            } catch (Exception e) {
                log.error("上传文件发生异常，错误信息：" + e.getMessage());
            } finally {
                ans.add(mailEntity);
            }
            log.info("该文件file:{}备份耗时：{} ms", file1.getName(), System.currentTimeMillis() - start);
            log.info("上传完成=====================================================================================================================");
        }
        double number = ((double) totalSize / (double) (1024 * 1024 * 1024));
        log.info("本次备份总耗时：{},文件总大小为：{} GB", (System.currentTimeMillis() - start0), df.format(number));
        // 如果不需要发送邮件，直接return
        if (!isSendMail) return;
        // 开始组装邮件内容
        eicsUtils.generateMailContentAndSend(ans);
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
            currentTimeMillis = EicsUtils.format_yyyyMMdd.parse(dateStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (int i = 1; i <= 15; i++) {
            String format = EicsUtils.format_yyyyMMdd.format(new Date(currentTimeMillis - 1000));
            ans.add(format);
            currentTimeMillis = currentTimeMillis - 1000 * 60 * 60 * 24;
        }
        return ans;
    }


    public boolean isTaskRunning() {
        return isTaskRunning();
    }


    public static void main(String[] args) throws URISyntaxException, IOException {

        System.out.println(EicsUtils.format_yyyyMMdd.format(new Date()));


        System.out.println(DateUtil.formatIso8601Date(new Date()));


        System.out.println(InetAddress.getLocalHost());


        ScheduledUpload scheduledUpload = new ScheduledUpload();
        System.out.println(convertFileName("172.18.238.104_3317_crm220240623_11:00.sql"));
        System.out.println(convertFileName("easy_take88020240620_02:34.sql"));
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
