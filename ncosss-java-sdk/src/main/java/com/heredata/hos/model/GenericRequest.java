package com.heredata.hos.model;

import com.heredata.model.WebServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: GenericRequest</p>
 * <p>Description:  通用的请求实体。可以设置基础的，大部分请求要求的属性，比如说bucket，key,versionId</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 10:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericRequest extends WebServiceRequest {
    /**
     * 桶名称
     */
    private String bucketName;
    /**
     * 对象名称
     */
    private String key;
    /**
     * 版本号
     */
    private String versionId;

    public GenericRequest(String bucketName) {
        this(bucketName, null);
    }

    public GenericRequest(String bucketName, String key) {
        this.bucketName = bucketName;
        this.key = key;
    }

    /**
     * 设置桶名称
     * @param bucketName
     * @return
     */
    public GenericRequest withBucketName(String bucketName) {
        setBucketName(bucketName);
        return this;
    }

    /**
     * 设置对象名称
     * @param key
     * @return
     */
    public GenericRequest withKey(String key) {
        setKey(key);
        return this;
    }


    /**
     * 设置版本号
     * @param versionId
     * @return
     */
    public GenericRequest withVersionId(String versionId) {
        setVersionId(versionId);
        return this;
    }
}
