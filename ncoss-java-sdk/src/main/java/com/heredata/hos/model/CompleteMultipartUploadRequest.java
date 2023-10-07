package com.heredata.hos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于完成分片上传类。包装了完成上传的所有的参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompleteMultipartUploadRequest extends GenericRequest {

    /**
     * 唯一上传id
     */
    private String uploadId;

    /**
     * 每个分片的标签类
     */
    private List<PartETag> partETags = new ArrayList<PartETag>();

    /**
     * @param bucketName
     *            桶桶名
     * @param key
     *            对象名称.
     * @param uploadId
     *            唯一上传id
     * @param partETags
     *            每个分片的标签类
     */
    public CompleteMultipartUploadRequest(String bucketName, String key, String uploadId, List<PartETag> partETags) {
        super(bucketName, key);
        this.uploadId = uploadId;
        this.partETags = partETags;
    }
}
