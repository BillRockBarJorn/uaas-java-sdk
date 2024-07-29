package com.heredata.eics.utils;

import com.heredata.hos.HOS;
import com.heredata.hos.HOSClientBuilder;
import com.heredata.swift.Swift;
import com.heredata.swift.SwiftClientBuilder;
import com.heredata.swift.model.KeyInformation;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URISyntaxException;

public class SwOSSClient {



    @Value("${originEndPoint}")
    private static String originEndPoint;

    @Value("${userName}")
    private static String userName;

    @Value("${password}")
    private static String password;

    @Value("${scopeName}")
    private static String scopeName;

    @Value("${uaasURL}")
    private static String uaasURL;

    @Value("${TaendPoint}")
    private static String TaendPoint;

    @Value("${TauserName}")
    private static String TauserName;

    @Value("${Tapassword}")
    private static String Tapassword;

    @Value("${TascopeName}")
    private static String TascopeName;

    @Value("${TauaasURL}")
    private static String TauaasURL;


    //源认证
    public static KeyInformation originIden() {

        KeyInformation keyInformation = new KeyInformation();
        keyInformation.setEndPoint(originEndPoint);
        try {
            // 设置对象中的关键信息，密钥、租户信息以及xSubjectToken(token)
            keyInformation.setKeyInformation(userName, password, scopeName, uaasURL);
            // 设置密钥id，用来做加密对象。
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keyInformation;
    }

    //目标认证
    public static KeyInformation tarIden() {

        KeyInformation keyInformation = new KeyInformation();
        keyInformation.setEndPoint(TaendPoint);
        try {
            // 设置对象中的关键信息，密钥、租户信息以及xSubjectToken(token)
            keyInformation.setKeyInformation(TauserName, Tapassword, TascopeName, TauaasURL);
            // 设置密钥id，用来做加密对象。
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keyInformation;
    }

}
