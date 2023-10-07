package com.heredata.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

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

    public static byte[] calculateMd5(byte[] binaryData) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found.");
        }
        messageDigest.update(binaryData);
        return messageDigest.digest();
    }

    public static String encodeMD5(byte[] binaryData) {
        byte[] md5Bytes = calculateMd5(binaryData);
        int len = md5Bytes.length;
        char buf[] = new char[len * 2];
        for (int i = 0; i < len; i++) {
            buf[i * 2] = HEX_DIGITS[(md5Bytes[i] >>> 4) & 0x0f];
            buf[i * 2 + 1] = HEX_DIGITS[md5Bytes[i] & 0x0f];
        }
        return new String(buf);
    }

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
