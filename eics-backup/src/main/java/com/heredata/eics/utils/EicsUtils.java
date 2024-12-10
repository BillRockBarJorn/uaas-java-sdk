package com.heredata.eics.utils;

import com.alibaba.fastjson.JSON;
import com.heredata.eics.entity.MailDTO;
import com.heredata.eics.entity.MailEntity;
import com.heredata.eics.service.Service;
import com.heredata.hos.HOS;
import com.heredata.hos.model.AccountInfo;
import com.heredata.hos.model.CompleteMultipartUploadResult;
import com.heredata.hos.model.PutObjectResult;
import com.heredata.hos.model.UploadObjectRequest;
import com.heredata.hos.model.bucket.Bucket;
import com.heredata.hos.model.bucket.BucketList;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EicsUtils {


    /**
     * yyyyMMdd
     */
    public static final SimpleDateFormat format_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

    /**
     * yyyy_MM_dd
     */
    public static final SimpleDateFormat format_yyyy_MM_dd = new SimpleDateFormat("yyyy_MM_dd");

    /**
     * yyyy年MM月dd日
     */
    public static final SimpleDateFormat format_chinese_yyyyMMdd = new SimpleDateFormat("yyyy年MM月dd日");

    /**
     * yyyy年MM月dd日 HH时mm分ss秒
     */
    public static final SimpleDateFormat format_chinese_yyyyMMddHHmmss = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");

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

    @Value("${expirationDays}")
    private int expirationDays;

    @Resource
    private HOS hos;

    /**
     * 上传该路径下的文件
     * @param file
     * @param mailEntity
     */
    public boolean uploadFile(String bucket, File file, MailEntity mailEntity) throws IOException {
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
                if ((file.length() / (partSize * 1024 * 1024)) > 150) {
                    // 281GB    100*1024*1024*150   15000MB/1024  14G
                    // 如果该文件的分片大于200片，就不行，需要限制在200片
                    newPartSize = file.length() / 200;
                    log.info("该文件太大，需要调整切片，按照200片上传该文件，调整后每片newPartSize={}", newPartSize);
                }

                if (newPartSize > (long) (1024L * 1024L * 1024L * 3.5D)) {
                    log.info("该文件每片大小超过5G，按照5G每片进行切片   newPartSize= {}", (1024L * 1024L * 1024L * 3.5D));
                    newPartSize = (long) (1024L * 1024L * 1024L * 3.5D);
                    log.info("该文件每片大小超过5G，按照5G每片进行切片   newPartSize= {}", newPartSize);
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
                        return true;
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
                    }
                } catch (Exception e) {
                    log.error("直接上传失败,bucket:{},key:{},errorStr:{}", bucket, file.getName(), e.getMessage());
                }
                return true;
            }
        }
        mailEntity.setSuccess(false);
        mailEntity.setCount(count - 1);
        return false;
    }

    @Value("${endPoint}")
    private String endPoint;
    @Value("${accountId}")
    private String accountId;

    @Value("${receptors}")
    private String receptors;

    @Value("${mailSubject}")
    private String mailSubject;

    /**
     * 组装邮件内容并且发送
     * @param ans
     */
    public void generateMailContentAndSend(List<MailEntity> ans) {

        String subject = unicodeToCN(mailSubject);

        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.warn("获取主机ip地址错误");
            e.printStackTrace();
        }
        // 组装邮件内容
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(localHost + subject + "\n\n");
        stringBuffer.append("基础信息如下：\n");
        stringBuffer.append("对象存储地址：").append(endPoint).append("\n");
        stringBuffer.append("租户id：").append(accountId).append("\n");
        stringBuffer.append("桶前缀：").append(bucketPrefix).append("\n\n");

        // 先获取桶信息，将桶中的size为0的桶进行删除处理，这样账户信息的桶数量才准确
        List<Bucket> list = getBucketInfo();
        // 进行降序排序
        list.sort(new Comparator<Bucket>() {
            @Override
            public int compare(Bucket o1, Bucket o2) {
                return Long.compare(o2.getCreationDate().getTime(), o1.getCreationDate().getTime());
            }
        });

        // 获取账户信息
        AccountInfo accountInfo = hos.getAccountInfo();
        stringBuffer.append("账户使用详情：").append('\n')
                .append("\t\t").append("桶数量：").append(accountInfo.getBucketCount()).append("\n")
                .append("\t\t").append("对象数量：").append(accountInfo.getObjCount()).append("\n")
                .append("\t\t").append("使用容量(字节)：").append(accountInfo.getBytesCount()).append("    ")
                .append("约").append(converseFileSize(accountInfo.getBytesCount())).append("\n");

        stringBuffer.append("\n");

        stringBuffer.append("桶基础信息如下：\n");
        for (Bucket bucket : list) {
            stringBuffer.append("bucketName=").append(bucket.getBucketName()).append(",")
                    .append("used=").append(bucket.getBytesUsed()).append(",")
                    .append("aboutUsed=").append(converseFileSize(bucket.getBytesUsed()))
                    .append("\n");
        }

        stringBuffer.append("\n");

        String format = EicsUtils.format_chinese_yyyyMMdd.format(new Date());

        stringBuffer.append(format).append("文件备份总结：").append("\n");
        stringBuffer.append("上传文件数量：").append(ans.size()).append("\n");
        int successCount = 0;
        long totalSize = 0;
        long time = 0;

        // 对ans进行排序，耗时降序排序
        ans.sort(new Comparator<MailEntity>() {
            @Override
            public int compare(MailEntity o1, MailEntity o2) {
                return Long.compare(o2.getConsumerTime(), o1.getConsumerTime());
            }
        });

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
        stringBuffer.append("结束时间：").append(EicsUtils.format_chinese_yyyyMMddHHmmss.format(new Date())).append("\n");
        stringBuffer.append("上传成功总耗时：").append(time).append("  ms").append('\n');


        stringBuffer.append("\n\n");

        stringBuffer.append("上传详情如下：").append("\n");
        for (MailEntity mailEntities : ans) {
            stringBuffer.append(mailEntities).append("\n");
        }
        stringBuffer.append("\n\n");


        MailDTO mailDTO = new MailDTO();
        // 设置邮件主题
        mailDTO.setSubject(subject);
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
                log.info("调用发送邮件接口成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送邮件失败");
        }

    }

    /**
     * 桶前缀
     * 每天生成一个桶   并且后缀为20240624
     */
    @Value("${bucketPrefix}")
    private String bucketPrefix;

    @Resource
    private Service service;

    /**
     * 查询所有桶详情
     * @return
     */
    private List<Bucket> getBucketInfo() {
        BucketList bucketList = hos.listBuckets(bucketPrefix, null, null);
        List<Bucket> ans = new ArrayList<>();
        for (Bucket bucket : bucketList.getBuckets()) {
            log.info("桶详细信息如下:" + bucket.toString());
            bucket = hos.getBucketInfo(bucket.getBucketName());
            if (bucket.getBytesUsed() == 0
                    && (System.currentTimeMillis() - bucket.getCreationDate().getTime()) > (1000 * 60 * 60 * 24 * (expirationDays + 1))) {
                // 该分支表示，桶使用大小为0，并且已经过期，就进行删除桶操作
                while (hos.doesBucketExist(bucket.getBucketName())) {
                    try {
                        service.deleteBucket(bucket.getBucketName(), true);
                        log.info("删除桶   " + bucket.getBucketName() + "  成功");
                        break;
                    } catch (Exception e) {
                        log.warn("桶删除失败,休息3S:" + bucket.toString());
                        try {
                            TimeUnit.SECONDS.sleep(3);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }
                }
            } else {
                ans.add(hos.getBucketInfo(bucket.getBucketName()));
            }
//            ans.add(hos.getBucketInfo(bucket.getBucketName()));
        }
        return ans;
    }

    /**
     * 提取出文件名称
     * 172.18.238.104_3317_crm220240623_11:00.sql  ==》  172.18.238.104_3317_crm2
     * @param name
     * @return
     */
    public static String convertFileName(String name) {
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


    public static String converseFileSize(Long size) {
        BigDecimal bigDecimal = new BigDecimal(size);
        String[] arr = new String[]{"B", "KB", "MB", "GB", "TB"};
        int index = 0;
        while (bigDecimal.longValue() > 1024) {
            bigDecimal = bigDecimal.divide(new BigDecimal(1024));
            index++;
        }
        return bigDecimal.setScale(4, RoundingMode.HALF_UP) + arr[index];
    }

    public static String unicodeToCN(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }

    public static void main(String[] args) {
        String str = "gitlab\\u5907\\u4efd\\u81f3\\u5bf9\\u8c61\\u5b58\\u50a8";
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        System.out.println(str);

        List<MailEntity> list = new ArrayList<>();
        MailEntity mailEntity = new MailEntity();
        mailEntity.setFileName("aaaa");
        mailEntity.setConsumerTime(100L);

        list.add(mailEntity);

        mailEntity = new MailEntity();
        mailEntity.setFileName("bbb");
        mailEntity.setConsumerTime(200L);

        list.add(mailEntity);

        list.sort(new Comparator<MailEntity>() {
            @Override
            public int compare(MailEntity o1, MailEntity o2) {
                return Long.compare(o2.getConsumerTime(), o1.getConsumerTime());
            }
        });


        String aa = "172.18.238.103_backup_3319_202407020400.xbstream.gz";

        int i = aa.lastIndexOf("_");
        System.out.println(aa.substring(i + 1, i + 9));
    }

}
