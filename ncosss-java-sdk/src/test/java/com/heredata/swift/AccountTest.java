package com.heredata.swift;

import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import com.heredata.swift.model.AccountInfo;
import com.heredata.swift.model.bucket.BucketListRequest;
import com.heredata.swift.model.bucket.BukcetListResult;
import org.junit.Test;


/**
 * account test
 */
public class AccountTest extends TestBase {

    /**
     * 当前账户下的桶列表
     */
    @Test
    public void queryBucketList() {
        // 构建桶列表请求对象    查询所有桶列表
        BucketListRequest queryAllBucket = new BucketListRequest();
        // 构建桶列表请求对象    限制桶数量
        BucketListRequest limitBucketCount = new BucketListRequest();
        limitBucketCount.setLimit(2);
        // 构建桶列表请求对象    限制桶名称大于某个值
        BucketListRequest startAfterBucket = new BucketListRequest();
        startAfterBucket.setStartAfter("a");
        // 构建桶列表请求对象    限制桶名称前缀
        BucketListRequest prefixBucket = new BucketListRequest();
        prefixBucket.setPrefix("a");
        // 构建桶列表请求对象    限制桶查询数量,桶名称大于某值,桶名称前缀
        BucketListRequest bucketListRequest = new BucketListRequest(2, "a", "a");

        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        try {
            // 桶列表查询
            BukcetListResult queryAllBucketResult = swift.listBuckets(queryAllBucket);
            queryAllBucketResult.getBucketList().forEach(System.out::println);
            System.out.println("==================================================================================");
//            // 限制桶列表数量查询
//            BukcetListResult limitBucketCountResult = swift.listBuckets(limitBucketCount);
//            limitBucketCountResult.getBucketList().forEach(System.out::println);
//            System.out.println("==================================================================================");
//            // 限制桶列表桶名称大于某个值
//            BukcetListResult startAfterBucketResult = swift.listBuckets(startAfterBucket);
//            startAfterBucketResult.getBucketList().forEach(System.out::println);
//            System.out.println("==================================================================================");
//            // 限制桶列表桶名称前缀
//            BukcetListResult prefixBucketResult = swift.listBuckets(prefixBucket);
//            prefixBucketResult.getBucketList().forEach(System.out::println);
//            System.out.println("==================================================================================");
//            // 限制桶列表查询数量、桶名称大于某值，桶名称前缀
//            BukcetListResult bucketListRequestResult = swift.listBuckets(bucketListRequest);
//            bucketListRequestResult.getBucketList().forEach(System.out::println);
//            System.out.println("==================================================================================");
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
     * 分页查询账户下桶列表
     */
    @Test
    public void queryBucketListByPage() {
        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        int pageSize = 2;
        int curPage = 0;
        String startAfter = null;
        try {
            while (true) {
                // 构建获取账户详情以及桶列表请求对象
                BucketListRequest bucketListRequest = new BucketListRequest();
                // 约束查询出桶的名称大于该值
                bucketListRequest.setStartAfter(startAfter);
                // 限制查询出的数量
                bucketListRequest.setLimit(pageSize);
                // 限制查询桶的前缀
                // accountInfoBucketsListRequest.setPrefix("prefix");
                // 查询账户详情
                BukcetListResult accountInfo = swift.listBuckets(bucketListRequest);
                if (accountInfo.getBucketList().size() < pageSize) {
                    break;
                } else {
                    startAfter = accountInfo.getBucketList().get(pageSize - 1).getBucketName();
                }
                System.out.println("=======================第 " + (++curPage) + " 页=====================");
                accountInfo.getBucketList().forEach(System.out::println);
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
     * 显示帐户的元数据，帐户的元数据包括：
     * 桶个数
     * 对象个数
     * 帐户存储在对象存储中的字节总数
     */
    @Test
    public void getAccount() {
        /**
         * endPoint：Swift的基础路径(公共前缀)
         * account：账户的ID
         * xSubjectToken：向UAAS服务请求到得到的token
         */
        Swift swift = new SwiftClientBuilder().build(endPoint, account, xSubjectToken);
        try {
            AccountInfo accountResult = swift.getAccount();
            System.out.println(accountResult);
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
