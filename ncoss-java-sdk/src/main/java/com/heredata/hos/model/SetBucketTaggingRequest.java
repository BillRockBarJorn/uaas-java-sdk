package com.heredata.hos.model;

import java.util.Map;

/**
 * <p>Title: SetBucketTaggingRequest</p>
 * <p>Description: 设置桶标签请求实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 16:23
 */
public class SetBucketTaggingRequest extends SetTaggingRequest {
    /**
     * @param bucketName 桶名称
     * @param tags 标签Map
     */
    public SetBucketTaggingRequest(String bucketName, Map<String, String> tags) {
        super(bucketName, null, tags);
    }

    /**
     * @param bucketName 桶名称
     * @param tagSet 标签对象 {@link TagSet}
     */
    public SetBucketTaggingRequest(String bucketName, TagSet tagSet) {
        super(bucketName, null, tagSet);
    }

    /**
     * @param tagSet 标签对象  {@link TagSet}
     * @return
     */
    public SetBucketTaggingRequest withTagSet(TagSet tagSet) {
        setTagSet(tagSet);
        return this;
    }
}
