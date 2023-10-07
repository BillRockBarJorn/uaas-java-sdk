package com.heredata.hos.model;

import com.heredata.exception.ClientException;
import com.heredata.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * <p>Title: InitiateMultipartUploadRequest</p>
 * <p>Description: 初始化分片上传请求实体，封装了初始化上传的参数信息 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 13:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InitiateMultipartUploadRequest extends GenericRequest {
    /**
     * 对象的元数据 {@link ObjectMetadata}
     */
    private ObjectMetadata objectMetadata;

    // 下面两个属性代表对象的服务端加密
    /**
     * 服务端加密：SSE-KMS方式，目前仅支持here:kms。
     * 需要另外一个服务HKMS服务
     */
    private String serverSideEncryption;

    /**
     * 服务端加密，加密秘钥。秘钥需要保存在HKMS服务中，
     * 并且当前秘钥用的是HKMS服务返回的秘钥
     */
    private String serverSideEncryptionKeyID;

    // 下面三个属性代表客户端加密方式
    /**
     * 客户端加密：加密算法，目前仅支持AES256加密算法
     */
    private AlgorithmEnum clientSideEncryptionAlgorithm;
    /**
     * 使用的密钥，256字符串长度的BASE64编码
     */
    private String clientSideEncryptionKey;
    /**
     * clientServerSideEncryptionKey属性的MD5值
     * {@link InitiateMultipartUploadRequest#clientSideEncryptionKey}
     */
    private String clientSideEncryptionKeyMD5;

    /**
     * @param bucketName
     *            桶名称
     * @param key
     *            对象名称
     */
    public InitiateMultipartUploadRequest(String bucketName, String key) {
        this(bucketName, key, null);
    }

    /**
     * @param bucketName
     *            桶名称
     * @param key
     *            对象名称
     * @param objectMetadata
     *            对象元数据
     */
    public InitiateMultipartUploadRequest(String bucketName, String key, ObjectMetadata objectMetadata) {
        super(bucketName, key);
        this.objectMetadata = objectMetadata;
    }

    /**
     * 客户端加密，设置秘钥
     * @param clientServerSideEncryptionKey 32长度的字符串，传进来的参数无需加密
     */
    public void setClientServerSideEncryptionKey(String clientServerSideEncryptionKey) {
        if (clientServerSideEncryptionKey == null && clientServerSideEncryptionKey.length() != 32) {
            throw new ClientException("非法的字符串长度");
        }
        String s = Base64.getEncoder().encodeToString(clientServerSideEncryptionKey.getBytes(StandardCharsets.UTF_8));
        this.clientSideEncryptionKey = s;
        byte[] bytes = StringUtils.encrypByMD5Arr(clientServerSideEncryptionKey);
        this.clientSideEncryptionKeyMD5 = Base64.getEncoder().encodeToString(bytes);
    }
}
