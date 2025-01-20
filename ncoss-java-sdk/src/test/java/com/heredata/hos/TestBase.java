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
//        keyInformation.setEndPoint("http://172.18.232.37:8089/HOSv1/");  // 该endpoint适用ncoss-4.*版本的,请同步修改com.heredata.hos.TestBase.getHOSClient
//        keyInformation.setEndPoint("http://172.18.232.192:8089/v1/"); // 该endpoint适用ncoss-3.*版本,请同步修改com.heredata.hos.TestBase.getHOSClient
        keyInformation.setEndPoint("http://10.20.29.12:8089/v1/"); // 该endpoint适用ncoss-3.*版本,请同步修改com.heredata.hos.TestBase.getHOSClient
//        keyInformation.setEndPoint("http://10.20.29.3:11611/v1/"); // 该endpoint适用ncoss-3.*版本,请同步修改com.heredata.hos.TestBase.getHOSClient
        try {
//            keyInformation.setKeyInformation("test_user1", "TEST#ps@857"
//                    , "test_pro1"
//                    , "http://172.18.232.192:6020/v3/auth/tokens");

//            keyInformation.setKeyInformation("admin-1736230231166", "66a38134a59b4883943cf821a68261ac"
//                    , "admin-1736230231166"
//                    , "http://10.20.29.2:22340/v3/auth/tokens");

            keyInformation.setKeyInformation("oss_project-1737254683227", "ede5b8ea2f1d46e48de5a4f662ae1f2b"
                    , "oss_project-1737254683227"
                    , "http://10.20.29.10:12340/v3/auth/tokens");

//            keyInformation.setKeyInformation("oss_project-1736473020343", "595a093c84ca430693d6dffb8e861f58"
//                    , "oss_project-1736473020343"
//                    , "http://10.20.29.2:22340/v3/auth/tokens");

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

    public static HOS getHOSClient() {
        return new HOSClientBuilder().build(endPoint, accountId, accessKey, secretKey);   // 适用ncoss-3.*版本。请同步修改com.heredata.hos.TestBase.oneTiemUp的endpoint
//        return new HOSClientBuilder().build(endPoint, accessKey, secretKey);    // 适用ncoss-4.*版本。请同步修改com.heredata.hos.TestBase.oneTiemUp的endpoint
    }

}
