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
        keyInformation.setEndPoint("http://10.30.52.249:31722/HOSv1/");  // 该endpoint适用ncoss-4.*版本的,请同步修改com.heredata.hos.TestBase.getHOSClient
//        keyInformation.setEndPoint("http://172.18.232.192:8089/v1/"); // 该endpoint适用ncoss-3.*版本,请同步修改com.heredata.hos.TestBase.getHOSClient
        try {
            keyInformation.setKeyInformation("hs_hs-1695742827153", "1083df3b0ac2403dae02435df0d0b78d"
                    , "hs_hs-1695742827153"
                    , "http://10.30.52.248:30919/v1/auth/tokens");
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
//        return new HOSClientBuilder().build(endPoint, accountId, accessKey, secretKey);   // 适用ncoss-3.*版本。请同步修改com.heredata.hos.TestBase.oneTiemUp的endpoint
        return new HOSClientBuilder().build(endPoint, accessKey, secretKey);    // 适用ncoss-4.*版本。请同步修改com.heredata.hos.TestBase.oneTiemUp的endpoint
    }

}
