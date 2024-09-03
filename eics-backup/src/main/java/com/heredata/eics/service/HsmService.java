package com.heredata.eics.service;

import com.heredata.eics.entity.oss.TbSwiftBackupFileTree;
import com.heredata.eics.utils.HosClient;
import com.heredata.eics.utils.SwOSSClient;
import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import com.heredata.swift.Swift;
import com.heredata.swift.SwiftClientBuilder;
import com.heredata.swift.model.*;
import com.heredata.swift.model.bucket.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@org.springframework.stereotype.Service
@Slf4j
public class HsmService extends HosClient {

    @Value("${scannerPath}")
    private String scannerPath;

    @Value("${fileThread}")
    private String fileThread;

    private static String ak="RU7WSH6Y9HAFCYNIX0QT";


    private static String sk="6e9ZSwqLIRT9YvOQhe6s1aUA6cAIXDAYFVUAastJ";

    private static String TaendPoint="http://10.86.8.201:27741/v1/";

    private static String accountId="c834866e4b3b11ef90eca9e93fc69fac";


      /**
     * *
     *@Title  dataHier
     *@Description   桶集数据迁移
     *@param [bucketName]
     *@return boolean
     *@creator dingrb
     *@creatTime  2024/7/24 16:26
     *@version 1.0
     */
    public boolean dataHier(String bucketName) throws Throwable {

        KeyInformation originInfo=SwOSSClient.originIden();
        Swift oriObj = new SwiftClientBuilder().build(originInfo.getEndPoint(), originInfo.getAccount(), originInfo.getXSubjectToken());

        KeyInformation taInfo=SwOSSClient.tarIden();

        Swift taObj = new SwiftClientBuilder().build(taInfo.getEndPoint(), taInfo.getAccount(), taInfo.getXSubjectToken());

        Boolean res=false;
        try {
            //对象列表
            List<SwiftObjectSummary> objectSummaries=getObjs(oriObj,bucketName);
            log.info("文件的数量:"+objectSummaries.size());


            List<SwiftObjectSummary> faileSummaries=new ArrayList<SwiftObjectSummary>();
            //线程
            ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(fileThread));
                AtomicInteger i= new AtomicInteger();
            for (SwiftObjectSummary obj: objectSummaries) {
                executorService.execute(() -> {
                    //文件下载
                    boolean loadFile= false;
                    try {
                        log.info("file 大小:" + getPrintSize(obj.getSize()));
                        loadFile = downloadObject(oriObj,bucketName,obj.getKey());
                        //文件大小
                        String maxSize="5368709120";
                        //文件下载成功，再上传
                        if (loadFile){
                            if (obj.getSize()>=Long.valueOf(maxSize)) {
                                //文件大于5G使用命令行上传
                                uploadByHosCmd(bucketName,obj.getKey());
                            }else{
                                createObject(taObj,bucketName,obj.getKey());
                            }
                        }
                        i.getAndIncrement();
                    } catch (Throwable e) {
                        log.error("file Message:" + e.getMessage());
                    }
                });
            }
            // 关闭ExecutorService
           // executorService.shutdown();

            log.info("上传完成,上传:"+objectSummaries.size()+"个文件,成功:"+i+"个文件");

              res=true;
        } catch (ServiceException oe) {
            log.error("Error Message:" + oe.getErrorMessage());
        } catch (ClientException ce) {
            log.error("Error Message:" + ce.getMessage());
        } finally {
            if (originInfo != null) {
                oriObj.shutdown();
            }
            if (taInfo != null) {
                taObj.shutdown();
            }
        }
        return res;
    }


    private void uploadByHosCmd(String bucket,String obj){

        //客户端调用命令/backuptmp/hos/hoscmd-3.4.2/bin
        String finalShell_order = "./hos/hoscmd-3.4.2/bin/hoscmd --endpoint="+ TaendPoint
                +" --account-id="+ accountId +" --access-key="+ ak +" --secret-key="+ sk
                +" put '"+ scannerPath +"/"+ obj +"' hos://"+ bucket +"/"+ obj
                +" -em  --part-size=5120";
        try {
            // 执行Linux命令
            Process process = Runtime.getRuntime().exec(finalShell_order);

            // 读取命令的输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
               log.info(line);
            }
            // 等待命令执行完成
            process.waitFor();
            // 关闭流
            reader.close();
        } catch (Exception e) {
           log.error("执行命令出现异常,异常原因:"+e.toString());
        }






    }

    public List<SwiftObjectSummary>  getObjs(Swift oriObj,String bucketName){
        List<SwiftObjectSummary> objectSummaries=new ArrayList<SwiftObjectSummary>();
         Bucket bucket = oriObj.getBucket(bucketName);
         //文件数量大于1000，要分页
         if (bucket.getObjCount()>1000){
             int pageSize = 1000;
             int curPage = 0;
             String startAfter = null;
             try {
                 while (true) {
                     // 构建获取账户详情以及桶列表请求对象
                     ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
                     // 约束查询出桶的名称大于该值
                     listObjectsRequest.setStartAfter(startAfter);
                     // 限制查询出的数量
                     listObjectsRequest.setMaxKeys(pageSize);
                     // 限制查询桶的前缀
                     // accountInfoBucketsListRequest.setPrefix("prefix");
                     // 查询账户详情
                     ObjectListing bucketInfoObjectListing = oriObj.listObjects(listObjectsRequest);
                     if (bucketInfoObjectListing.getObjectSummaries().size() < pageSize) {
                         System.out.println("=======================第 " + (++curPage) + " 页=====================");
                         //  bucketInfoObjectListing.getObjectSummaries().forEach(System.out::println);
                         objectSummaries.addAll(bucketInfoObjectListing.getObjectSummaries());
                         break;
                     } else {
                         startAfter = bucketInfoObjectListing.getObjectSummaries().get(pageSize - 1).getKey();
                     }
                     System.out.println("=======================第 " + (++curPage) + " 页=====================");
                    //分页集合
                     objectSummaries.addAll(bucketInfoObjectListing.getObjectSummaries());
                 }
             } catch (ServiceException oe) {
                 log.error("服务异常:" + oe.getErrorMessage());
             } catch (ClientException ce) {
                 log.error("请求端异常:" + ce.getMessage());
             }
         }else{
             // 查询桶下独享列表
             ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
             ObjectListing objectListing = oriObj.listObjects(listObjectsRequest);

             //对象列表
             objectSummaries=objectListing.getObjectSummaries();
         }


            return objectSummaries;


    }
    public static String getPrintSize(long size) {
        //格式化小数
        DecimalFormat df = new DecimalFormat("0.00");
        if(size < 1024){
            //少于1024B则直接返回
            return String.valueOf(size) + "B";
        }else if(size >= 1024 && size  < 1024 * 1024){
            //大于1KB少于1MB
            return df.format(size/1024.00) + "KB";
        }else if(size >= 1024 * 1024 && size  < 1024 * 1024 * 1024){
            //大于1MB少于1GB
            return df.format(size/(1024.00 * 1024.00)) + "MB";
        }else{
            //大于1GB少于1TB
            return df.format(size/(1024.00 * 1024.00 * 1024.00)) + "GB";
        }
    }


    public Boolean downloadObject(Swift oriObj,String bucket, String obj)  throws Throwable {
        Boolean is=false;

          // 普通下载
        DownloadFileRequest downloadFileRequest = new DownloadFileRequest(bucket, obj);
        downloadFileRequest.setDownloadFile(scannerPath+obj);
        try {
            DownloadFileResult downloadFileResult = oriObj.downloadObject(downloadFileRequest);
                is=true;
        } catch (ServiceException oe) {
            log.error("Error Message:" + oe.getErrorMessage());
        } catch (ClientException ce) {
           log.error("Error Message:" + ce.getMessage());
        }
        return is;
    }


    /**
     * *
     *@Title  createObject
     *@Description   调用接口上传
     *@params [taObj, bucket, obj]
     *@return void
     *@creator dingrb
     *@creatTime  2024/8/1 10:22
     *@version 1.0
     */
    public void createObject(Swift taObj,String bucket, String obj) throws FileNotFoundException {
        // 设置对象的元数据

        PutObjectRequest putObjectRequest = new PutObjectRequest( bucket,  obj, new FileInputStream(scannerPath+obj));
        try {
            PutObjectResult example = taObj.putObject(putObjectRequest);
            if (example.getResponse().isSuccessful()) {
                log.info("上传成功");
            }
        } catch (ServiceException oe) {
            log.error("Error Code:" + oe.getErrorCode());
        } catch (ClientException ce) {
            log.error("Error Message:" + ce.getMessage());
        }
    }

    public boolean accountData() {
        return false;

    }

    public String getBuckets() {
        //



        return null;

    }

    public boolean dataObj(List<TbSwiftBackupFileTree> objects) {

        KeyInformation originInfo=SwOSSClient.originIden();
        Swift oriObj = new SwiftClientBuilder().build(originInfo.getEndPoint(), originInfo.getAccount(), originInfo.getXSubjectToken());

        KeyInformation taInfo=SwOSSClient.tarIden();

        Swift taObj = new SwiftClientBuilder().build(taInfo.getEndPoint(), taInfo.getAccount(), taInfo.getXSubjectToken());

        String bucketName=objects.get(0).getUserid()+"folder";
        Boolean res=false;
        try {

            //对象列
            log.info("文件的数量:"+objects.size());
            //线程
            ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(fileThread));
            for (TbSwiftBackupFileTree obj: objects) {
                 // executorService.execute(() -> {
                //文件下载
                boolean loadFile= false;
                try {
                    log.info("file 大小:" + getPrintSize(Long.valueOf(obj.getSize())));
                    loadFile = downloadObject(oriObj,bucketName,obj.getAlias());
                    //文件下载成功，再上传
                    //文件大小
                    String maxSize="5368709120";
                    if (loadFile){
                        if (Long.valueOf(obj.getSize())>=Long.valueOf(maxSize)) {
                            //文件大于5G使用命令行上传
                            uploadByHosCmd(bucketName,obj.getAlias());
                        }else{
                            createObject(taObj,bucketName,obj.getAlias());
                        }
                    }
                } catch (Throwable e) {
                    log.error("file Message:" + e.getMessage());
                }
                //  });
            }

            // 关闭ExecutorService
            // executorService.shutdown();

            log.info("上传完成,上传:"+objects.size()+"个文件");

            res=true;
        } catch (ServiceException oe) {
            log.error("Error Message:" + oe.getErrorMessage());
        } catch (ClientException ce) {
            log.error("Error Message:" + ce.getMessage());
        } finally {
            if (originInfo != null) {
                oriObj.shutdown();
            }
            if (taInfo != null) {
                taObj.shutdown();
            }
        }
        return res;
    }
}
