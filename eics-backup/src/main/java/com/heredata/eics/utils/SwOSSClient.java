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




    private static String originEndPoint="http://172.18.239.18:8093/v1/";


    private static String userName="8635110001";


    private static String password="$&&@!1!@1%84";


    private static String scopeName="8635110001";


    private static String uaasURL="http://172.18.239.16:8088/v3/auth/tokens";


    private static String TaendPoint="http://10.86.8.201:27741/v1/";


    private static String TauserName="backupSys";


    private static String Tapassword="HereData#!@2023";


    private static String TascopeName="backupSys";


    private static String TauaasURL= "http://10.86.8.201:27731/v3/auth/tokens";

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
