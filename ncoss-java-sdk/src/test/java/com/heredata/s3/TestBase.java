package com.heredata.s3;

import com.heredata.hos.HOS;
import com.heredata.hos.HOSClientBuilder;
import com.heredata.hos.model.KeyInformation;
import com.heredata.s3.S3;
import com.heredata.s3.S3ClientBuilder;
import org.junit.BeforeClass;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * TODO
 * @author wuzz
 * @since 2022/8/25
 */
public class TestBase {

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
        keyInformation.setEndPoint("http://172.18.232.37:8089");  // 该endpoint适用ncoss-4.*版本的,请同步修改com.heredata.hos.TestBase.getHOSClient
//        keyInformation.setEndPoint("http://172.18.232.192:8089/v1/"); // 该endpoint适用ncoss-3.*版本,请同步修改com.heredata.hos.TestBase.getHOSClient
        try {
            keyInformation.setKeyInformation("test_user1", "TEST#ps@857"
                    , "test_pro1"
                    , "http://172.18.232.192:6020/v3/auth/tokens");
            System.out.println("AccessKey=" + keyInformation.getAccessKey());
            System.out.println("SecretKey=" + keyInformation.getSecretKey());
            System.out.println("Token=" + keyInformation.getXSubjectToken());
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

    public static S3 getS3Client() {
//        return new HOSClientBuilder().build(endPoint, accountId, accessKey, secretKey);   // 适用ncoss-3.*版本。请同步修改com.heredata.hos.TestBase.oneTiemUp的endpoint
        return new S3ClientBuilder().build(endPoint, accessKey, secretKey);    // 适用ncoss-4.*版本。请同步修改com.heredata.hos.TestBase.oneTiemUp的endpoint
    }

}
