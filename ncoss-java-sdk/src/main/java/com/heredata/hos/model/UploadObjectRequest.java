package com.heredata.hos.model;

import lombok.Data;

/**
 * <p>Title: UploadFileRequest</p>
 * <p>Description: 上传对象请求实体，开启多分片上传 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 16:35
 */
@Data
public class UploadObjectRequest extends GenericRequest {

    /**
     * 每个分片的大小
     */
    private long partSize = 1024 * 1024 * 4;
    /**
     * 开启多线程上传，默认为1个线程
     */
    private int taskNum = 1;
    /**
     * 本地文件的路径
     */
    private String uploadFile;
    /**
     * 是否读取度文件，主要用来做断点续传
     */
    private boolean enableCheckpoint = false;
    /**
     * 进度文件在本地的位置
     */
    private String checkpointFile;
    /**
     * 对象元数据 {@link ObjectMetadata}
     */
    private ObjectMetadata objectMetadata;

    /**
     * @param bucketName 桶名称
     * @param key 对象名称
     */
    public UploadObjectRequest(String bucketName, String key) {
        super(bucketName, key);
    }

    /**
     *
     * @param bucketName 桶名称
     * @param key 对象名称
     * @param uploadFile 本地文件的路径
     * @param partSize 每个分片的大小
     * @param taskNum 开启多线程上传，默认为1个线程
     */
    public UploadObjectRequest(String bucketName, String key, String uploadFile, long partSize, int taskNum) {
        this(bucketName, key, uploadFile, partSize, taskNum, false, null);
    }

    /**
     * @param bucketName 桶名称
     * @param key 对象名称
     * @param uploadFile 本地文件的路径
     * @param partSize 每个分片的大小
     * @param taskNum 开启多线程上传，默认为1个线程
     * @param enableCheckpoint  是否读取度文件，主要用来做断点续传
     */
    public UploadObjectRequest(String bucketName, String key, String uploadFile, long partSize, int taskNum,
                               boolean enableCheckpoint) {
        this(bucketName, key, uploadFile, partSize, taskNum, enableCheckpoint, null);
    }

    /**
     * @param bucketName 桶名称
     * @param key 对象名称
     * @param uploadFile 本地文件的路径
     * @param partSize 每个分片的大小
     * @param taskNum 开启多线程上传，默认为1个线程
     * @param enableCheckpoint  是否读取度文件，主要用来做断点续传
     * @param checkpointFile 进度文件在本地的位置
     */
    public UploadObjectRequest(String bucketName, String key, String uploadFile, long partSize, int taskNum,
                               boolean enableCheckpoint, String checkpointFile) {
        super(bucketName, key);
        this.partSize = partSize;
        this.taskNum = taskNum;
        this.uploadFile = uploadFile;
        this.enableCheckpoint = enableCheckpoint;
        this.checkpointFile = checkpointFile;
    }

    public void setPartSize(long partSize) {
//        if (partSize < 1024 * 100) {
//            this.partSize = 1024 * 100;
//        } else {
            this.partSize = partSize;
//        }
    }

    public void setTaskNum(int taskNum) {
        if (taskNum < 1) {
            this.taskNum = 1;
        } else if (taskNum > 1000) {
            this.taskNum = 1000;
        } else {
            this.taskNum = taskNum;
        }
    }
}
