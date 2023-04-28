package com.heredata.swift;

import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import com.heredata.model.VoidResult;
import com.heredata.swift.model.*;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * object test
 * @author wuzezhao
 * @since 2022/8/4
 */
public class ObjectTest extends TestBase {

    /**
     * 创建/上传对象
     * @throws FileNotFoundException
     */
    @Test
    public void createObject() throws FileNotFoundException {

        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        // 设置对象的元数据
        PutObjectRequest putObjectRequest = new PutObjectRequest("bucket"
                , "/input/a/b/c/d/e/f", new FileInputStream("E:\\比洛巴乔\\Desktop\\卡农简谱.png"));
        try {
            PutObjectResult example = swift.putObject(putObjectRequest);
            if (example.getResponse().isSuccessful()) {
                System.out.println("上传成功");
            }
        } catch (ServiceException oe) {
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (swift != null) {
                swift.shutdown();
            }
        }
    }

    /**
     * 获取对象的元数据
     */
    @Test
    public void getObjectMeta() {

        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        try {
            ObjectMetadata example = swift.getObjectMeta("bucket", "tmp/hadoop-yarn/staging/root/.staging/job_1680161709720_0001/job.splitmetainfo");
            System.out.println(example);
        } catch (ServiceException oe) {
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (swift != null) {
                swift.shutdown();
            }
        }
    }

    /**
     * 设置对象元数据
     */
    @Test
    public void setObjectMeta() {
        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.addUserMetadata("isdir", "true");
//            objectMetadata.addUserMetadata("meta2", "value2");
//            objectMetadata.addUserMetadata("meta3", "value3");
            VoidResult example = swift.setObjectMeta("bucket", "2023/03/29/qwer.txt", objectMetadata);
            System.out.println(example);
        } catch (ServiceException oe) {
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (swift != null) {
                swift.shutdown();
            }
        }
    }

    /**
     * 列出桶内对象列表
     */
    @Test
    public void listObjects() {
        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        try {
            // 创建请求对象
            System.out.println("============查询出所有对象====================");
            ObjectListing example = swift.listObjects("bucket");
            example.getObjectSummaries().stream().forEach(item -> System.out.println(item));

//            // 查询前缀和大于"e"的对象
//            System.out.println("===========查询出对象名称前缀为e的对象=====================");
//            ListObjectsRequest prefixQuery = new ListObjectsRequest("example");
//            prefixQuery.setPrefix("2022/08/11/");
//            ObjectListing example1 = swift.listObjects(prefixQuery);
//            example1.getObjectSummaries().stream().forEach(item -> System.out.println(item.getKey()));
//
//            // 设置查询数量
//            System.out.println("===========限制数量查询=====================");
//            ListObjectsRequest maxCountQuery = new ListObjectsRequest("example");
//            maxCountQuery.setMaxKeys(1);
//            ObjectListing example2 = swift.listObjects(maxCountQuery);
//            example2.getObjectSummaries().stream().forEach(item -> System.out.println(item.getKey()));
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (swift != null) {
                swift.shutdown();
            }
        }
    }

    /**
     * 分页查询桶内对象列表
     */
    @Test
    public void listObjectsByPage() {
        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        int pageSize = 2;
        int curPage = 0;
        String startAfter = null;
        try {
            while (true) {
                // 构建获取账户详情以及桶列表请求对象
                ListObjectsRequest listObjectsRequest = new ListObjectsRequest("example");
                // 约束查询出桶的名称大于该值
                listObjectsRequest.setStartAfter(startAfter);
                // 限制查询出的数量
                listObjectsRequest.setMaxKeys(pageSize);
                // 限制查询桶的前缀
                // accountInfoBucketsListRequest.setPrefix("prefix");
                // 查询账户详情
                ObjectListing bucketInfoObjectListing = swift.listObjects(listObjectsRequest);
                if (bucketInfoObjectListing.getObjectSummaries().size() < pageSize) {
                    System.out.println("=======================第 " + (++curPage) + " 页=====================");
                    bucketInfoObjectListing.getObjectSummaries().forEach(System.out::println);
                    break;
                } else {
                    startAfter = bucketInfoObjectListing.getObjectSummaries().get(pageSize - 1).getKey();
                }
                System.out.println("=======================第 " + (++curPage) + " 页=====================");
                bucketInfoObjectListing.getObjectSummaries().forEach(System.out::println);
            }
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (swift != null) {
                swift.shutdown();
            }
        }
    }


    /**
     * 查询对象详情:对象最后更新时间,标签、对象大小、对象mime类型
     */
    @Test
    public void getObject() {
        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        try {
            SwiftObject example = swift.getObject("example", "2022/08/25/qwer.txt");
            System.out.println(example);
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (swift != null) {
                swift.shutdown();
            }
        }
    }

    /**
     * 下载
     */
    @Test
    public void downloadObject() throws Throwable {
        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
//        DownloadFileRequest downloadFileRequest = new DownloadFileRequest("bucket", "input/hosts");
//        // 开启断点下载
//        downloadFileRequest.setEnableCheckpoint(true);
//        // 每片5M的进行下载
//        downloadFileRequest.setPartSize(1024 * 1024 * 5);
//        // 下载本地的文件
//        downloadFileRequest.setDownloadFile("E:\\比洛巴乔\\Desktop\\a.avi");
//        // 断点下载
//        swift.downloadObject(downloadFileRequest);

        // 普通下载
        DownloadFileRequest downloadFileRequest = new DownloadFileRequest("bucket", "tmp/hadoop-yarn/staging/root/.staging/job_1680167329682_0002/job.xml");
        downloadFileRequest.setDownloadFile("E:\\比洛巴乔\\Desktop\\job.xml");
        try {
            DownloadFileResult downloadFileResult = swift.downloadObject(downloadFileRequest);
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (swift != null) {
                swift.shutdown();
            }
        }
    }

    /**
     * 删除对象
     */
    @Test
    public void deleteObject() {
        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        try {
            VoidResult example = swift.deleteObject("bucket", "input/a/b/c/d/e/f/g/h");
            System.out.println(example);
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (swift != null) {
                swift.shutdown();
            }
        }

    }

    /**
     * 复制对象
     */
    @Test
    public void copyObject() {
        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        // 构建复制对象请求参数
        CopyObjectRequest copyObjectRequest = new CopyObjectRequest("example", "2022/08/27/qwer.txt"
                , "example", "2022/09/999/a.txt");
        try {
            CopyObjectResult example = swift.copyObject(copyObjectRequest);
            if (example.getResponse().isSuccessful()) {
                System.out.println("复制成功");
            }
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (swift != null) {
                swift.shutdown();
            }
        }
    }
}
