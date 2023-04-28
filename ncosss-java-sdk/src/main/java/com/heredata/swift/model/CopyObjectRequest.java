package com.heredata.swift.model;

import com.heredata.model.WebServiceRequest;
import lombok.Data;

/**
 * <p>Title: CopyObjectRequest</p>
 * <p>Description: 用来复制对象的尸体来，包装了复制的各个属性含义 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:31
 */
@Data
public class CopyObjectRequest extends WebServiceRequest {

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
    private String targetBucketName;

    /**
     * 目的对象名称
     */
    private String targetKey;

    /**
     * 新对象元数据
     */
    private ObjectMetadata newObjectMetadata;

    /**
     * 是否刷新元数据。
     * 如果为true，新对象的元数据将使用{@link ObjectMetadata}对象中的元数据。
     * 如果为false，使用源对象的元数据作为新对象的元数据
     */
    private Boolean freshMetadata = false;

    /**
     * @param sourceBucketName 源对象所在桶
     * @param sourceKey 源对象名称
     * @param targetBucketName 目的桶名称
     * @param targetKey 目的对象名称
     */
    public CopyObjectRequest(String sourceBucketName, String sourceKey,
                             String targetBucketName, String targetKey) {
        setSourceBucketName(sourceBucketName);
        setSourceKey(sourceKey);
        setTargetBucketName(targetBucketName);
        setTargetKey(targetKey);
    }
}
