package com.heredata.hos.model;

import java.util.Map;

/**
 * <p>Title: SetObjectTaggingRequest</p>
 * <p>Description: 对象标签设置请求实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 16:25
 */
public class SetObjectTaggingRequest extends SetTaggingRequest {

    /**
     * @param bucketName 桶名称
     * @param key 对象名称
     */
    public SetObjectTaggingRequest(String bucketName, String key) {
        super(bucketName, key);
    }

    /**
     * @param bucketName 桶名称
     * @param key 对象名称
     * @param tags 标签Map集合
     */
    public SetObjectTaggingRequest(String bucketName, String key, Map<String, String> tags) {
        super(bucketName, key, tags);
    }

    /**
     *
     * @param bucketName 桶名称
     * @param key 对象名称
     * @param tagSet 标签对象  {@link TagSet}
     */
    public SetObjectTaggingRequest(String bucketName, String key, TagSet tagSet) {
        super(bucketName, key, tagSet);
    }

    /**
     * @param tagSet 标签对象  {@link TagSet}
     * @return
     */
    public SetObjectTaggingRequest withTagSet(TagSet tagSet) {
        setTagSet(tagSet);
        return this;
    }
}
