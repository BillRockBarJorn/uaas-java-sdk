package com.heredata.hos.model;

/**
 * <p>Title: CreateBucketRequest</p>
 * <p>Description: 创建桶名称 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/24 19:03
 */
public class CreateBucketRequest extends GenericRequest {
    public CreateBucketRequest(String bucketName) {
        super(bucketName);
    }
}
