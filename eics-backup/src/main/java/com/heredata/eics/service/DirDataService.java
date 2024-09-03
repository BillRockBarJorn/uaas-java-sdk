package com.heredata.eics.service;

import com.google.common.collect.Lists;
import com.heredata.eics.config.Thread.AsyncConfig;
import com.heredata.eics.entity.MailEntity;
import com.heredata.eics.utils.EicsUtils;
import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import com.heredata.hos.HOS;
import com.sitech.cmap.fw.core.common.EmptyValidator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@Component
public class DirDataService {

    @Value("${scannerPath}")
    private String scannerPath;

    @Resource
    private HOS hos;

    @Value("${bucket}")
    private String bucketName;

    @Resource
    AsyncConfig asyncConfig;

    @Resource
    EicsUtils eicsUtils;

    DecimalFormat df = new DecimalFormat("#.00");




    @SneakyThrows
    public  void  fullFir(){

        TimeUnit.SECONDS.sleep(10);
        // 创建Scanner对象
        Scanner scanner = new Scanner(System.in);

        System.out.println("备份目录: "+scannerPath+",是否需要进行一次全量备份(yes/no):");
        // 读取一行输入
        String input = scanner.nextLine();
        System.out.println("您输入的内容是：" + input);
        if ("yes".equalsIgnoreCase(input)){
            //文件上传(异步，大文件分段上传)
            log.info("=============全量备份开始=============");
            uploadFull();
        }

    }

    public  void  bucketInfo(String bucketName){
        Boolean isExist = false;
        // 先查询桶是否存在,不存在就创建，对接备份平台 当前用户只有一个桶
        if (!hos.doesBucketExist(bucketName)) {
            try {
                hos.createBucket(bucketName);
                isExist = true;
            } catch (Exception e) {
                log.error("桶创建失败:{}", bucketName);
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }

    }

    public void uploadFull(){
        //bucket判断是否存在，不存在就创建
         bucketInfo(bucketName);
            File[] files = new File(scannerPath).listFiles();
            if (files !=null){
                log.info("执行上传任务,扫描路径为:" + scannerPath+",文件数量:"+files.length);
                //目录首次全量备份
                asyload(bucketName, Arrays.asList(files),"upload");
            }else{
                log.info("备份目录为空，无文件");
            }

    }


    public  boolean asyload(String bucketName, List<File> objectSummaries, String load){
        Boolean res=false;
        if (EmptyValidator.isNotEmpty(objectSummaries)) {
            try {
                //线程池开启
                Executor executor = asyncConfig.taskExecutor();
                //文件集合，为下步执行
                List<MailEntity> totalFile = new ArrayList<>();

                if (objectSummaries.size() > 1000) {
                    List<List<File>> splitNList = Lists.partition(objectSummaries, 1000);
                    log.info("切割之后的大小:" + splitNList.size());
                    List<CompletableFuture> futureList = new ArrayList<>();
                    //计数器，初始值为切割的大小
                    CountDownLatch latch = new CountDownLatch(splitNList.size());

                    for (List<File> summaries : splitNList) {
                        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                            List<MailEntity> files=null;
                            if ("upload".equals(load)) {
                                files= uploadlcoal(summaries,bucketName);
                            }
                            //文件信息组装
                            totalFile.addAll(files);
                            //计数器-1
                            latch.countDown();
                            return "";
                        }, executor);
                        futureList.add(future);
                    }
                    //各个线程执行完
                    CompletableFuture.allOf(futureList.toArray(new CompletableFuture[splitNList.size()])).join();
                    //等待所有线程执行完毕(当计数器为0才释放)
                    latch.await();
                }else{
                    totalFile.addAll(uploadlcoal(objectSummaries,bucketName));
                }
                // 开始组装邮件内容
                eicsUtils.generateMailContentAndSend(totalFile);
            } catch (ServiceException oe) {
                log.error("Error Message:" + oe.getErrorMessage());
            } catch (ClientException ce) {
                log.error("Error Message:" + ce.getMessage());
            } catch (InterruptedException e) {
                log.error("Error Message:" + e.getMessage());
            }
        }
        return res;
    }


    public   List<MailEntity>    uploadlcoal(List<File> files,String bucketName ){
        long startTime = System.currentTimeMillis();
        // 每上传一个文件就将上传的大小加到该变量中
        long totalSize = 0;
        List<MailEntity> ans = new ArrayList<>();
        // 遍历需要上传的文件集合
        for (File file1 : files) {
            log.info("扫描到文件：" + file1.toString()+",开始上传");
            long start = System.currentTimeMillis();
            MailEntity mailEntity = new MailEntity();
            mailEntity.setBucketName(bucketName);
            mailEntity.setFileName(file1.getName());
            mailEntity.setSize(file1.length());
            try {
                // 代表当天首次上传
                eicsUtils.uploadFile(bucketName, file1, mailEntity);
                totalSize += file1.length();
            } catch (Exception e) {
                log.error("上传文件发生异常，错误信息：" + e.getMessage());
            } finally {
                ans.add(mailEntity);
            }
        }
        double number = ((double) totalSize / (double) (1024 * 1024 * 1024));
        log.info("本次备份总耗时：{},文件总大小为：{} GB", (System.currentTimeMillis() - startTime), df.format(number));
        return ans;
    }
}
