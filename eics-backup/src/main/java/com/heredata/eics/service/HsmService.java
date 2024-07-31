package com.heredata.eics.service;

import com.google.common.collect.Lists;
import com.heredata.eics.utils.HosClient;
import com.heredata.eics.utils.SwOSSClient;
import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import com.heredata.swift.Swift;
import com.heredata.swift.SwiftClientBuilder;
import com.heredata.swift.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
        List<SwiftObjectSummary> objectSummaries=new ArrayList<SwiftObjectSummary>();
        try {
            // 查询桶下独享列表
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
            ObjectListing objectListing = oriObj.listObjects(listObjectsRequest);
            //对象列表
            objectSummaries=objectListing.getObjectSummaries();
            log.info("文件的数量:"+objectSummaries.size());
            //线程
            ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(fileThread));
            for (SwiftObjectSummary obj: objectSummaries) {
              //  executorService.execute(() -> {
                    //文件下载
                    boolean loadFile= false;
                    try {
                        loadFile = downloadObject(oriObj,bucketName,obj.getKey());
                        //文件下载成功，再上传
                        if (loadFile){
                            createObject(taObj,bucketName,obj.getKey());
                        }
                    } catch (Throwable e) {
                        log.error("file Message:" + e.getMessage());
                    }
              //  });
            }

            // 关闭ExecutorService
           // executorService.shutdown();

            log.info("上传完成,上传:"+objectSummaries.size()+"个文件");

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
}
