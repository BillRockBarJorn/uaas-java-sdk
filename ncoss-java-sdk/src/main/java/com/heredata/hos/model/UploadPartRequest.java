package com.heredata.hos.model;

import com.heredata.comm.io.BoundedInputStream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

/**
 * <p>Title: UploadPartRequest</p>
 * <p>Description: 分片上传请求实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/26 10:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadPartRequest extends GenericRequest {
    /**
     * 默认分片大小4MB
     */
    private final long defaultPartSize = 1024 * 1024 * 4;

    /**
     * 全局唯一上传id，初始化任务后{@link InitiateMultipartUploadRequest}后会
     * 返回uploadId{@link InitiateMultipartUploadResult#getUploadId()}
     */
    private String uploadId;

    /**
     * 当前分片在整个文件中的索引位置    从1开始
     */
    private int partNumber;

    /**
     * 分片大小   单位：Byte
     */
    private long partSize = -1;

    /**
     * 分片流内容的MD5
     */
    private String md5Digest;

    /**
     * 分片中流信息
     */
    private InputStream inputStream;

    /**
     * 是否对流信息进行ChunkEncoding
     */
    private boolean useChunkEncoding = false;

    /**
     * 对象元数据
     */
    private ObjectMetadata objectMetadata;

    /**
     * @param bucketName 桶名称
     * @param key 对象名称
     */
    public UploadPartRequest(String bucketName, String key) {
        super(bucketName, key);
    }

    /**
     * @param bucketName 桶名称
     * @param key 对象名称
     * @param uploadId 全局唯一上传id
     * @param partNumber 当前分片在整个文件中的索引位置    从1开始
     * @param inputStream 分片中流信息
     * @param partSize 分片大小   单位：Byte
     */
    public UploadPartRequest(String bucketName, String key, String uploadId, int partNumber, InputStream inputStream,
                             long partSize) {
        super(bucketName, key);
        this.uploadId = uploadId;
        this.partNumber = partNumber;
        this.inputStream = inputStream;
        this.partSize = partSize;
    }

    /**
     * 设置分片大小
     * @param partSize
     */
    public void setPartSize(long partSize) {
//        if (partSize <= 1024 * 100) {
//            partSize = defaultPartSize;
//        }
        this.partSize = partSize;
    }

    /**
     * 是否对分片内容进行ChunkEncoding
     * @return
     */
    public boolean isUseChunkEncoding() {
        return useChunkEncoding || (this.partSize == -1);
    }

    public BoundedInputStream buildPartialStream() {
        return new BoundedInputStream(inputStream, (int) partSize);
    }
}
