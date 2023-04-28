package com.heredata.hos.model;

import lombok.Data;

import java.io.File;
import java.io.InputStream;

/**
 * <p>Title: PutObjectRequest</p>
 * <p>Description: 普通对象删除 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 16:01
 */
@Data
public class PutObjectRequest extends GenericRequest {
    /**
     * 文件对象
     */
    private File file;
    /**
     * 文件流对象
     */
    private InputStream inputStream;
    /**
     * 对象元数据
     */
    private ObjectMetadata metadata;

    /**
     * @param bucketName
     *            桶名称
     * @param key
     *            对象名称
     * @param file
     *            文件对象
     */
    public PutObjectRequest(String bucketName, String key, File file) {
        this(bucketName, key, file, null);
    }

    /**
     * @param bucketName
     *            桶名称
     * @param key
     *            对象名称
     * @param file
     *            文件对象
     * @param metadata
     *            对象元数据
     */
    public PutObjectRequest(String bucketName, String key, File file, ObjectMetadata metadata) {
        super(bucketName, key);
        this.file = file;
        this.metadata = metadata;
    }

    /**
     * @param bucketName
     *            桶名称
     * @param key
     *            对象名称
     * @param input
     *            对象流数据
     */
    public PutObjectRequest(String bucketName, String key, InputStream input) {
        this(bucketName, key, input, null);
    }

    /**
     * @param bucketName
     *            桶名称
     * @param key
     *            对象名称
     * @param input
     *            对象流数据
     * @param metadata
     *            对象元数据
     */
    public PutObjectRequest(String bucketName, String key, InputStream input, ObjectMetadata metadata) {
        super(bucketName, key);
        this.inputStream = input;
        this.metadata = metadata;
    }
}
