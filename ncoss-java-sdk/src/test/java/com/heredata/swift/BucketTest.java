package com.heredata.swift;

import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import com.heredata.model.VoidResult;
import com.heredata.swift.model.*;
import com.heredata.swift.model.bucket.Bucket;
import com.heredata.swift.model.bucket.BucketAclRequest;
import com.heredata.swift.model.bucket.BucketQuotaResult;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * bucket test
 * @author wuzezhao
 * @since 2022/8/4
 */
public class BucketTest extends TestBase {

    /**
     * 创建桶
     */
    @Test
    public void createBucket() {
        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        try {
            // 创建请求对象，并且设置创建桶名为"example"的桶，设置配额信息
            CreateBucketRequest createBucketRequest = new CreateBucketRequest("example", 1024 * 1024 * 1024L, 100);
            // 设置ACL权限
            BucketAclRequest bucketAclRequest = new BucketAclRequest();
            // 设置所有用户都可以访问桶内对象，无需令牌
            bucketAclRequest.setAllUserReadObject(true);
            // 设置所有用户都可以访问桶权限
            bucketAclRequest.setHeadOrGetBukcet(true);
            // 设置指定账户下的所有用户拥有访问对象的权限
            bucketAclRequest.addTokenReadKeyValue(new KeyValue("admin", "*"));
            // 设置指定账户下的指定用户拥有操作对象的权限
            bucketAclRequest.addTokenWriteKeyValue(new KeyValue("admin", "*"));
            createBucketRequest.setBucketAclRequest(bucketAclRequest);
            // 调用方法创建桶并设置配额和ACL
            VoidResult result = swift.createBucket(createBucketRequest);
            if (result.getResponse().isSuccessful()) {
                System.out.println("创建成功");
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
     * 简单创建一个桶
     */
    @Test
    public void createBucketSimple() {
        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        try {
            // 创建请求对象，并且设置创建桶名为"example"的桶
            CreateBucketRequest createBucketRequest = new CreateBucketRequest("example");
            VoidResult result = swift.createBucket(createBucketRequest);
            if (result.getResponse().isSuccessful()) {
                System.out.println("创建成功");
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
     * 查询桶详情  返回指定桶的详细，包括桶下对象个数、对象字节数统计。
     */
    @Test
    public void getBucketDetail() {
        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        try {
            // 查询桶名为"example"的详情
            Bucket bucket = swift.getBucket("bucket");
            System.out.println(bucket);
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
     * 查询桶内对象列表
     */
    @Test
    public void getBucketObjectList() {
        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        try {
            // 查询桶名为"example"的详情
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest("example");
            ObjectListing objectListing = swift.listObjects(listObjectsRequest);
            System.out.println(objectListing);
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
     * 删除桶
     * 删除指定桶，只有桶内对象为空时才可以删除
     * 只要该桶下有对象的话，就无法删除
     */
    @Test
    public void deleteBucket() {
        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        try {
            VoidResult result = swift.deleteBucket("example");
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
            if (swift != null) {
                swift.shutdown();
            }
        }
    }

    /**
     * 设置或更新桶的自定义元数据
     */
    @Test
    public void setOrUpdateBucketMeta() {
        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        SetBucketMetaRequest setBucketMetaRequest = new SetBucketMetaRequest();
        // 设置桶名称
        setBucketMetaRequest.setBucketName("example");
        try {
            // 元数据map集合
            Map<String, String> aa = new HashMap<>();
            aa.put("metaKey3", "metaValue3");
            aa.put("metaKey4", "metaValue4");
            setBucketMetaRequest.setUserMeta(aa);
            // 需要删除哪些元数据
            List<String> needRemoveMeta = new ArrayList<>();
            needRemoveMeta.add("metaKey1");
            setBucketMetaRequest.setNeedRemoveMeta(needRemoveMeta);
            // 调用功能进行元数据处理
            VoidResult result = swift.setBucketMeta(setBucketMetaRequest);
            System.out.println(result);
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
     * 删除桶的自定义元数据
     */
    @Test
    public void deleteBucketMeta() {
        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        try {
            // 需要删除哪些元数据
            List<String> needRemoveMeta = new ArrayList<>();
            needRemoveMeta.add("metaKey3");
            // 调用功能进行元数据处理
            VoidResult result = swift.deleteBucketMeta("example", needRemoveMeta);
            System.out.println(result);
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
     * 设置桶配额：这个桶只能上传这么多个文件和这些总文件的大小不能超过总和
     * 桶配额值必须为非负整数。默认配额为0，表示没有限制配额。配额设置后，如果想取消配额限制，可以把配额设置为0，
     * 桶配额提供 storageQuota（字节数限制，单位为Byte）
     * StorageMaxCount（桶内对象 个数限制，非负整数）两项配置
     */
    @Test
    public void setOrUpdateBucketQuota() {
        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        // 配置设置配额请求参数对象
        SetBucketQuotaRequest setBucketQuota = new SetBucketQuotaRequest(0L, 0);
        setBucketQuota.setBucketName("example");
        try {
            VoidResult result = swift.setBucketQuota(setBucketQuota);
            System.out.println(result);
        } catch (ServiceException oe) {
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Error Message:" + ce.getMessage());
        }
        finally {
            if (swift != null) {
                swift.shutdown();
            }
        }
    }

    /**
     * 删除桶的配额
     */
    @Test
    public void deleteBucketQuota() {
        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        try {
            VoidResult result = swift.deleteBucketQuota("example2", true, true);
            System.out.println(result);
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
     * 查询桶配额
     */
    @Test
    public void getBucketQuota() {
        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        try {
            BucketQuotaResult result = swift.getBucketQuota("example");
            System.out.println(result);
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
     * 设置桶ACL
     */
    @Test
    public void setBucketAcl() {
        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        try {
            BucketAclRequest bucketAclRequest = new BucketAclRequest();
            // 设置桶名称
            bucketAclRequest.setBucketName("example");
            // 设置ACL权限
            bucketAclRequest.setAllUserReadObject(true);
            bucketAclRequest.setHeadOrGetBukcet(true);

            bucketAclRequest.addTokenReadKeyValue(new KeyValue("admin", "9ef3db5428f311edbc330391d2a979b2"));
            bucketAclRequest.addTokenReadKeyValue(new KeyValue("admin", "*"));

            bucketAclRequest.addTokenWriteKeyValue(new KeyValue("admin", "9ef3db5428f311edbc330391d2a979b2"));
            bucketAclRequest.addTokenWriteKeyValue(new KeyValue("admin", "*"));

            VoidResult result = swift.setBucketAcl(bucketAclRequest);
            if (result.getResponse().isSuccessful()) {
                System.out.println("创建成功");
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
     * 删除桶ACL
     */
    @Test
    public void deleteBucketAcl() {
        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        try {
            VoidResult result = swift.deleteBukcetAcl("example", true, true);
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
        }
        finally {
            if (swift != null) {
                swift.shutdown();
            }
        }
    }

}
