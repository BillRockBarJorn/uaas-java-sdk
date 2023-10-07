package com.heredata.swift.model.bucket;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

/**
 * <p>Title: Bucket</p>
 * <p>Description: 查询桶信息成功响应的实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 17:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Bucket extends GenericResult {

    /**
     * 桶名称
     */
    private String bucketName;

    /**
     * 当前桶使用的空间  单位：Byte
     */
    private Long bytesUsed;

    /**
     * 当前桶存在对象数量
     */
    private Integer objCount;

    /**
     * 桶配额大小，单位：Byte
     */
    private Long quotaByte;

    /**
     * 桶配额对象数量
     */
    private Integer quotaCount;

    /**
     * 桶的权限信息
     */
    private BucketAclRequest bucketAclRequest;

    /**
     * 桶元数据
     */
    private Map<String, String> meta;

    /**
     * @param bucketName
     *            Bucket name.
     */
    public Bucket(String bucketName) {
        this.bucketName = bucketName;
    }

    public Bucket(String bucketName, String requestId) {
        setBucketName(bucketName);
        setRequestId(requestId);
    }
}
