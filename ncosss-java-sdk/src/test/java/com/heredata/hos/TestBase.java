package com.heredata.hos;

import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import com.heredata.hos.model.KeyInformation;
import com.heredata.hos.model.ListObjectsRequest;
import com.heredata.hos.model.ObjectListing;
import org.junit.BeforeClass;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * TODO
 * @author wuzz
 * @since 2022/8/25
 */
public class TestBase {


    public static void main(String[] args) throws URISyntaxException, IOException {
        KeyInformation keyInformation = new KeyInformation();
        keyInformation.setEndPoint("http://172.18.232.192:6013/v1/");
        keyInformation.setKeyInformation("wzz", "!wzz12345qwert", "wzz", "http://10.20.29.14:12340/v3/auth/tokens");
//            keyInformation.setSecretId(StringUtils.getPointLengthUUID(32), new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24), "http://172.18.232.37:6069/v1/secrets");
        HOS hos = new HOSClientBuilder().build(keyInformation.getEndPoint(), keyInformation.getAccountId(), keyInformation.getAccessKey(), keyInformation.getSecretKey());
//        System.out.println(hos.getAccountInfo());
//
//        List<Bucket> buckets = hos.listBuckets();
//        buckets.forEach(System.out::println);

        System.out.println(hos.getAccountInfo());

    }

    static String endPoint;
    static String accessKey;
    static String secretKey;
    static String account;
    static String accountId;
    static String userId;
    static String xSubjectToken;
    static String secretId;

    @BeforeClass
    public static void oneTiemUp() {

        KeyInformation keyInformation = new KeyInformation();
//        keyInformation.setEndPoint("http://172.18.232.37:8089/v1/");
        keyInformation.setEndPoint("http://172.18.232.37:6055/HOSv1/");
//        keyInformation.setEndPoint("http://172.18.232.192:8089/v1/");
        try {
            keyInformation.setKeyInformation("test_user1", "TEST#ps@857"
                    , "test_pro1"
                    , "http://172.18.232.192:6020/v3/auth/tokens");
            System.out.println("AccessKey=" + keyInformation.getAccessKey());
            System.out.println("SecretKey=" + keyInformation.getSecretKey());
            System.out.println("Token=" + keyInformation.getXSubjectToken());
//            keyInformation.setSecretId(StringUtils.getPointLengthUUID(32), new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24), "http://172.18.232.37:6069/v1/secrets");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        endPoint = keyInformation.getEndPoint();
        accessKey = keyInformation.getAccessKey();
        secretKey = keyInformation.getSecretKey();
        account = keyInformation.getAccount();
        accountId = keyInformation.getAccountId();
        xSubjectToken = keyInformation.getXSubjectToken();
        userId = keyInformation.getUserId();
        secretId = keyInformation.getSecretId();
    }


    public void demo3() throws URISyntaxException, IOException {
        KeyInformation keyInformation = new KeyInformation();
        keyInformation.setEndPoint("http://10.20.29.15:27734/v1/");
        keyInformation.setKeyInformation("username", "password", "scopename", "http://10.20.29.14:12340/v3/auth/tokens");

        HOS hos = new HOSClientBuilder().build(keyInformation.getEndPoint(), keyInformation.getAccountId(), keyInformation.getAccessKey(), keyInformation.getSecretKey());
        try {
            // 创建请求对象
            ObjectListing example = hos.listObjects("example");
            example.getObjectSummaries().stream().forEach(item -> System.out.println(item.getKey()));
            System.out.println("================================");

            // 查询前缀和大于"e"的对象
            ListObjectsRequest prefixQuery = new ListObjectsRequest("example");
            prefixQuery.setPrefix("prefix");
            ObjectListing example1 = hos.listObjects(prefixQuery);
            example1.getObjectSummaries().stream().forEach(item -> System.out.println(item.getKey()));
            System.out.println("================================");

            // 设置查询数量
            ListObjectsRequest maxCountQuery = new ListObjectsRequest("example");
            maxCountQuery.setMaxKeys(1);
            ObjectListing example2 = hos.listObjects(maxCountQuery);
            example2.getObjectSummaries().stream().forEach(item -> System.out.println(item.getKey()));
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

    public static HOS getHOSClient() {
//        return new HOSClientBuilder().build(endPoint, accountId, accessKey, secretKey);
        return new HOSClientBuilder().build(endPoint, accessKey, secretKey);
    }

}
