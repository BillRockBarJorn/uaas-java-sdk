package com.heredata.eics.config;

import com.heredata.hos.HOS;
import com.heredata.hos.HOSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NcossClient {

    @Value("${endPoint}")
    private String endPoint;

    @Value("${accessKey}")
    private String accessKey;

    @Value("${secretKey}")
    private String secretKey;

    @Value("${accountId}")
    private String accountId;

    @Bean
    public HOS getHos() {
        return new HOSClientBuilder().build(endPoint, accountId, accessKey, secretKey);
    }

}
