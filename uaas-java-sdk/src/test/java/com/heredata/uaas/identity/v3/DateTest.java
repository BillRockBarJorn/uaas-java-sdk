package com.heredata.uaas.identity.v3;

import org.junit.Test;

import java.security.*;
import java.util.Base64;

/**
 * TODO
 * @author wuzz
 * @since 2022/9/6
 */
public class DateTest {

    @Test
    public void demo1() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");

        // 初始化KeyPairGenerator。
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyGen.initialize(1024, random);

        // 生成密钥对，私钥和公钥。
        KeyPair keyPair = keyGen.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        Base64.Encoder encoder = Base64.getEncoder();
        System.out.println("privateKey: " + encoder.encodeToString(privateKey.getEncoded()));
        System.out.println("publicKey: " + encoder.encodeToString(publicKey.getEncoded()));
    }

}
