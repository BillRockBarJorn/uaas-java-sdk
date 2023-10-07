package com.heredata.hos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 终止分片上传请求实体。当发送此请求后，所有已上传的分片会被删除
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbortMultipartUploadRequest extends GenericRequest {

    /**
     * 唯一上传id
     */
    private String uploadId;

    /**
     * @param bucketName
     *           桶名称
     * @param key
     *           对象名称
     * @param uploadId
     *            唯一是上传id
     */
    public AbortMultipartUploadRequest(String bucketName, String key, String uploadId) {
        super(bucketName, key);
        this.uploadId = uploadId;
    }
}
