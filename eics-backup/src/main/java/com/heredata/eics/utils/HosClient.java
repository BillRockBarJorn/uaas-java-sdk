package com.heredata.eics.utils;

import com.heredata.hos.HOS;
import com.heredata.hos.HOSClientBuilder;
import org.springframework.beans.factory.annotation.Value;

public class HosClient {



    @Value("${endPoint}")
    private static String originEndPoint;

    @Value("${accessKey}")
    private static String originAK;

    @Value("${secretKey}")
    private static String originSK;

    @Value("${accountId}")
    private static String originAD;


    public static HOS getHOSClient() {

        return new HOSClientBuilder().build(originEndPoint, originAD,originAK, originSK );
    }
}
