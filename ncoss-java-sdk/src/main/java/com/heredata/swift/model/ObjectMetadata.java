package com.heredata.swift.model;

import com.heredata.swift.comm.SwiftHeaders;
import lombok.ToString;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * <p>Title: ObjectMetadata</p>
 * <p>Description: 对象元数据。包含自定义元数据和特定元数据 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 19:22
 */
@ToString
public class ObjectMetadata {

    /**
     * 自定义元数据Map集合
     */
    private Map<String, String> userMetadata = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    /**
     * SWIFT服务特定含义元数据
     */
    protected Map<String, Object> metadata = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

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
     * 获取http请求协议body的content-length
     * @return
     */
    public long getContentLength() {
        Long contentLength = (Long) metadata.get(SwiftHeaders.CONTENT_LENGTH);
        return contentLength == null ? 0 : contentLength.longValue();
    }

    /**
     * 获取http请求协议中body的content-type
     * @return
     */
    public String getContentType() {
        return (String) metadata.get(SwiftHeaders.CONTENT_TYPE);
    }

    /**
     * 获取http请求协议中body的content-type
     * @param contentType
     */
    public void setContentType(String contentType) {
        metadata.put(SwiftHeaders.CONTENT_TYPE, contentType);
    }

    /**
     * 获取标签信息
     * @return
     */
    public String getETag() {
        return (String) metadata.get(SwiftHeaders.ETAG);
    }

    /**
     * 获取对象数对象（该对象属于只读）
     * @return
     */
    public Map<String, Object> getRawMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

    /**
     * 获取冗余校验值
     * @return
     */
    public Long getServerCRC() {
        String strSrvCrc = (String) metadata.get(SwiftHeaders.SWIFT_HASH_CRC64_ECMA);

        if (strSrvCrc != null) {
            BigInteger bi = new BigInteger(strSrvCrc);
            return bi.longValue();
        }
        return null;
    }
}
