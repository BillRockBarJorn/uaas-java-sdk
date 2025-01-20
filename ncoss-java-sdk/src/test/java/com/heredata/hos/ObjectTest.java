package com.heredata.hos;

import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import com.heredata.hos.model.*;
import com.heredata.model.VoidResult;
import com.heredata.swift.model.DownloadFileRequest;
import com.heredata.utils.IOUtils;
import com.heredata.utils.StringUtils;
import org.junit.Test;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * object test
 * @author wuzezhao
 * @since 2022/8/4
 */
public class ObjectTest extends TestBase {


    @Test
    public void test222() throws MalformedURLException {
        double aaa = 99999999999D;
        String s = "9.9999999999E10";
        Double aDouble = Double.valueOf(s);
        System.out.println(153931627888640D);

//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        try {
//            Date parse = simpleDateFormat.parse("2023-07-08 ");
//            System.out.println(parse);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
    }

    @Test
    public void test1() throws IOException {
        HOS hos = getHOSClient();
        ObjectListing hbase = hos.listObjects("hadoop");
        hbase.getObjectSummaries().forEach(item -> hos.deleteObject("hadoop", item.getKey()));

//        FileInputStream fis =new FileInputStream("E:\\比洛巴乔\\Desktop\\haha2.java");
//        byte[] bys = new byte[64];
//        int read = fis.read(bys, 4, 64);
//        fis.close();

//        RandomAccessFile aFile = new
//                RandomAccessFile("E:\\比洛巴乔\\Desktop\\haha2.java", "rw");
//        FileChannel fromChannel = aFile.getChannel();
//        RandomAccessFile bFile = new
//                RandomAccessFile("E:\\比洛巴乔\\Desktop\\haha3.java", "rw");
//        FileChannel toChannel = bFile.getChannel();
//        long position = 0;
//        long count = fromChannel.size();
//        ByteBuffer allocate = ByteBuffer.allocate(16);
//        toChannel.read(allocate);
//        long l = fromChannel.transferTo(4, count, toChannel);
//        toChannel.write(allocate);
//        System.out.println(l);
//        aFile.close();
//        bFile.close();
//        System.out.println("over!");


    }


    /**
     * 创建/上传对象
     * @throws FileNotFoundException
     */
    @Test
    public void createObject() throws FileNotFoundException {

        HOS hos = getHOSClient();
        // 设置对象的元数据
        ObjectMetadata objectMetadata = new ObjectMetadata();
//        objectMetadata.setObjectStorageClass(StorageClass.ARCHIVE);
//        objectMetadata.addUserMetadata("isdir", "false");
//        objectMetadata.getUserMetadata().put("meta1", "I am meta1");
//        objectMetadata.getUserMetadata().put("meta2", "I am meta2");
//        objectMetadata.getUserMetadata().put("meta3", "I am meta3");
//        objectMetadata.getUserMetadata().put("example", "txt1");
//        objectMetadata.getUserMetadata().put("example2", "txt2");

        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(new byte[]{97, 98, 99, 100});
            PutObjectRequest putObjectRequest = new PutObjectRequest("bt02",
                    "a/defdg.txt", byteArrayInputStream, objectMetadata);
            PutObjectResult example = hos.putObject(putObjectRequest);

//            for (int i = 0; i < 100; i++) {
//                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(new byte[]{97, 98, 99, 100});
////        /opdb/docfile/17853/6FA4982D58B14653B2B1370BB7D132BB/123321.docx
//                PutObjectRequest putObjectRequest = new PutObjectRequest("bucket1",
//                        "a/" + i + ".txt"
//                        , byteArrayInputStream, objectMetadata);
//
////        PutObjectRequest putObjectRequest = new PutObjectRequest("jssdk",
////                "2023/04/20/软负载安装.zip"
////                , new FileInputStream("E:\\比洛巴乔\\Desktop\\fsdownload\\软负载安装.zip"), null);
//                PutObjectResult example = hos.putObject(putObjectRequest);
//                if (example.getResponse().isSuccessful()) {
//                    System.out.println(example);
//                    System.out.println("上传成功");
//                }
//            }
        } catch (ServiceException oe) {
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }


