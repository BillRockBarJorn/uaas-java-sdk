package com.heredata.swift.model;

import com.heredata.hos.model.GenericRequest;
import com.heredata.utils.BinaryUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>Title: DownloadFileRequest</p>
 * <p>Description: 下载请求实体。可以实现断点下载 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 9:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadFileRequest extends GenericRequest {

    /**
     * 每次下载的分片大小，默认为4MB
     */
    private long partSize = 1024 * 1024 * 4;
    /**
     * 开启多少个线程进行下载，默认为1个
     */
    private int taskNum = 1;
    /**
     * 下载到本地哪个文件中，即下载文件路径
     */
    private String downloadFile;
    /**
     * 是否检查下载进度文件，从这个文件中可以知道下载的进度
     */
    private boolean enableCheckpoint;
    /**
     * 检查进度文件的路径，从这个文件中可以知道下载的进度
     */
    private String checkpointFile;

    /**
     * 匹配的ETag约束
     */
    private List<String> matchingETagConstraints = new ArrayList<String>();
    /**
     * 不匹配的ETag约束
     */
    private List<String> nonmatchingEtagConstraints = new ArrayList<String>();
    /**
     * 未修改的自约束
     */
    private Date unmodifiedSinceConstraint;
    /**
     * 修改的自约束
     */
    private Date modifiedSinceConstraint;
    /**
     * 要下载的对象的范围，如果有规定范围，里面其实就两个元素
     */
    private long[] range;

    public DownloadFileRequest(String bucketName, String key) {
        super(bucketName, key);
    }

    public DownloadFileRequest(String bucketName, String key, String downloadFile, long partSize) {
        super(bucketName, key);
        this.partSize = partSize;
        this.downloadFile = downloadFile;
    }

    public DownloadFileRequest(String bucketName, String key, String downloadFile, long partSize, int taskNum,
                               boolean enableCheckpoint) {
        this(bucketName, key, downloadFile, partSize, taskNum, enableCheckpoint, null);
    }

    public DownloadFileRequest(String bucketName, String key, String downloadFile, long partSize, int taskNum,
                               boolean enableCheckpoint, String checkpointFile) {
        super(bucketName, key);
        this.partSize = partSize;
        this.taskNum = taskNum;
        this.downloadFile = downloadFile;
        this.enableCheckpoint = enableCheckpoint;
        this.checkpointFile = checkpointFile;
    }

    public String getTempDownloadFile() {
        if (getVersionId() != null) {
            return downloadFile + "." + BinaryUtil.bytesToHex(BinaryUtil.calculateSha256(getVersionId().getBytes())) + ".tmp";
        } else {
            return downloadFile + ".tmp";
        }
    }

    public boolean isEnableCheckpoint() {
        return enableCheckpoint;
    }


    /**
     * 设置匹配标签的列表。这样子只会下载匹配标签的分片
     */
    public void setMatchingETagConstraints(List<String> eTagList) {
        this.matchingETagConstraints.clear();
        if (eTagList != null && !eTagList.isEmpty()) {
            this.matchingETagConstraints.addAll(eTagList);
        }
    }

    /**
     * 清除约束标签列表
     */
    public void clearMatchingETagConstraints() {
        this.matchingETagConstraints.clear();
    }

    /**
     * 设置非匹配标签的列表。这样子分片的标签不在列表里面就会下载，否则不会下载
     */
    public void setNonmatchingETagConstraints(List<String> eTagList) {
        this.nonmatchingEtagConstraints.clear();
        if (eTagList != null && !eTagList.isEmpty()) {
            this.nonmatchingEtagConstraints.addAll(eTagList);
        }
    }

    /**
     * 清空非约束列表
     */
    public void clearNonmatchingETagConstraints() {
        this.nonmatchingEtagConstraints.clear();
    }

    /**
     * 设置下载范围(start,end]
     * @param start
     * @param end
     */
    public void setRange(long start, long end) {
        range = new long[]{start, end};
    }
}
