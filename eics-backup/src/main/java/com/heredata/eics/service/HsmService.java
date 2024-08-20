package com.heredata.eics.service;

import com.google.common.collect.Lists;
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

@org.springframework.stereotype.Service
@Slf4j
public class HsmService extends HosClient {

    @Value("${scannerPath}")
    private String scannerPath;

    @Value("${fileThread}")
    private String fileThread;

    @Autowired
    AsyncConfig asyncConfig;

    @Value("${MetaStor}")
    private boolean MetaStor;

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
    public boolean dataHier(String bucketName)  {

        KeyInformation originInfo=SwOSSClient.originIden();
        Swift oriObj = new SwiftClientBuilder().build(originInfo.getEndPoint(), originInfo.getAccount(), originInfo.getXSubjectToken());

        Boolean res=false;
        try {
            //对象列表
            List<SwiftObjectSummary> objectSummaries=getObjs(oriObj,bucketName);
            log.info("文件的数量:"+objectSummaries.size());
            //文件下载
            asyload(bucketName,objectSummaries,"download",oriObj);
            //数据上传
            asyload(bucketName,objectSummaries,"upload",oriObj);
        } catch (ServiceException oe) {
            log.error("Error Message:" + oe.getErrorMessage());
        } catch (ClientException ce) {
            log.error("Error Message:" + ce.getMessage());
        }

        return res;
    }


