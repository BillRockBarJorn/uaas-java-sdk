package com.heredata.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <p>Title: BinaryUtil</p>
 * <p>Description: 二进制工具类 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 15:30
 */
public class BinaryUtil {

    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
            'E', 'F'};

    public static String toBase64String(byte[] binaryData) {
        return new String(Base64.encodeBase64(binaryData));
    }

    public static byte[] fromBase64String(String base64String) {
        return Base64.decodeBase64(base64String);
    }

    /**
     * 修复漏洞3.2.1.1   漏洞来源代码扫描报告-cmstoreos-sdk-java-1215-0b57751a.pdf
     * 之前是使用MD5，现在将MD5的工具类已做删除，采用sha256算法
     */
    public static byte[] calculateSha256(byte[] binaryData) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found.");
        }

        messageDigest.update(binaryData);
        return messageDigest.digest();
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Converts byte data to a Hex-encoded string in lower case.
     *
     * @param data
     *            data to hex encode.
     *
     * @return hex-encoded string.
     */
    public static String toHex(byte[] data) {
        return Hex.encodeHexString(data);
    }
}
