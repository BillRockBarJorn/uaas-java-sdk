package com.heredata.swift.model;

import com.heredata.swift.model.bucket.BucketAclRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateBucketRequest extends GenericRequest {
    /**
     * 桶配额
     */
    private Long quotaByte;

    /**
     * 桶的最大数量
     */
    private Integer objCount;

    /**
     * 桶的ACL对象信息
     */
    private BucketAclRequest bucketAclRequest;

    public CreateBucketRequest(String bucketName) {
        super(bucketName);
    }

    /**
     * @param bucketName 桶名称
     * @param quotaByte 桶配额，单位：Byte
     * @param objCount 桶内对象数量
     */
    public CreateBucketRequest(String bucketName, Long quotaByte, Integer objCount) {
        super(bucketName);
        this.quotaByte = quotaByte;
        this.objCount = objCount;
    }
}
