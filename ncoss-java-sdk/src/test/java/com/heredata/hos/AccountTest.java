package com.heredata.hos;

import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import com.heredata.hos.model.AccountInfo;
import com.heredata.hos.model.ListBucketsRequest;
import com.heredata.hos.model.SetAccountQuotaRequest;
import com.heredata.hos.model.bucket.Bucket;
import com.heredata.hos.model.bucket.BucketList;
import com.heredata.model.VoidResult;
import org.junit.Test;

import java.util.List;


/**
 * account test
 */
public class AccountTest extends TestBase {


    /**
     * 账户类操作，查询账户详情、设置配额、查询配额
     */
    @Test
    public void queryAccount() {
        /**
         * endPoint：HOS的基础路径(公共前缀)
         * account：账户的ID
         * accessKey：向UAAS服务请求到的access_key
         * secretKey：向UAAS服务请求到的secret_key
         */
        HOS hos = getHOSClient();
        try {
            // 查询账户详情
            AccountInfo accountInfo = hos.getAccountInfo();
            System.out.println(accountInfo);
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
     * 查询当前账户的bucket(条件查询)
     */
    @Test
    public void queryBuckets() {
        HOS hos = getHOSClient();
        /**
         * 查询出所有的桶
         */
        ListBucketsRequest listBucketsRequest = new ListBucketsRequest();

        /**
         * 查询出前缀为a的桶
         */
        ListBucketsRequest prefixListBucketsRequest = new ListBucketsRequest();
        prefixListBucketsRequest.setPrefix("test");

        /**
         * 指定查出3数量的桶
         */
        ListBucketsRequest countListBucketsRequest = new ListBucketsRequest();
        countListBucketsRequest.setMaxKeys(3);

        /**
         * 指定查询大于"ab"字符串的桶，字典序
         */
        ListBucketsRequest startAfterListBucketsRequest = new ListBucketsRequest();
        startAfterListBucketsRequest.setStartAfter("test_Hss");

        try {
            BucketList bucketList = hos.listBuckets(listBucketsRequest);
            System.out.println("所有的桶名：");
            bucketList.getBuckets().stream().forEach(item -> System.out.print(item.getBucketName() + "   "));
            System.out.println();
            System.out.println("======================================================");
            bucketList = hos.listBuckets(prefixListBucketsRequest);
            System.out.println("查询出前缀为a的桶：");
            bucketList.getBuckets().stream().forEach(item -> System.out.print(item.getBucketName() + "   "));
            System.out.println();
            System.out.println("======================================================");
            bucketList = hos.listBuckets(countListBucketsRequest);
            System.out.println("指定查出3数量的桶：");
            bucketList.getBuckets().stream().forEach(item -> System.out.print(item.getBucketName() + "   "));
            System.out.println();
            System.out.println("======================================================");
            bucketList = hos.listBuckets(startAfterListBucketsRequest);
            System.out.println("指定查询大于\"ab\"字符串的桶，字典序：");
            bucketList.getBuckets().stream().forEach(item -> System.out.print(item.getBucketName() + "   "));
            System.out.println();
            System.out.println("======================================================");
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
     * 查询当前账户的bucket(条件查询)
     */
    @Test
    public void listBuckets() {
        HOS hos = getHOSClient();
        try {
            List<Bucket> buckets = hos.listBuckets();
            buckets.forEach(System.out::println);
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
    public void queryBucketsByPage() {
        HOS hos = getHOSClient();
        // 创建通查询请求对象，并且设置每次查询最多2个元素
        ListBucketsRequest listBucketsRequest = new ListBucketsRequest().withMaxKeys(1);
        // 分页页数
        int index = 1;
        try {
            String nextMarker = null;
            BucketList bucketList = null;
            do {
                System.out.println("=====================第  " + index + "  页========================");
                // 将下一页的起点赋值属性startAfter
                listBucketsRequest.setStartAfter(nextMarker);
                // 查询
                bucketList = hos.listBuckets(listBucketsRequest);

                // 打印出查询结果
                List<Bucket> bucketList1 = bucketList.getBuckets();
                for (Bucket bucket : bucketList1) {
                    System.out.println(bucket.getBucketName());
                }
                // 赋值下一页的起点
                nextMarker = bucketList.getNextStartAfter();

                index++;
            } while (bucketList.isTruncated());
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
     * 设置账户配额
     * 账户配额值必须为非负整数，单位为Byte（字节）。默认配额为0，表示没有配额限制。配额设置后，如果想取消配额限制，可以把配额设置为0
     */
    @Test
    public void setQuota() {

        HOS hos = getHOSClient();
        // 创建账户配额请求对象并设置配额数量
        SetAccountQuotaRequest setAccountQuotaRequest = new SetAccountQuotaRequest(1024 * 1024 * 1024*100L, 100L);
        try {
            VoidResult voidResult = hos.setAccountQuota(setAccountQuotaRequest);
            if (voidResult.getResponse().isSuccessful()) {
                System.out.println("设置配额成功");
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
     * 查询账户配额
     * 查询账户配额信息。配额值的单位为Byte（字节），0代表不设上限。
     */
    @Test
    public void getQuota() {
        /**
         * endPoint：NCOSS的基础路径
         * account：账户
         * accessKey：向UAAS服务请求到的access_key
         * secretKey：向UAAS服务请求到的secret_key
         */
        HOS hos = getHOSClient();

        try {
            AccountInfo accountInfo1 = hos.getAccountQuota();
            System.out.println(accountInfo1);
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
