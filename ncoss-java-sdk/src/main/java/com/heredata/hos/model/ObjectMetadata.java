package com.heredata.hos.model;


import com.heredata.exception.ServiceException;
import com.heredata.hos.comm.HOSHeaders;
import com.heredata.utils.DateUtil;
import com.heredata.utils.StringUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.heredata.hos.comm.HOSHeaders.COPY_OBJECT_METADATA_DIRECTIVE;
import static com.heredata.hos.comm.HOSHeaders.HOS_STORAGE_CLASS;


/**
 * <p>Title: ObjectMetadata</p>
 * <p>Description: 对象元数据。包含自定义元数据和特定元数据 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 19:22
 */
public class ObjectMetadata {

    /**
     * 自定义元数据Map集合,前缀为 x-hos-meta-.
     */
    private Map<String, String> userMetadata = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

    /**
     * HOS系统特定含义元数据
     */
    protected Map<String, Object> metadata = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);

    public static final String AES_256_SERVER_SIDE_ENCRYPTION = "AES256";

    public static final String KMS_SERVER_SIDE_ENCRYPTION = "KMS";

    /**
     * 获取自定义元数据map集合
     * @return
     */
    public Map<String, String> getUserMetadata() {
        return userMetadata;
    }

    /**
     * 设置用户自定义元数据
     * @param userMetadata 自定义元数据Map集合
     */
    public void setUserMetadata(Map<String, String> userMetadata) {
        this.userMetadata.clear();
        if (userMetadata != null && !userMetadata.isEmpty()) {
            this.userMetadata.putAll(userMetadata);
        }
    }

    /**
     * 设置头信息
     * @param key 键
     * @param value 值
     */
    public void setHeader(String key, Object value) {
        metadata.put(key, value);
    }

    /**
     * 根据键移除特定的头数据
     * @param key 键
     */
    public void removeHeader(String key) {
        metadata.remove(key);
    }

    /**
     * 添加用户自定义元数据
     * @param key 键
     * @param value 值
     */
    public void addUserMetadata(String key, String value) {
        this.userMetadata.put(key, value);
    }

    /**
     * 获取最后的修改时间
     * @return
     */
    public Date getLastModified() {
        return (Date) metadata.get(HOSHeaders.LAST_MODIFIED);
    }

    /**
     * 设置最新的修改时间
     * @param lastModified
     */
    public void setLastModified(Date lastModified) {
        metadata.put(HOSHeaders.LAST_MODIFIED, lastModified);
    }

    /**
     * 设置到期时间
     * @param expirationTime
     */
    public void setExpirationTime(Date expirationTime) {
        metadata.put(HOSHeaders.EXPIRES, DateUtil.formatRfc822Date(expirationTime));
    }

    /**
     * 获取http请求协议body的content-length
     * @return
     */
    public long getContentLength() {
        Long contentLength = (Long) metadata.get(HOSHeaders.CONTENT_LENGTH);
        return contentLength == null ? 0 : contentLength.longValue();
    }

    /**
     * 设置http请求协议中body的长度
     * @param contentLength
     */
    public void setContentLength(long contentLength) {
        metadata.put(HOSHeaders.CONTENT_LENGTH, contentLength);
    }

    /**
     * 获取http请求协议中body的content-type
     * @return
     */
    public String getContentType() {
        return (String) metadata.get(HOSHeaders.CONTENT_TYPE);
    }

    /**
     * 获取http请求协议中body的content-type
     * @param contentType
     */
    public void setContentType(String contentType) {
        metadata.put(HOSHeaders.CONTENT_TYPE, contentType);
    }

    /**
     * 获取http请求协议中的body中的MD5值
     * @return
     */
    public String getContentMD5() {
        return (String) metadata.get(HOSHeaders.CONTENT_MD5);
    }

    /**
     * 设置http请求协议中的body中的MD5值
     * @param contentMD5
     */
    public void setContentMD5(String contentMD5) {
        metadata.put(HOSHeaders.CONTENT_MD5, contentMD5);
    }

    /**
     * 获取http请求协议中的body中的Encode值
     * @return
     */
    public String getContentEncoding() {
        return (String) metadata.get(HOSHeaders.CONTENT_ENCODING);
    }

    /**
     * 设置http请求协议中的body中的Encode值
     * @param encoding
     */
    public void setContentEncoding(String encoding) {
        metadata.put(HOSHeaders.CONTENT_ENCODING, encoding);
    }

    /**
     * 获取请求头中的Cache-Control
     * 用于指定所有缓存机制在整个请求/响应链中必须服从的指令。
     * 这些指令指定用于阻止缓存对请求或响应造成不利干扰的行为。
     * 这些指令通常覆盖默认缓存算法。
     * 缓存指令是单向的，即请求中存在一个指令并不意味着响应中将存在同一个指令。
     * 网页的缓存是由HTTP消息头中的“Cache-Control”来控制的，
     * 常见的取值有private、no-cache、max-age、must-revalidate等，默认为private。
     * @return
     */
    public String getCacheControl() {
        return (String) metadata.get(HOSHeaders.CACHE_CONTROL);
    }

    /**
     * 设置请求头中的Cache-Control
     * 用于指定所有缓存机制在整个请求/响应链中必须服从的指令。
     * 这些指令指定用于阻止缓存对请求或响应造成不利干扰的行为。
     * 这些指令通常覆盖默认缓存算法。
     * 缓存指令是单向的，即请求中存在一个指令并不意味着响应中将存在同一个指令。
     * 网页的缓存是由HTTP消息头中的“Cache-Control”来控制的，
     * 常见的取值有private、no-cache、max-age、must-revalidate等，默认为private。
     * @param cacheControl
     */
    public void setCacheControl(String cacheControl) {
        metadata.put(HOSHeaders.CACHE_CONTROL, cacheControl);
    }

    /**
     * 设置请求头中的Content-Disposition
     * MIME 协议的扩展，MIME 协议指示 MIME 用户代理如何显示附加的文件。
     * Content-disposition其实可以控制用户请求所得的内容存为一个文件的时候提供一个默认的文件名，
     * 文件直接在浏览器上显示或者在访问时弹出文件下载对话框
     * @return
     */
    public String getContentDisposition() {
        return (String) metadata.get(HOSHeaders.CONTENT_DISPOSITION);
    }

    /**
     * 设置请求头中的Content-Disposition
     * MIME 协议的扩展，MIME 协议指示 MIME 用户代理如何显示附加的文件。
     * Content-disposition其实可以控制用户请求所得的内容存为一个文件的时候提供一个默认的文件名，
     * 文件直接在浏览器上显示或者在访问时弹出文件下载对话框
     * @param disposition
     */
    public void setContentDisposition(String disposition) {
        metadata.put(HOSHeaders.CONTENT_DISPOSITION, disposition);
    }

    /**
     * 获取标签信息
     * @return
     */
    public String getETag() {
        return (String) metadata.get(HOSHeaders.ETAG);
    }

    /**
     * 服务端加密，获取加密有效值，目前仅支持here:kms
     * @return
     */
    public String getServerSideEncryption() {
        return (String) metadata.get(HOSHeaders.HOS_SERVER_SIDE_ENCRYPTION);
    }

    /**
     * 服务端加密，设置加密有效值，目前仅支持here:kms
     * @param serverSideEncryption
     */
    public void setServerSideEncryption(String serverSideEncryption) {
        metadata.put(HOSHeaders.HOS_SERVER_SIDE_ENCRYPTION, serverSideEncryption);
    }

    /**
     * 服务端加密，获取HKMS服务返回的secretId值
     * @return
     */
    public String getServerSideEncryptionKeyId() {
        return (String) metadata.get(HOSHeaders.HOS_SERVER_SIDE_ENCRYPTION_KEY_ID);
    }

    /**
     * 服务端加密，设置HKMS服务返回的secretId值
     * @param serverSideEncryptionKeyId HKMS服务返回的secretId值
     */
    public void setServerSideEncryptionKeyId(String serverSideEncryptionKeyId) {
        metadata.put(HOSHeaders.HOS_SERVER_SIDE_ENCRYPTION_KEY_ID, serverSideEncryptionKeyId);
    }

    /**
     * 客户端加密，设置加密算法  {@link AlgorithmEnum}
     * 目前仅支持AES256
     * @param algorithmEnum
     */
    public void setClientSideEncryptionAlgorithm(AlgorithmEnum algorithmEnum) {
        metadata.put(HOSHeaders.HOS_CLIENT_SIDE_ENCRYPTION_ALGORITHM, algorithmEnum.getAlgorithm());
    }

    /**
     * 客户端加密，设置加密算法
     * @return
     */
    public String getClientSideEncryptionAlgorithm() {
        return metadata.get(HOSHeaders.HOS_CLIENT_SIDE_ENCRYPTION_ALGORITHM).toString();
    }

    /**
     * 客户端加密，设置秘钥的BASE64值
     * @param key
     */
    public void setClientSideEncryptionKey(String key) {
        if (key == null && key.length() != 32) {
            throw new ServiceException("非法的字符串长度");
        }
        String s = Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.UTF_8));
        metadata.put(HOSHeaders.HOS_CLIENT_SIDE_ENCRYPTION_KEY, s);
        byte[] bytes = StringUtils.encrypByMD5Arr(key);
        metadata.put(HOSHeaders.HOS_CLIENT_SIDE_ENCRYPTION_KEY_MD5, Base64.getEncoder().encodeToString(bytes));
    }

    /**
     * 客户端加密，获取秘钥的BASE64值
     * @return
     */
    public String getClientSideEncryptionKey() {
        return metadata.get(HOSHeaders.HOS_CLIENT_SIDE_ENCRYPTION_KEY).toString();
    }

    /**
     * 客户端加密，获取秘钥的MD5值
     * @return
     */
    public String getClientSideEncryptionKeyMD5() {
        return metadata.get(HOSHeaders.HOS_CLIENT_SIDE_ENCRYPTION_KEY_MD5).toString();
    }


    /**
     * 设置对象的ACL授权人权限
     * @param permission
     */
    @Deprecated
    public void setObjectAcl(Permission permission) {
        metadata.put(HOSHeaders.HOS_OBJECT_ACL, permission != null ? permission.toString() : "");
    }

    /**
     * 获取对象数对象（该对象属于只读）
     * @return
     */
    public Map<String, Object> getRawMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

    /**
     * 获取请求ID
     * @return
     */
    public String getRequestId() {
        return (String) metadata.get(HOSHeaders.HOS_HEADER_REQUEST_ID);
    }

    /**
     * 获取对象版本信息
     * @return
     */
    public String getVersionId() {
        return (String) metadata.get(HOSHeaders.HOS_HEADER_VERSION_ID);
    }

    /**
     * 获取冗余校验值
     * @return
     */
    public Long getServerCRC() {
        String strSrvCrc = (String) metadata.get(HOSHeaders.HOS_HASH_CRC64_ECMA);

        if (strSrvCrc != null) {
            BigInteger bi = new BigInteger(strSrvCrc);
            return bi.longValue();
        }
        return null;
    }


    /**
     * 复制对象时，设置目标对象元数据的类型
     * 标识新对象的元数据是从源对象中复制，还是用请求中的元数据替换或更新
     * {@link CopyObjectRequest.MetadataDirective}
     * @param directive
     */
    public void setObjectDirective(String directive) {
        metadata.put(COPY_OBJECT_METADATA_DIRECTIVE, directive);
    }

    /**
     * 设置对象的存储类型  {@link StorageClass}
     * @param storageClass
     */
    public void setObjectStorageClass(StorageClass storageClass) {
        metadata.put(HOS_STORAGE_CLASS, storageClass.name());
    }

    /**
     * 获取对象的存储类型   {@link StorageClass}
     * @return
     */
    public StorageClass getObjectStorageClass() {
        String storageClassString = (String) metadata.get(HOS_STORAGE_CLASS);
        if (storageClassString != null) {
            return StorageClass.parse(storageClassString);
        }
        return StorageClass.STANDARD;
    }

    /**
     * 获取Archive类型对象的解冻状态
     * @return
     */
    public String getObjectRestoreStatus() {
        return (String) metadata.get(HOSHeaders.HOS_RESTORE);
    }

    /**
     * 对象是否完成恢复归档
     * @return
     */
    @Deprecated
    public boolean isRestoreCompleted() {
        String restoreString = getObjectRestoreStatus();
        if (restoreString == null) {
            throw new NullPointerException();
        }

        if (restoreString.equals(HOSHeaders.HOS_ONGOING_RESTORE)) {
            return false;
        }
        return true;
    }
}
