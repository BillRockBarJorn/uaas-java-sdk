package com.heredata.swift.model;

import lombok.Data;

import java.io.File;
import java.io.InputStream;

/**
 * <p>Title: PutObjectRequest</p>
 * <p>Description: TODO </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:59
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
     * @param bucketName
     *            桶名称
     * @param key
     *            对象名称
     * @param file
     *            文件对象
     */
    public PutObjectRequest(String bucketName, String key, File file) {
        super(bucketName, key);
        this.file = file;
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
        super(bucketName, key);
        this.inputStream = input;
    }
}
