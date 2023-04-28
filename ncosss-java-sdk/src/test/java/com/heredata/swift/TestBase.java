package com.heredata.swift;

import com.heredata.swift.model.KeyInformation;
import org.junit.BeforeClass;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * TODO
 * @author wuzz
 * @since 2022/8/25
 */
public class TestBase {
    static String endPoint = "http://172.18.232.192:8089/v1/";
    static String account;
    static String accountId;
    static String userId;
    static String xSubjectToken;

    @BeforeClass
    public static void oneTiemUp() {
        // 用户名
        String userName = "test_user1";
        // 用户对应的密码
        String password = "TEST#ps@857";
        // 账户(租户)名
        String scopeName = "test_pro1";
        // 向UAAS请求秘钥的接口
        String uaasURL = "http://172.18.232.37:7079/v3/auth/tokens";
        // 服务端加密使用到的hkms服务接口
        String hkmsURL = "http://172.18.232.37:6069/v1/secrets";

        KeyInformation keyInformation = new KeyInformation();
        keyInformation.setEndPoint(endPoint);
        try {
            // 设置对象中的关键信息，密钥、租户信息以及xSubjectToken(token)
            keyInformation.setKeyInformation(userName, password, scopeName, uaasURL);
            // 设置密钥id，用来做加密对象。
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        account = keyInformation.getAccount();
        accountId = keyInformation.getAccountId();
        xSubjectToken = keyInformation.getXSubjectToken();
        userId = keyInformation.getUserId();
    }
}