    /**
     * 列出桶内对象列表
     */
    @Test
    public void listObjects() {
        HOS hos = getHOSClient();
        try {
            // 创建请求对象
//            ObjectListing example = hos.listObjects("jssdk", "");
//            example.getObjectSummaries().stream().forEach(item -> System.out.println(item.getKey()));
//            System.out.println("================================");

            // 查询前缀和大于"e"的对象
//            ListObjectsRequest prefixQuery = new ListObjectsRequest("hadoop","BP-1991540692-172.26.68.47-1692611683831/current/rbw",);
//            prefixQuery.setPrefix("prefix");
//            prefixQuery.setStartAfter("2023/04/26");
            ObjectListing example1 = hos.listObjects("jssdk6");
            example1.getObjectSummaries().stream().forEach(item -> System.out.println(item.getKey()));
            System.out.println("================================");
//
//            // 设置查询数量
//            ListObjectsRequest maxCountQuery = new ListObjectsRequest("example");
//            maxCountQuery.setMaxKeys(1);
//            maxCountQuery.setPrefix("");
//            maxCountQuery.setStartAfter("");
//            ObjectListing example2 = hos.listObjects(maxCountQuery);
//            example2.getObjectSummaries().stream().forEach(item -> System.out.println(item.getKey()));
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }

    @Test
    public void demo3() {
        HOS hos = getHOSClient();
        String prefix = "";
        ListObjectsRequest listRequest = new ListObjectsRequest("example");
        listRequest.setPrefix(prefix.trim().length() > 0 ? prefix : null);
        listRequest.setMaxKeys(1000);

        ObjectListing listing = hos.listObjects(listRequest);
        listing.getObjectSummaries().forEach(System.out::println);
    }


    @Test
    public void listVersionsObjectsByPage() {
        HOS hos = getHOSClient();

        // 页码
        int index = 1;
        try {
            // 下一页的起点
            String nextStartAfter = null;
            String versionId=null;
            // 查询结果对象
            VersionListing versionListing;
            do {
                System.out.println("====================第 " + index + " 页======================");
                // 构建请求参数并查询
                versionListing = hos.listVersions(new ListVersionsRequest().withBucketName("bucket1")
                        .withStartAfter(nextStartAfter).withMaxKeys(2).withVersionIdMarker(versionId));

                // 打印结果
                List<HOSVersionSummary> sums = versionListing.getVersionSummaries();
                for (HOSVersionSummary s : sums) {
                    System.out.println(s);
                }
                // 获取nextStartAfter
                nextStartAfter = versionListing.getNextStartAfter();
                versionId=versionListing.getNextVersionIdMarker();
                index++;
            } while (versionListing.isTruncated());
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }

    /**
     * 列出桶内对象列表
     */
    @Test
    public void listVersions() {
        HOS hos = getHOSClient();
        try {
            // 创建请求对象
            VersionListing example = hos.listVersions(new ListVersionsRequest().withBucketName("bucket1"));
            example.getVersionSummaries().stream().forEach(item -> System.out.println(item));
            System.out.println("================================");

//            // 查询前缀和大于"e"的对象
//            VersionListing example1 = hos.listVersions("jssdk", "2023", "a", null, 100);
//            example1.getVersionSummaries().stream().forEach(item -> System.out.println(item.getKey()));
//            System.out.println("================================");
//
//            // 设置查询数量
//            ListVersionsRequest listVersionsRequest = new ListVersionsRequest("test_Hss2", null, null, null, 10);
//            listVersionsRequest.setMaxKeys(1);
//            VersionListing example2 = hos.listVersions(listVersionsRequest);
//            example2.getVersionSummaries().stream().forEach(item -> System.out.println(item.getKey()));
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }

    @Test
    public void listObjectsByPage() {
        HOS hos = getHOSClient();

        // 页码
        int index = 1;
        try {
            // 下一页的起点
            String nextStartAfter = null;

            ObjectListing objectListing;
            do {
                System.out.println("============第 " + index + " 页=================");
                // 构建请求参数并查询
                objectListing = hos.listObjects(new ListObjectsRequest("bucket1")
                        .withStartAfter(nextStartAfter).withMaxKeys(2));

                // 打印结果
                List<HOSObjectSummary> sums = objectListing.getObjectSummaries();
                for (HOSObjectSummary s : sums) {
                    System.out.println(s.getKey());
                }
                // 获取nextStartAfter
                nextStartAfter = objectListing.getDelimiter();
                index++;
            } while (objectListing.isTruncated());
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }

    /**
     * 创建/上传对象   使用SSE-KMS上传
     * @throws FileNotFoundException
     */
    @Test
    public void createObject_SSE_KMS() throws FileNotFoundException {
        HOS hos = getHOSClient();
        // 设置加密id
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setServerSideEncryption("here:kms");
        objectMetadata.setServerSideEncryptionKeyId(secretId);
        PutObjectRequest putObjectRequest = new PutObjectRequest("example", "2022/08/22/q.txt"
                , new FileInputStream("E:\\比洛巴乔\\Desktop\\b.txt"), objectMetadata);
        try {
            PutObjectResult example = hos.putObject(putObjectRequest);
            if (example.getResponse().isSuccessful()) {
                System.out.println("上传加密成功");
            }
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }

    /**
     * 创建/上传对象   使用SSE-C加密
     * @throws FileNotFoundException
     */
    @Test
    public void createObject_SSE_C() throws FileNotFoundException {
        HOS hos = getHOSClient();
        // 构建请求头元数据
        ObjectMetadata objectMetadata = new ObjectMetadata();
        // 设置加密方法
        objectMetadata.setClientSideEncryptionAlgorithm(AlgorithmEnum.AES256);
        String pointLengthUUID = StringUtils.getPointLengthUUID(32);
        objectMetadata.setClientSideEncryptionKey(pointLengthUUID);

        PutObjectRequest putObjectRequest = new PutObjectRequest("example", "2022/08/23/q.txt"
                , new FileInputStream("E:\\比洛巴乔\\Desktop\\b.txt"), objectMetadata);
        try {
            PutObjectResult example = hos.putObject(putObjectRequest);
            System.out.println(example);
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }

    /**
     * 查询对象详情:对象最后更新时间,标签、对象大小、对象mime类型
     */
    @Test
    public void getObject() throws IOException {
        HOS hos = getHOSClient();
        try {
            GetObjectRequest getObjectRequest = new GetObjectRequest("ncoss-4", "a/b/c/d");
            getObjectRequest.setVersionId("3e34fac4425a11eeb3a4fa163e2fcf06");
            getObjectRequest.setIncludeInputStream(true);
//            http://172.18.232.37:8089/v1/HOS_7c9dfff2139b11edbc330391d2a979b2/hbase/hbase/MasterData/WALs/host-172-18-193-129%2C14001%2C1676295626289
            HOSObject example = hos.getObject(getObjectRequest);
            InputStream objectContent = example.getObjectContent();

            byte bys[] = new byte[128];
            int read = objectContent.read(bys);
            System.out.println("内容为：" + new String(bys, 0, read));
            System.out.println(example);
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }

    /**
     * 获取对象内容和对象信息（下载对象）   使用SSE-C加密
     */
    @Test
    public void getObjectInfo_SSE_C() {
        HOS hos = getHOSClient();
        // 构建获取对象请求参数
        GetObjectRequest getObjectRequest = new GetObjectRequest("example", "2022/08/15/c.txt");
        getObjectRequest.setIncludeInputStream(true);
        // 设置加密方式
        getObjectRequest.setClientSideEncryptionAlgorithm(AlgorithmEnum.AES256);
        String pointLengthUUID = StringUtils.getPointLengthUUID(32);
        getObjectRequest.setClientSideEncryptionKey(pointLengthUUID);
        try {
            HOSObject example = hos.getObject(getObjectRequest);
            System.out.println(example);
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }


    /**
     * 下载
     */
    @Test
    public void downLoadObject() throws Throwable {
        HOS hos = getHOSClient();
        try {
            DownloadFileRequest downloadFileRequest = new DownloadFileRequest("jssdk6", "2022/08/3123/4.avi");
//            downloadFileRequest.setVersionId("4182cc941c7411eeac37c7c965b9af79");
            // 开启断点下载
            downloadFileRequest.setEnableCheckpoint(true);
            // 每片5M的进行下载
            downloadFileRequest.setPartSize(1024 * 128);
            // 下载本地的文件
            downloadFileRequest.setDownloadFile("E:\\比洛巴乔\\Desktop\\bb.avi");
            // 断点下载
            hos.downloadObject(downloadFileRequest);
//            // 普通下载
//            DownloadFileRequest downloadFileRequest = new DownloadFileRequest("ncoss-4", "a/b/c/d");
//            downloadFileRequest.setVersionId("3e34fac4425a11eeb3a4fa163e2fcf06");
////            downloadFileRequest.setVersionId("4182cc941c7411eeac37c7c965b9af79");
//            downloadFileRequest.setDownloadFile("E:\\比洛巴乔\\Desktop\\blk_1073741825_1001.meta");
//            hos.downloadObject(downloadFileRequest);
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }

    /**
     * 删除对象
     */
    @Test
    public void deleteObject() {
        HOS hos = getHOSClient();
        try {
            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest("bucket1");
//            deleteObjectsRequest.setVersionId("3e34fac4425a11eeb3a4fa163e2fcf06");
            deleteObjectsRequest.setKey("a/b/c/ddcc");
            hos.deleteObject(deleteObjectsRequest);
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }

    }


    /**
     * 对象标签管理
     */
    @Test
    public void objectTagging() {
        HOS hos = getHOSClient();
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        try {
            SetObjectTaggingRequest request = new SetObjectTaggingRequest("ncoss-4", "a/b/c/d", map);
            request.setVersionId("4b574ab8425a11eeb3a4fa163e2fcf06");
            // 设置标签
            VoidResult example = hos.setObjectTagging(request);
            if (example.getResponse().isSuccessful()) {
                System.out.println("设置成功");
            }
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }

    /**
     * 查询对象标签
     */
    @Test
    public void getObjectTagging() {
        HOS hos = getHOSClient();
        try {
            TagSet example = hos.getObjectTagging("ncoss-4", "a/b/c/d");
            System.out.println(example);
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }

    /**
     * 删除对象标签
     */
    @Test
    public void deleteObjectTagging() {
        HOS hos = getHOSClient();
        try {
            VoidResult example = hos.deleteObjectTagging("ncoss-4", "a/b/c/d");
            if (example.getResponse().isSuccessful()) {
                System.out.println("删除成功");
            }
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }

    /**
     * 设置对象acl
     * 默认情况下，只有对象的拥有者才有该对象的读写权限。此操作属于覆盖操作，
     */
    @Test
    public void setObjectAcl() {
        HOS hos = getHOSClient();
        // 设置权限控制信息，所有用户可匿名访问
        Grantee grantee = GroupGrantee.AllUsers;
        // 设置权限
        Permission permission = Permission.READ;
        Permission write = Permission.WRITE;

        Grantee grantee1 = new CanonicalUserGrantee("7c9dfff2139b11edbc330391d2a979b2");

        // 创建权限访问控制容器
        AccessControlList accessControlList = new AccessControlList();
        accessControlList.grantPermission(grantee1, permission);
        accessControlList.grantPermission(grantee, write);

        // 构建请求参数对象
        SetAclRequest setAclRequest = new SetAclRequest("ncoss-4", "a/b/c/d", accessControlList);
        // 如果对象有版本号，必须要填写版本号
        //setAclRequest.setVersionId("4b574ab8425a11eeb3a4fa163e2fcf06");
        try {
            // 设置
            VoidResult result = hos.setObjectAcl(setAclRequest);
            System.out.println(result);
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }

    /**
     * 查询对象acl
     */
    @Test
    public void getObjectAcl() {
        HOS hos = getHOSClient();
        try {
            GenericRequest genericRequest = new GenericRequest("ncoss-4", "a/b/c/d", "4b574ab8425a11eeb3a4fa163e2fcf06");
            AccessControlList example = hos.getObjectAcl(genericRequest);
            System.out.println(example);
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }

    /**
     * 复制对象
     */
    @Test
    public void copyObject() {
        HOS hos = getHOSClient();
        // 构建复制对象请求参数
        CopyObjectRequest copyObjectRequest = new CopyObjectRequest("ncoss-4", "a/b/c/d",
                "ncoss-3", "a/b/c/d");


        // 设置对象的元数据
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setObjectDirective("REPLACE_NEW");
        objectMetadata.addUserMetadata("meta1", "vales1");
        objectMetadata.addUserMetadata("meta2", "vales2");
        objectMetadata.addUserMetadata("meta3", "vales3");
        objectMetadata.setObjectStorageClass(StorageClass.STANDARD);
        copyObjectRequest.setNewObjectMetadata(objectMetadata);
        try {
            CopyObjectResult example = hos.copyObject(copyObjectRequest);
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
            if (hos != null) {
                hos.shutdown();
            }
        }
    }

    /**
     * 复制对象, 服务端对对象进行加密，使用SSE-KMS加密
     */
    @Test
    public void copyObject_SSE_KMS() {
        HOS hos = getHOSClient();
        // 构建复制对象的请求参数
        CopyObjectRequest copyObjectRequest = new CopyObjectRequest("example", "2022/08/15/b.txt"
                , "example", "2022/08/15/c.txt");
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setObjectDirective("COPY");
        copyObjectRequest.setNewObjectMetadata(objectMetadata);
        // 设置服务端加密
        copyObjectRequest.setServerSideEncryption("here:kms");
        copyObjectRequest.setServerSideEncryptionKeyID(secretId);
        try {
            CopyObjectResult example = hos.copyObject(copyObjectRequest);
            System.out.println(example);
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }

    /**
     * 复制对象, 服务端对对象进行加密，使用SSE-KMS加密
     */
    @Test
    public void copyObject_SSE_C() {
        HOS hos = getHOSClient();
        // 构建复制对象请求参数
        CopyObjectRequest copyObjectRequest = new CopyObjectRequest("example", "2022/08/15/b.txt"
                , "example", "2022/08/15/c.txt");
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setObjectDirective("COPY");
        copyObjectRequest.setNewObjectMetadata(objectMetadata);
        // 设置加密参数
        copyObjectRequest.setClientSideEncryptionAlgorithm(AlgorithmEnum.AES256);
        copyObjectRequest.setClientSideEncryptionKey(StringUtils.getPointLengthUUID(32));

        try {
            CopyObjectResult example = hos.copyObject(copyObjectRequest);
            System.out.println(example);
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }


    /**
     * 解冻或恢复对象
     */
    @Test
    public void restoreObject2() throws IOException {
        HOS hos = getHOSClient();
        RestoreConfiguration restoreConfiguration = new RestoreConfiguration(1);
        RestoreObjectResult example = hos.restoreObject("bucket", "2023/04/20/aaa.txt", restoreConfiguration);

        // 如果解冻成功，下面进行下载操作
        if (example.getResponse().isSuccessful()) {
            System.out.println("解冻成功,因为解冻需要时间，所以请稍候才可以进行下载，调用下面的downRestoreObject()方法进行下载");
        }
    }

    /**
     * 解冻或恢复对象
     */
    @Test
    public void restoreObject() throws IOException {
        HOS hos = getHOSClient();

        /**
         * 上传一个ARCHIVE对象
         */

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setObjectStorageClass(StorageClass.ARCHIVE);
        PutObjectRequest putObjectRequest = new PutObjectRequest("bucket1", "2022/08/24/a.txt"
                , new FileInputStream("test1.txt"), objectMetadata);
        PutObjectResult putObjectResult = hos.putObject(putObjectRequest);
        if (putObjectResult.getResponse().isSuccessful()) {
            System.out.println("上传成功");
            // 下载对象 就会报出不能下载的错误，所以必须解冻对象
            DownloadFileRequest downloadFileRequest = new DownloadFileRequest("bucket1", "2022/08/24/a.txt");
            downloadFileRequest.setDownloadFile("E:\\比洛巴乔\\Desktop\\123456.txt");
            try {
                hos.downloadObject(downloadFileRequest);
            } catch (Throwable ServiceException) {
//                System.out.println(ServiceException.getMessage());
                // 解冻对象处理
                RestoreConfiguration restoreConfiguration = new RestoreConfiguration(1);
                RestoreObjectResult example = hos.restoreObject("bucket1", "2022/08/24/a.txt", restoreConfiguration);

                // 如果解冻成功，下面进行下载操作
                if (example.getResponse().isSuccessful()) {
                    System.out.println("解冻成功,因为解冻需要时间，所以请稍候才可以进行下载，调用下面的downRestoreObject()方法进行下载");
                }
            }
        }
    }

    /**
     * 下载解冻对象
     * @throws IOException
     */
    @Test
    public void downRestoreObject() throws IOException {
        HOS hos = getHOSClient();
//        ClassPathResource classPathResource = new ClassPathResource("testFile/test1.txt");
//        File file = classPathResource.getFile();
//        GetObjectRequest getObjectRequest = new GetObjectRequest("bucket1", "2022/08/24/a.txt");
//        getObjectRequest.setIncludeInputStream(true);
//        HOSObject example = hos.getObject(getObjectRequest);
//        InputStream objectContent = example.getObjectContent();
//        IOUtils.writeOutFile(objectContent, new File(file.getParent(), UUID.randomUUID() + ".txt").getAbsolutePath());
    }

    /**
     * 初始化文件上传
     */
    @Test
    public void initiateMultipartUpload() {
        HOS hos = getHOSClient();
        // 构建初始化上传请求参数对象
        InitiateMultipartUploadRequest initiateMultipartUploadRequest =
                new InitiateMultipartUploadRequest("bucket", "2022/08/3123/4.avi");
        try {
            InitiateMultipartUploadResult initiateMultipartUploadResult =
                    hos.initiateMultipartUpload(initiateMultipartUploadRequest);
            System.out.println(initiateMultipartUploadResult);
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }

    /**
     * 初始化文件上传  服务端对象进行加密    SSE-KMS方式，完成上传完的对象就会是加密的了
     */
    @Test
    public void initiateMultipartUpload_SSE_KMS() {
        HOS hos = getHOSClient();
        // 创建初始化任务上传
        InitiateMultipartUploadRequest initiateMultipartUploadRequest = new InitiateMultipartUploadRequest("example", "2022/08/16/2.avi");
        // 设置服务端加密
        initiateMultipartUploadRequest.setServerSideEncryption("here:kms");
        initiateMultipartUploadRequest.setServerSideEncryptionKeyID(secretId);

        try {
            InitiateMultipartUploadResult initiateMultipartUploadResult = hos.initiateMultipartUpload(initiateMultipartUploadRequest);
            System.out.println(initiateMultipartUploadResult);
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }

    /**
     * 初始化文件上传  服务端对象进行加密    SSE-C方式，完成上传完的对象就会是加密的了
     */
    @Test
    public void initiateMultipartUpload_SSE_C() {
        HOS hos = getHOSClient();
        // 构建初始化任务上传参数
        InitiateMultipartUploadRequest initiateMultipartUploadRequest =
                new InitiateMultipartUploadRequest("example", "2022/08/31/2.avi");
        // 设置加密参数
        initiateMultipartUploadRequest.setClientSideEncryptionAlgorithm(AlgorithmEnum.AES256);
        initiateMultipartUploadRequest.setClientSideEncryptionKey(StringUtils.getPointLengthUUID(32));
        try {
            InitiateMultipartUploadResult initiateMultipartUploadResult =
                    hos.initiateMultipartUpload(initiateMultipartUploadRequest);
            System.out.println(initiateMultipartUploadResult);
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }

    /**
     * 分片上传以及完成上传
     */
    @Test
    public void partUpload() throws IOException {
        long start = System.currentTimeMillis();
        HOS hos = getHOSClient();
        try {
            // 分片上传、完成上传的基础信息
            String bucketName = "ncoss-3";
            String key = "aa.avi";

            // 构建初始化上传请求参数对象
            InitiateMultipartUploadRequest initiateMultipartUploadRequest =
                    new InitiateMultipartUploadRequest(bucketName, key);
            InitiateMultipartUploadResult initiateMultipartUploadResult =
                    hos.initiateMultipartUpload(initiateMultipartUploadRequest);

            String uploadId = initiateMultipartUploadResult.getUploadId();
            // 每个分片的大小，用于计算文件有多少个分片。单位为字节。
            final long partSize = 1024 * 128;   //5 MB。
            // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件。
            final File sampleFile = new File("E:\\比洛巴乔\\Desktop\\狂神说JUC笔记.pdf");
            long fileLength = sampleFile.length();
            // 计算需要分多少片上传
            int partCount = (int) (fileLength / partSize);
            if (fileLength % partSize != 0) {
                partCount++;
            }

            // partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
            List<PartETag> partETags = new ArrayList<>();

            // 遍历分片上传。
            for (int i = 0; i < partCount; i++) {
                long startPos = i * partSize;
                long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
                InputStream instream = new FileInputStream(sampleFile);
                // 跳过已经上传的分片。
                instream.skip(startPos);
                // 创建分片上传对象，并且将相关属性赋值
                UploadPartRequest uploadPartRequest = new UploadPartRequest(bucketName, key);
                uploadPartRequest.setUploadId(uploadId);
                uploadPartRequest.setInputStream(instream);
                // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB,默认值为5MB
                uploadPartRequest.setPartSize(curPartSize);
                // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出此范围，HOS将返回InvalidArgument错误码。
                uploadPartRequest.setPartNumber(i + 1);
                // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，HOS会按照分片号排序组成完整的文件。
                UploadPartResult uploadPartResult = hos.uploadPart(uploadPartRequest);
                System.out.println(uploadPartResult);
                // 每次上传分片之后，HOS的返回结果包含PartETag。PartETag将被保存在partETags中。
                partETags.add(new PartETag(uploadPartResult.getPartNumber(), uploadPartResult.getETag(), uploadPartResult.getPartSize()));
            }
            // 完成上传参数对象构建
            CompleteMultipartUploadRequest completeMultipartUploadRequest =
                    new CompleteMultipartUploadRequest(bucketName, key, uploadId, partETags);
            // 完成上传
            CompleteMultipartUploadResult completeMultipartUploadResult = hos.completeMultipartUpload(completeMultipartUploadRequest);
            System.out.println(completeMultipartUploadResult);
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }

    /**
     * 上传段对象/分片上传   使用sse-c加密
     */
    @Test
    public void partUpload_SSE_C() throws IOException {

        HOS hos = getHOSClient();
        String bucketName = "example";
        String key = "2022/08/26/2.avi";
        String uploadId = "5813bc74250411edbeca955b70957396";

        // 每个分片的大小，用于计算文件有多少个分片。单位为字节。
        final long partSize = 5 * 1024 * 1024L;   //1 MB。
        // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件。
        final File sampleFile = new File("E:\\比洛巴乔\\Videos\\123\\1.avi");
        long fileLength = sampleFile.length();
        int partCount = (int) (fileLength / partSize);
        if (fileLength % partSize != 0) {
            partCount++;
        }

        // partETags是PartETag的集合。PartETag由分片的ETag和分片号组成。
        List<PartETag> partETags = new ArrayList<>();

        try {
            // 遍历分片上传。
            for (int i = 0; i < partCount; i++) {
                long startPos = i * partSize;
                long curPartSize = (i + 1 == partCount) ? (fileLength - startPos) : partSize;
                InputStream instream = new FileInputStream(sampleFile);
                // 跳过已经上传的分片。
                instream.skip(startPos);
                UploadPartRequest uploadPartRequest = new UploadPartRequest();

                // 客户端加密
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setClientSideEncryptionAlgorithm(AlgorithmEnum.AES256);
                objectMetadata.setClientSideEncryptionKey(secretId);
                uploadPartRequest.setObjectMetadata(objectMetadata);


                uploadPartRequest.setBucketName(bucketName);
                uploadPartRequest.setKey(key);
                uploadPartRequest.setUploadId(uploadId);
                uploadPartRequest.setInputStream(instream);
                // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
                uploadPartRequest.setPartSize(curPartSize);
                // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出此范围，HOS将返回InvalidArgument错误码。
                uploadPartRequest.setPartNumber(i + 1);
                // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，HOS会按照分片号排序组成完整的文件。
                UploadPartResult uploadPartResult = hos.uploadPart(uploadPartRequest);
                System.out.println(uploadPartResult);
                // 每次上传分片之后，HOS的返回结果包含PartETag。PartETag将被保存在partETags中。
                partETags.add(new PartETag(uploadPartResult.getPartNumber(), uploadPartResult.getETag(), uploadPartResult.getPartSize()));
            }

            CompleteMultipartUploadRequest completeMultipartUploadRequest =
                    new CompleteMultipartUploadRequest(bucketName, key, uploadId, partETags);
            CompleteMultipartUploadResult completeMultipartUploadResult = hos.completeMultipartUpload(completeMultipartUploadRequest);
            System.out.println(completeMultipartUploadResult.getETag());
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }

    /**
     * 列举出已经上传的分片列表
     */
    @Test
    public void listParts() {
        // 构建hos连接
        HOS hos = getHOSClient();
        // 桶名称
        String bucketName = "bucket";
        // 对象名称
        String key = "1677485316947";
        // 上传唯一ID
        String uploadId = "2dfc7490b67611ed98cdcb0dd09ad600";
        // 创建并且赋值查询分片列表请求参数对象
        ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, key, uploadId);
        try {
            PartListing partListing = hos.listParts(listPartsRequest);
            partListing.getParts().forEach(System.out::println);
//            System.out.println(partListing);
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }

    }

    /**
     * 断点续传
     */
    @Test
    public void uploadFile() throws Throwable {
        // 创建HOSClient实例。
        HOS hos = getHOSClient();

//        File file = new File("E:\\devData\\home-space\\myStudy\\bigcomponent\\hadoop\\hadoop-branch-3.3.1\\BP-345915182-172.26.68.47-1692606038655\\current\\rbw\\blk_1073741825");
        File file = new File("E:\\比洛巴乔\\Downloads\\10.86.16.11_3336_membership20240627_03_02.sql");

        try {
            ObjectMetadata meta = new ObjectMetadata();
            // 指定上传的内容类型。
            meta.setContentType(new MimetypesFileTypeMap().getContentType(file));

            // 文件上传时设置访问权限ACL。
//             meta.setObjectAcl(CannedAccessControlList.Private);

            // 通过UploadFileRequest设置多个参数。
            // 依次填写Bucket名称（例如examplebucket）以及Object完整路径（例如exampledir/exampleobject.txt），Object完整路径中不能包含Bucket名称。
//            UploadObjectRequest uploadObjectRequest = new UploadObjectRequest("bucket", "BP-1786105931-172.26.68.47-1692603292363/current/finalized/subdir0/subdir0/blk_1073741825");
            UploadObjectRequest uploadObjectRequest = new UploadObjectRequest("bucket", "10.86.16.11_3336_membership20240627_03_02.sql");

            // 通过UploadFileRequest设置单个参数。
            // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件。
            uploadObjectRequest.setUploadFile(file.getAbsolutePath());
            // 指定上传并发线程数，默认值为1。
            uploadObjectRequest.setTaskNum(10);
            // 指定上传的分片大小，单位为字节，取值范围为100 KB~5 GB。默认值为100 KB。
//            uploadObjectRequest.setPartSize(10 * 1024 * 1024);
            uploadObjectRequest.setPartSize(215247854);
            // 开启断点续传，默认关闭。
            uploadObjectRequest.setEnableCheckpoint(true);
            // 记录本地分片上传结果的文件。上传过程中的进度信息会保存在该文件中，如果某一分片上传失败，再次上传时会根据文件中记录的点继续上传。上传完成后，该文件会被删除。
            // 如果未设置该值，默认与待上传的本地文件同路径，名称为${uploadFile}.ucp。
//            uploadFileRequest.setCheckpointFile("yourCheckpointFile");
            // 文件的元数据。
            uploadObjectRequest.setObjectMetadata(meta);
            // 设置上传回调，参数为Callback类型。
            //uploadFileRequest.setCallback("yourCallbackEvent");

            // 断点续传上传。
            CompleteMultipartUploadResult uploadFileResult = hos.uploadFile(uploadObjectRequest);

        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } finally {
            // 关闭HOSClient。
            if (hos != null) {
                hos.shutdown();
            }
        }
    }


    /**
     * 多段对象——列出已初始化的分段上传
     */
    @Test
    public void listMultipartUploads() {
        HOS hos = getHOSClient();
        ListMultipartUploadsRequest listMultipartUploadsRequest = new ListMultipartUploadsRequest("bucket");
        try {
//            listMultipartUploadsRequest.setMaxKeys(3);
            listMultipartUploadsRequest.setPrefix("2022/08/3123/4.av");
            MultipartUploadListing multipartUploadListing = hos.listMultipartUploads(listMultipartUploadsRequest);
            System.out.println(multipartUploadListing);
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }

    /**
     * 多段对象——终止分段上传
     */
    @Test
    public void deleteMultipartUploads() {
        HOS hos = getHOSClient();
        // 桶名称
        String bucketName = "example";
        // 对象名称
        String key = "2022/08/31/2.avi";
        // 上传唯一ID
        String uploadId = "94b398ee299a11ed8e799337afa1a09c";
        AbortMultipartUploadRequest abortMultipartUploadRequest = new AbortMultipartUploadRequest(bucketName, key, uploadId);
        try {
            VoidResult result = hos.abortMultipartUpload(abortMultipartUploadRequest);
            if (result.getResponse().isSuccessful()) {
                System.out.println("删除成功");
            }
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (hos != null) {
                hos.shutdown();
            }
        }
    }
}
