package com.heredata.hos.model;

import lombok.Data;

import java.util.Map;

/**
 * <p>Title: SetTaggingRequest</p>
 * <p>Description: 设置标签请求实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 16:20
 */
@Data
public class SetTaggingRequest extends GenericRequest {
    /**
     * 标签实体对象  {@link TagSet}
     */
    protected TagSet tagSet = null;

    /**
     * @param bucketName 桶名称
     * @param key 对象名称
     */
    public SetTaggingRequest(String bucketName, String key) {
        super(bucketName, key);
        this.tagSet = new TagSet();
    }

    /**
     * @param bucketName 桶名称
     * @param key 对象名称
     * @param tags 标签Map对象
     */
    public SetTaggingRequest(String bucketName, String key, Map<String, String> tags) {
        super(bucketName, key);
        this.tagSet = new TagSet(tags);
    }

    /**
     *
     * @param bucketName 桶名称
     * @param key 对象名称
     * @param tagSet 标签实体对象  {@link TagSet}
     */
    public SetTaggingRequest(String bucketName, String key, TagSet tagSet) {
        super(bucketName, key);
        this.tagSet = tagSet;
    }
}
