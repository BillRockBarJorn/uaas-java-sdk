package com.heredata.swift.model;

import com.heredata.model.WebServiceRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: GenericRequest</p>
 * <p>Description:  通用的请求实体。可以设置基础的，大部分请求要求的属性，比如说bucket，key</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 10:08
 */
@Data
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

    public GenericRequest(String bucketName) {
        this(bucketName, null);
    }

    public GenericRequest(String bucketName, String key) {
        this.bucketName = bucketName;
        this.key = key;
    }


    public GenericRequest withBucketName(String bucketName) {
        setBucketName(bucketName);
        return this;
    }

    public GenericRequest withKey(String key) {
        setKey(key);
        return this;
    }
}
