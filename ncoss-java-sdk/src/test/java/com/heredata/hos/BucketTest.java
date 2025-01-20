package com.heredata.hos;

import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import com.heredata.hos.model.*;
import com.heredata.hos.model.bucket.Bucket;
import com.heredata.hos.model.bucket.BucketQuotaResult;
import com.heredata.hos.model.bucket.BucketVersioningConfiguration;
import com.heredata.model.VoidResult;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.heredata.hos.model.bucket.BucketVersioningConfiguration.ENABLED;

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
        HOS hos = getHOSClient();
        try {
            // 创建请求对象，并且设置创建桶名为"example"的桶
            CreateBucketRequest createBucketRequest = new CreateBucketRequest("ncossndfcbv-bvcvbbvc");
            VoidResult result = hos.createBucket(createBucketRequest);

            createBucketRequest = new CreateBucketRequest("ncosjhgjfdgsdf-bv");
            hos.createBucket(createBucketRequest);

            createBucketRequest = new CreateBucketRequest("ncos645654sdf-bv");
            hos.createBucket(createBucketRequest);

            createBucketRequest = new CreateBucketRequest("ncosxzczadsdf-bv");
            hos.createBucket(createBucketRequest);

            createBucketRequest = new CreateBucketRequest("ncosduyretsdf-bv");
            hos.createBucket(createBucketRequest);

            createBucketRequest = new CreateBucketRequest("ncoss546df-bv");
            hos.createBucket(createBucketRequest);

            createBucketRequest = new CreateBucketRequest("ncosasdsdf-bv");
            hos.createBucket(createBucketRequest);

            createBucketRequest = new CreateBucketRequest("ncosxzcsdf-bv");
            hos.createBucket(createBucketRequest);

            createBucketRequest = new CreateBucketRequest("ncosfdgsdf-bv");
            hos.createBucket(createBucketRequest);

            createBucketRequest = new CreateBucketRequest("nco213ssdf-bv");
            hos.createBucket(createBucketRequest);

            createBucketRequest = new CreateBucketRequest("ncossqwedf-bv");
            hos.createBucket(createBucketRequest);

            createBucketRequest = new CreateBucketRequest("ncossbfbdf-bv");
            hos.createBucket(createBucketRequest);

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
            if (hos != null) {
                hos.shutdown();
            }
        }
    }

    /**
     * 查询桶详情  返回指定桶的详细，包括桶下对象个数、对象字节数统计。
     */
    @Test
    public void getBucketDetail() {
        HOS hos = getHOSClient();
        try {
            if (hos.doesBucketExist("bucket1")) {

                //查询桶名为"example"的详情
                Bucket bucket = hos.getBucketInfo("bucket1");
                System.out.println(bucket);
            } else {
                System.out.println("桶不存在");
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
     * 删除桶
     * 删除指定桶，只有桶内对象为空时才可以删除
     * 只要该桶下有对象或者初始化多段上传任务的话，就无法删除
     */
    @Test
    public void deleteBucket() {
        HOS hos = getHOSClient();
        try {
            VoidResult result = hos.deleteBucket("my-bucketname");
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

    /**
     * 设置桶配额：这个桶只能上传这么多个文件和这些总文件的大小不能超过总和
     * 桶配额值必须为非负整数。默认配额为0，表示没有限制配额。配额设置后，如果想取消配额限制，可以把配额设置为0，
     * 桶配额提供 storageQuota（字节数限制，单位为Byte）
     * StorageMaxCount（桶内对象 个数限制，非负整数）两项配置
     */
    @Test
    public void setBucketQuota() {
        HOS hos = getHOSClient();
        // 配置设置配额请求参数对象
        SetBucketQuotaRequest setBucketQuota = new SetBucketQuotaRequest(0L, 0);
        setBucketQuota.setBucketName("ncoss-4");
        try {
            VoidResult result = hos.setBucketQuota(setBucketQuota);
            if (result.getResponse().isSuccessful()) {
                System.out.println("设置桶配额成功");
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
     * 查询桶配额
     */
    @Test
    public void getBucketQuota() {
        HOS hos = getHOSClient();
        try {
            BucketQuotaResult result = hos.getBucketQuota("ncoss-4");
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
     * 设置桶策略
     * 默认情况下资源（桶和对象）都是私有的，只有资源拥有者可以访问资源，
     * 其他用户在未经授权的情况下均无HOS访问权限。通过编写访问策略向其他帐户或者UAAS用户授予资源的控制权限。
     * 详细参数文档请参照使用手册
     */
    @Test
    public void setBucketPolicy() {
        HOS hos = getHOSClient();
        // 桶策略字符串
        String policyText = "{\"Version\":\"2023-04-04\",\"Statement\":[{\"Sid\":\"Stmt1375240018061\",\"Action\":[\"PutBucketAcl\"]" +
                ",\"Effect\":\"Allow\",\"Resource\":[\"example\"],\"Principal\":{\"HWS\":[\"test_pro1:root\"]}}]}";
//        String policyText = "{\"Version\": \"2023-04-25\",\"Statement\": [{\"Action\": [\"ListBucket\",\"HeadBucket\"],\"Effect\": \"Allow\",\"Principal\": {\"HWS\": [\"*\"]},\"Resource\": [\"jdsdk\"],\"Sid\": \"\"},{\"Action\": [\"GetObject\"],\"Effect\": \"Allow\",\"Principal\": {\"HWS\": [\"*\"]},\"Resource\": [\"jdsdk\"],\"Sid\": \"\"}]}";
        try {
            VoidResult result = hos.setBucketPolicy("ncoss-4", policyText);
            if (result.getResponse().isSuccessful()) {
                System.out.println("设置桶策略成功");
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
     * 查询桶策略
     */
    @Test
    public void getBucketPolicy() {
        HOS hos = getHOSClient();
        try {
            GetBucketPolicyResult bucket1 = hos.getBucketPolicy("ncoss-4");
            System.out.println(bucket1);
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
     * 删除桶策略
     */
    @Test
    public void deleteBucketPolicy() {
        HOS hos = getHOSClient();
        try {
            VoidResult bucket1 = hos.deleteBucketPolicy("ncoss-4");
            System.out.println(bucket1);
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
     * 设置桶ACL：对桶操作进行权限控制。
     * 默认情况下，只有桶的拥有者才有该桶的读写权限。
     * 用户也可以设置其他的访问策略，比如对一个桶可以设置公共访问策略，允许所有人对其都有读权限，
     * 此操作属于覆盖操作，新的配置将覆盖桶已存在的ACL配置，
     */
    @Test
    public void setBucketACL() {
        HOS hos = getHOSClient();
        // ACL权限容器
        AccessControlList accessControlList = new AccessControlList();

        // 所有人包括匿名用户有可读权限
        Grantee grantee = GroupGrantee.AllUsers;
        Permission permission = Permission.READ;
        accessControlList.grantPermission(grantee, permission);

        // 对于指定的人有可写权限
        grantee = new CanonicalUserGrantee("7c9dfff2139b11edbc330391d2a979b2");
        permission = Permission.WRITE;
        accessControlList.grantPermission(grantee, permission);

        // 将权限信息添加到容器中
        SetAclRequest setBucketAclRequest = new SetAclRequest();
        setBucketAclRequest.setAccessControlList(accessControlList);
        Owner owner = new Owner(accountId);
        setBucketAclRequest.setOwner(owner);
        try {
            // 设置
            VoidResult result = hos.setBucketAcl("ncoss-4", setBucketAclRequest);
            if (result.getResponse().isSuccessful()) {
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
     * 设置桶默认ACL权限：允许所有人匿名访问（读）
     */
    @Test
    public void setBucketDefaultACL() {
        HOS hos = getHOSClient();
        try {
            // 设置
            VoidResult result = hos.setDefaultConfigBucketAcl("ncoss-4");
            if (result.getResponse().isSuccessful()) {
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
     * 查询桶ACL
     */
    @Test
    public void getBucketACL() {
        HOS hos = getHOSClient();
        try {
            AccessControlList result = hos.getBucketAcl("ncoss-4");
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
     * 开启/关闭桶的版本状态
     * 多版本功能可在用户意外覆盖或删除对象的情况下提供一种恢复手段。
     * 用户可以使用多版本功能来保存、检索和还原对象的各个版本，这样用户能够从意外操作或应用程序故障中轻松恢复数据。
     * 多版本功能还可用于数据保留和存档。
     *
     *默认情况下，桶没有开启多版本功能，当多版本状态开启后就无法关闭，只能暂停或恢复桶的多版本状态。
     */
    @Test
    public void setBucketVersioning() {
        HOS hos = getHOSClient();

        // 创建桶生命周期配置类
        BucketVersioningConfiguration bucketVersioningConfiguration = new BucketVersioningConfiguration();
        bucketVersioningConfiguration.setStatus(ENABLED);
        // 创建请求对象
        SetBucketVersioningRequest setBucketVersioningRequest = new SetBucketVersioningRequest("ncoss-4", bucketVersioningConfiguration);
        try {
            // 设置桶生命周期
            VoidResult result = hos.setBucketVersioning(setBucketVersioningRequest);
            if (result.getResponse().isSuccessful()) {
                System.out.println("设置桶版本状态成功");
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
     * 查询桶的版本状态
     */
    @Test
    public void getBucketVersioning() {
        HOS hos = getHOSClient();
        try {
            BucketVersioningConfiguration result = hos.getBucketVersioning("ncoss-4");
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
     * 设置桶生命周期配置
     * 通过支持指定规则来实现定时删除或迁移桶中对象，
     * 如果桶已存在生命周期配置则将覆盖之前的配置，
     */
    @Test
    public void setBucketLifecycle() {
        HOS hos = getHOSClient();

        SetBucketLifecycleRequest setBucketLifecycleRequest = new SetBucketLifecycleRequest("ncoss-10");
        LifecycleRule lifecycleRule = new LifecycleRule();
        // 设置过滤器，对哪些对象进行生命周期设置
        LifecycleRule.Filter filter = new LifecycleRule.Filter();
        filter.setPrefix("2023");
        lifecycleRule.setFilter(filter);
        // 设置object的存储类型
        lifecycleRule.setStatus(LifecycleRule.RuleStatus.Enabled);
        // 设置当前版本对象的过期配置
        LifecycleRule.Expiration expiration = new LifecycleRule.Expiration(90, null);
        lifecycleRule.setExpiration(expiration);
        // 设置30天以后转换为ARCHIVE类型
        LifecycleRule.Transition transition = new LifecycleRule.Transition(30, StorageClass.ARCHIVE);
        lifecycleRule.getTransitions().add(transition);
        // 将生命周期规则添加集合中
        setBucketLifecycleRequest.getLifecycleRules().add(lifecycleRule);

        try {
            // 调用方法设置桶的生命周期
            VoidResult result = hos.setBucketLifecycle(setBucketLifecycleRequest);
            if (result.getResponse().isSuccessful()) {
                System.out.println("设置桶生命周期成功");
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
     * 查询桶生命周期配置
     */
    @Test
    public void getBucketLifecycle() {
        HOS hos = getHOSClient();
        try {
            List<LifecycleRule> result = hos.getBucketLifecycle("ncoss-10");
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
     * 删除桶生命周期配置
     */
    @Test
    public void deleteBucketLifecycle() {
        HOS hos = getHOSClient();
        try {
            VoidResult result = hos.deleteBucketLifecycle("rocke");
            if (result.getResponse().isSuccessful()) {
                System.out.println("删除桶生命周期成功");
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
     * 桶标签
     * 通过此操作可以给桶添加标签配置，如果桶已存在标签配置则覆盖之前的标签配置属于覆盖操作，标签最多支持10对
     */
    @Test
    public void bucketTagging() {
        HOS hos = getHOSClient();

        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        SetBucketTaggingRequest setBucketTaggingRequest = new SetBucketTaggingRequest("ncoss-4", map);
        try {
            // 设置标签
            VoidResult result = hos.setBucketTagging(setBucketTaggingRequest);
            if (result.getResponse().isSuccessful()) {
                System.out.println("设置标签成功");
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
     * 查询桶标签
     */
    @Test
    public void getBucketTagging() {
        HOS hos = getHOSClient();
        try {
            TagSet result = hos.getBucketTagging("ncoss-4");
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
     * 删除桶标签
     */
    @Test
    public void deleteBucketTagging() {
        HOS hos = getHOSClient();
        try {
            VoidResult result = hos.deleteBucketTagging("ncoss-4");
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