    /**
     * *
     *@Title  asyload
     *@Description   文件集合的异步操作
     *@param [bucketName, objectSummaries, load, oriObj]
     *@return boolean
     *@creator dingrb
     *@creatTime  2024/8/07 14:59
     *@version 1.0
     */
    public  boolean asyload(String bucketName,List<SwiftObjectSummary> objectSummaries,String load, Swift oriObj){
        Swift taObj=null;
        if ("upload".equals(load)) {
            KeyInformation taInfo = SwOSSClient.tarIden();

            taObj = new SwiftClientBuilder().build(taInfo.getEndPoint(), taInfo.getAccount(), taInfo.getXSubjectToken());
        }
        Boolean res=false;
        if (EmptyValidator.isNotEmpty(objectSummaries)) {
            try {
                log.info("文件的数量:" + objectSummaries.size());
                //线程池开启
                Executor executor = asyncConfig.taskExecutor();

                //文件迁移，回收成集合，进行二次上传(失败包括下载失败以及文件上传失败)
                List<SwiftObjectSummary> faileSummaries = new ArrayList<SwiftObjectSummary>();

                if (objectSummaries.size() > 1000) {
                    List<List<SwiftObjectSummary>> splitNList = Lists.partition(objectSummaries, 1000);
                    log.info("切割之后的大小:" + splitNList.size());

                    List<CompletableFuture> futureList = new ArrayList<>();
                    //计数器，初始值为切割的大小
                    CountDownLatch latch = new CountDownLatch(splitNList.size());

                    for (List<SwiftObjectSummary> summaries : splitNList) {
                        Swift finalTaObj = taObj;
                        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                            List<SwiftObjectSummary> spliFail=null;
                            if ("upload".equals(load)) {
                                //文件上传
                                spliFail= uplocalFile(bucketName, summaries, finalTaObj);
                            }else if("delete".equals(load)){
                                //文件删除
                                deleObjs(summaries,bucketName,oriObj);
                            }else{
                                //文件下载
                                spliFail = dataRe(summaries, bucketName,oriObj);
                            }
                            //失败文件组成集合
                            faileSummaries.addAll(spliFail);
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
                }
                log.info("文件操作完成,成功:" + objectSummaries.size() + "个文件,失败:" + faileSummaries.size() + "个文件，失败文件列表如下:");
                //数据上传
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


    public List<SwiftObjectSummary>  uplocalFile(String bucketName,List<SwiftObjectSummary> objectSummaries,Swift taObj){
        //文件迁移，回收成集合，进行二次上传(失败包括下载失败以及文件上传失败)
        List<SwiftObjectSummary> faileSummaries=new ArrayList<SwiftObjectSummary>();
        try {
            log.info("文件的数量:"+objectSummaries.size());
            for (SwiftObjectSummary obj: objectSummaries) {
                //本地文件上传
                boolean upFile=false;
                try {
                    log.info("file 大小:" + EicsUtils.converseFileSize(obj.getSize()));
                    //文件大小
                    String maxSize="5368709120";
                    if (obj.getSize()>=Long.valueOf(maxSize)) {
                        //文件大于5G使用命令行上传
                        upFile=  uploadByHosCmd(bucketName,obj.getKey());
                    }else{
                        upFile= createObject(taObj,bucketName,obj.getKey());
                    }

                    if (upFile){
                        faileSummaries.add(obj);
                    }
                } catch (Throwable e) {
                    log.error("file Message:" + e.getMessage());
                }
            }
            log.info("上传完成,总的文件数:"+objectSummaries.size()+"个文件,失败:"+faileSummaries.size()+"个文件，失败文件列表已形成文件打印");
        } catch (ServiceException oe) {
            log.error("Error Message:" + oe.getErrorMessage());
        } catch (ClientException ce) {
            log.error("Error Message:" + ce.getMessage());
        }
        return faileSummaries;
    }

    /**
     * *
     *@Title  dataRe
     *@Description 文件循环下载
     *@param [summaries, bucketName]
     *@return boolean
     *@creator dingrb
     *@creatTime  2024/8/8 11:09
     *@version 1.0
     */
    public List<SwiftObjectSummary> dataRe(List<SwiftObjectSummary> summaries,String bucketName,Swift oriObj)  {
        //文件迁移，回收成集合，进行二次上传(失败包括下载失败以及文件上传失败)
        List<SwiftObjectSummary> faileSummaries=new ArrayList<SwiftObjectSummary>();
        try {
            for (SwiftObjectSummary obj: summaries) {
                //文件下载
                boolean loadFile= false;
                try {
                    log.info("file 大小:" + EicsUtils.converseFileSize(obj.getSize()));
                    loadFile = downloadObject(oriObj,bucketName,obj.getKey());
                    if (loadFile){
                        faileSummaries.add(obj);
                    }
                } catch (Throwable e) {
                    log.error("file Message:" + e.getMessage());
                }
            }
            log.info("文件下载完成,总数:"+summaries.size()+"个文件,成功:"+faileSummaries.size()+"个文件");
            /*多次调用,防止乱码文件使用
            if (EmptyValidator.isNotEmpty(faileSummaries)) {
                dataRe(faileSummaries, bucketName);
            }*/
            //打印出未能下载的文件数量
            faileSummaries.stream().forEach(System.out::println);;
        } catch (ServiceException oe) {
            log.error("Error Message:" + oe.getErrorMessage());
        } catch (ClientException ce) {
            log.error("Error Message:" + ce.getMessage());
        }
        return faileSummaries;
    }

    /**
     * *
     *@Title  uploadByHosCmd
     *@Description   使用hos 客户端上传文件
     *@param [bucket, obj]
     *@return java.lang.Boolean
     *@creator dingrb
     *@creatTime  2024/8/8 11:08
     *@version 1.0
     */
    private Boolean uploadByHosCmd(String bucket,String obj){
        Boolean re=false;
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
            re=true;
        } catch (Exception e) {
            log.error("执行命令出现异常,异常原因:"+e.toString());
        }

        return re;
    }

    /**
     * *
     *@Title  getObjs
     *@Description   数据分页展示汇总
     *@param [oriObj, bucketName]
     *@return java.util.List<com.heredata.swift.model.SwiftObjectSummary>
     *@creator dingrb
     *@creatTime  2024/8/8 10:34
     *@version 1.0
     */
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
                    // 查询账户详情
                    ObjectListing bucketInfoObjectListing = oriObj.listObjects(listObjectsRequest);
                    if (bucketInfoObjectListing.getObjectSummaries().size() < pageSize) {
                        log.info("=======================第 " + (++curPage) + " 页=====================");
                        objectSummaries.addAll(bucketInfoObjectListing.getObjectSummaries());
                        break;
                    } else {
                        startAfter = bucketInfoObjectListing.getObjectSummaries().get(pageSize - 1).getKey();
                    }
                    log.info("=======================第 " + (++curPage) + " 页=====================");
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
        log.info("文件是否下载成功=====:"+is);
        return is;
    }


    /**
     * *
     *@Title  createObject
     *@Description   调用接口上传
     *@param [taObj, bucket, obj]
     *@return void
     *@creator dingrb
     *@creatTime  2024/8/1 10:22
     *@version 1.0
     */
    public Boolean createObject(Swift taObj,String bucket, String obj) throws FileNotFoundException {
        Boolean re=false;

        PutObjectRequest putObjectRequest = new PutObjectRequest( bucket,  obj, new FileInputStream(scannerPath+obj));
        try {
            PutObjectResult example = taObj.putObject(putObjectRequest);
            if (example.getResponse().isSuccessful()) {
                log.info("上传成功");
                re=true;
            }
        } catch (ServiceException oe) {
            log.error("Error Code:" + oe.getErrorCode());
        } catch (ClientException ce) {
            log.error("Error Message:" + ce.getMessage());
        }

        return re;
    }


    public String getBuckets() {
        //
        return null;

    }
    @Async
    public boolean dataObj(List<TbSwiftBackupFileTree> objects) {

        KeyInformation originInfo=SwOSSClient.originIden();
        Swift oriObj = new SwiftClientBuilder().build(originInfo.getEndPoint(), originInfo.getAccount(), originInfo.getXSubjectToken());

        KeyInformation taInfo=SwOSSClient.tarIden();

        Swift taObj = new SwiftClientBuilder().build(taInfo.getEndPoint(), taInfo.getAccount(), taInfo.getXSubjectToken());

        String bucketName=objects.get(0).getUserid()+"folder";
        Boolean res=false;
        try {
            //文件迁移，回收成集合，进行二次上传(失败包括下载失败以及文件上传失败)
            List<TbSwiftBackupFileTree> faileTree=new ArrayList<TbSwiftBackupFileTree>();
            //对象列
            log.info("文件的数量:"+objects.size());
            //线程
            ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(fileThread));
            for (TbSwiftBackupFileTree obj: objects) {
                // executorService.execute(() -> {
                //文件下载
                boolean loadFile= false;
                boolean upFile=false;
                try {
                    log.info("file 大小:" + EicsUtils.converseFileSize(Long.valueOf(obj.getSize())));
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
                    if (loadFile|| upFile){
                        faileTree.add(obj);
                    }
                } catch (Throwable e) {
                    log.error("file Message:" + e.getMessage());
                }
                //  });
            }

            // 关闭ExecutorService
            // executorService.shutdown();

            log.info("上传完成,上传:"+objects.size()+"个文件");

            if (EmptyValidator.isNotEmpty(faileTree)) {
                dataTree(faileTree, bucketName);
            }

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

    public boolean loadFile(String bucketName) {
        KeyInformation originInfo=SwOSSClient.originIden();
        Swift oriObj = new SwiftClientBuilder().build(originInfo.getEndPoint(), originInfo.getAccount(), originInfo.getXSubjectToken());

        KeyInformation taInfo=SwOSSClient.tarIden();

        Swift taObj = new SwiftClientBuilder().build(taInfo.getEndPoint(), taInfo.getAccount(), taInfo.getXSubjectToken());

        Boolean res=false;
        try {
            //对象列表
            List<SwiftObjectSummary> objectSummaries=getObjs(oriObj,bucketName);
            log.info("文件的数量:"+objectSummaries.size());

            //文件迁移，回收成集合，进行二次上传(失败包括下载失败以及文件上传失败)
            List<SwiftObjectSummary> faileSummaries=new ArrayList<SwiftObjectSummary>();
            //线程
            ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(fileThread));
            for (SwiftObjectSummary obj: objectSummaries) {
                //      executorService.execute(() -> {
                //文件下载
                boolean loadFile= false;
                boolean upFile=false;
                try {
                    log.info("file 大小:" + EicsUtils.converseFileSize(obj.getSize()));
                    loadFile = downloadObject(oriObj,bucketName,obj.getKey());
                    if (!loadFile){
                        faileSummaries.add(obj) ;
                    }
                } catch (Throwable e) {
                    log.error("file Message:" + e.getMessage());
                }
                //       });
            }
            // 关闭ExecutorService
            //    executorService.shutdown();

            log.info("上传完成,上传:"+objectSummaries.size()+"个文件,成功:"+faileSummaries.size()+"个文件，失败文件列表如下:");
            faileSummaries.stream().forEach(System.out::println);
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

    public boolean dataTree(List<TbSwiftBackupFileTree> summaries,String bucketName)  {

        KeyInformation originInfo=SwOSSClient.originIden();
        Swift oriObj = new SwiftClientBuilder().build(originInfo.getEndPoint(), originInfo.getAccount(), originInfo.getXSubjectToken());

        KeyInformation taInfo=SwOSSClient.tarIden();

        Swift taObj = new SwiftClientBuilder().build(taInfo.getEndPoint(), taInfo.getAccount(), taInfo.getXSubjectToken());

        Boolean res=false;
        try {
            //文件迁移，回收成集合，进行二次上传(失败包括下载失败以及文件上传失败)
            List<SwiftObjectSummary> faileSummaries=new ArrayList<SwiftObjectSummary>();
            //线程
            ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(fileThread));
            int i=0;
            for (TbSwiftBackupFileTree obj: summaries) {
                // executorService.execute(() -> {
                //文件下载
                boolean loadFile= false;
                boolean upFile=false;
                try {
                    log.info("file 大小:" + EicsUtils.converseFileSize(Long.valueOf(obj.getSize())));
                    loadFile = downloadObject(oriObj,bucketName,obj.getAlias());
                    //文件大小
                    String maxSize="5368709120";
                    //文件下载成功，再上传
                    if (loadFile){
                        if (Long.valueOf(obj.getSize())>=Long.valueOf(maxSize)) {
                            //文件大于5G使用命令行上传
                            upFile=  uploadByHosCmd(bucketName,obj.getAlias());
                        }else{
                            upFile= createObject(taObj,bucketName,obj.getAlias());
                        }
                    }
                } catch (Throwable e) {
                    log.error("file Message:" + e.getMessage());
                }
                //  });
            }

            log.info("上传完成,上传:"+summaries.size()+"个文件,失败:"+faileSummaries.size()+"个文件");
            //多次调用
            if (EmptyValidator.isNotEmpty(faileSummaries)) {
                dataRe(faileSummaries, bucketName,oriObj);
            }
            res=true;
        } catch (ServiceException oe) {
            log.error("Error Message:" + oe.getErrorMessage());
        } catch (ClientException ce) {
            log.error("Error Message:" + ce.getMessage());
        }
        return res;
    }

    public boolean moveBuck(String bucketName) {
        KeyInformation originInfo=SwOSSClient.originIden();
        Swift oriObj = new SwiftClientBuilder().build(originInfo.getEndPoint(), originInfo.getAccount(), originInfo.getXSubjectToken());

        Boolean res=false;
        try {
            //容器删除要以清除完容器内对象
            List<SwiftObjectSummary> objectSummaries = getObjs(oriObj, bucketName);
            log.info("文件的数量:" + objectSummaries.size());

            //文件批量操作
            asyload(bucketName,objectSummaries,"delete",null);

        }catch (Exception e){

        }


        return res;
    }


    public void deleObjs(List<SwiftObjectSummary> summaries,String bucketName,Swift oriObj){
        //文件删除失败
        List<SwiftObjectSummary> faileSummaries=new ArrayList<SwiftObjectSummary>();
        try {
            for (SwiftObjectSummary obj: summaries) {
                //文件下载
                boolean loadFile= false;
                try {
                    VoidResult example = oriObj.deleteObject(bucketName, obj.getKey());
                } catch (Throwable e) {
                    log.error("file Message:" + e.getMessage());
                }
            }
            log.info("文件下载完成,总数:"+summaries.size()+"个文件,成功:"+faileSummaries.size()+"个文件");
            /*多次调用,防止乱码文件使用
            if (EmptyValidator.isNotEmpty(faileSummaries)) {
                dataRe(faileSummaries, bucketName);
            }*/
            //打印出未能下载的文件数量
            faileSummaries.stream().forEach(System.out::println);;
        } catch (ServiceException oe) {
            log.error("Error Message:" + oe.getErrorMessage());
        } catch (ClientException ce) {
            log.error("Error Message:" + ce.getMessage());
        }

    }
}
