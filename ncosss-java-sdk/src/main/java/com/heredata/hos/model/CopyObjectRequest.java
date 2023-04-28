package com.heredata.hos.model;

import com.heredata.exception.ServiceException;
import com.heredata.model.WebServiceRequest;
import com.heredata.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * <p>Title: CopyObjectRequest</p>
 * <p>Description: 用来复制对象的尸体来，包装了复制的各个属性含义 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/24 18:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CopyObjectRequest extends WebServiceRequest {

    public static enum MetadataDirective {

        /**
         * 旧对象的元数据赋值给新对象
         */
        COPY("COPY"),
        /**
         * 使用请求中的元数据替换，属于覆盖操作
         */
        REPLACE("REPLACE"),
        /* Replace metadata with newly metadata */
        /**
         * 使用请求中的元数据更新，有则更新无则新建
         */
        REPLACE_NEW("REPLACE_NEW");

        private final String directiveAsString;

        private MetadataDirective(String directiveAsString) {
            this.directiveAsString = directiveAsString;
        }

        @Override
        public String toString() {
            return this.directiveAsString;
        }
    }

    /**
     * 源对象所在桶
     */
    private String sourceBucketName;
    /**
     * 源对象名称
     */
    private String sourceKey;
    /**
     * 目的桶名称
     */
    private String destinationBucketName;
    /**
     * 目的对象名称
     */
    private String destinationKey;
    /**
     * 新对象元数据
     */
    private ObjectMetadata newObjectMetadata;

    // 下面2个属性用于服务端加密，作用域目标对象
    /**
     * 服务端加密是SSE-KMS方式，目前仅支持here:kms
     */
    private String serverSideEncryption;
    /**
     * 对象加密将使用用户托管在HKMS服务的密钥进行加密
     * 即HKMS服务返回的秘钥
     */
    private String serverSideEncryptionKeyID;

    // 下面6个属性用户客户端加密
    /**
     * 目标对象的加密算法，目前只支持AES256
     */
    private AlgorithmEnum clientSideEncryptionAlgorithm;
    /**
     * 目标对象的加密秘钥，256位密钥的base64编码
     */
    private String clientSideEncryptionKey;
    /**
     * 目标对象加密秘钥的MD5值
     */
    private String clientSideEncryptionKeyMD5;

    // 如果源对象有加密需要传入下列三个值
    /**
     * 源对象加密算法，目前只支持AES256
     */
    private String copyClientSideEncryptionAlgorithm;
    /**
     *目标对象的加密秘钥，256位密钥的base64编码
     */
    private String copyClientSideEncryptionKey;
    /**
     * 目标对象加密秘钥的MD5值
     */
    private String copyClientSideEncryptionKeyMD5;

    public CopyObjectRequest(String sourceBucketName, String sourceKey,
                             String destinationBucketName, String destinationKey) {
        setSourceBucketName(sourceBucketName);
        setSourceKey(sourceKey);
        setDestinationBucketName(destinationBucketName);
        setDestinationKey(destinationKey);
    }

    public AlgorithmEnum getClientSideEncryptionAlgorithm() {
        return clientSideEncryptionAlgorithm;
    }

    public void setClientSideEncryptionAlgorithm(AlgorithmEnum algorithmEnum) {
        this.clientSideEncryptionAlgorithm = algorithmEnum;
        this.copyClientSideEncryptionAlgorithm = algorithmEnum.getAlgorithm();
    }

    public String getClientSideEncryptionKey() {
        return clientSideEncryptionKey;
    }

    public void setClientSideEncryptionKey(String clientSideEncryptionKey) {
        if (clientSideEncryptionKey == null && clientSideEncryptionKey.length() != 32) {
            throw new ServiceException("非法的字符串长度");
        }
        String s = Base64.getEncoder().encodeToString(clientSideEncryptionKey.getBytes(StandardCharsets.UTF_8));
        this.clientSideEncryptionKey = s;
        byte[] bytes = StringUtils.encrypByMD5Arr(clientSideEncryptionKey);
        String s1 = Base64.getEncoder().encodeToString(bytes);
        this.clientSideEncryptionKeyMD5 = s1;

        this.copyClientSideEncryptionKey = s;
        this.copyClientSideEncryptionKeyMD5 = s1;
    }
}
