package com.heredata.hos.model;

import com.heredata.exception.ClientException;
import com.heredata.swift.model.DownloadFileRequest;
import com.heredata.utils.StringUtils;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * <p>Title: GetObjectRequest</p>
 * <p>Description: 这个类是下载时{@link DownloadFileRequest}使用到的类，封装了一下必要的信息 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 10:21
 */
@Data
public class GetObjectRequest extends GenericRequest {
    /**
     * 约束列表，如果当前分片在列表中则下载，反之不下载
     */
    private List<String> matchingETagConstraints = new ArrayList<String>();
    /**
     * 非约束列表，如果下载标签不在一下列表中，则下载，反之不下载
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
     * 获取对象流文件的范围(end,start]
     */
    private long[] range;

    /**
     * 是否包含流，true为包含流，false不包含
     */
    private boolean includeInputStream;

    // 下面三个属性是客户端加密使用到参数
    /**
     * 客户端加密，加密算法，目前仅支持AES256
     */
    private String clientSideEncryptionAlgorithm;
    /**
     * 客户端加密，加密秘钥，256长度字符串，再经过base64编码
     */
    private String clientSideEncryptionKey;
    /**
     * 客户端加密，clientSideEncryptionKey属性的MD5值
     */
    private String clientSideEncryptionKeyMD5;

    public GetObjectRequest(String bucketName, String key) {
        super(bucketName, key);
    }

    public GetObjectRequest(String bucketName, String key, String versionId) {
        super(bucketName, key);
        setVersionId(versionId);
    }

    public void setClientSideEncryptionAlgorithm(AlgorithmEnum algorithmEnum) {
        this.clientSideEncryptionAlgorithm = algorithmEnum.getAlgorithm();
    }


    public void setClientSideEncryptionKey(String clientSideEncryptionKey) {
        if (clientSideEncryptionKey == null && clientSideEncryptionKey.length() != 32) {
            throw new ClientException("非法的字符串长度");
        }
        String s = Base64.getEncoder().encodeToString(clientSideEncryptionKey.getBytes(StandardCharsets.UTF_8));
        this.clientSideEncryptionKey = s;
        byte[] bytes = StringUtils.encrypByMD5Arr(clientSideEncryptionKey);
        this.clientSideEncryptionKeyMD5 = Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 设置流范围。(start,end]
     * @param start
     * @param end
     */
    public void setRange(long start, long end) {
        range = new long[]{start, end};
    }

    /**
     * 设置流范围。(start,end]
     * @param start
     * @param end
     * @return
     */
    public GetObjectRequest withRange(long start, long end) {
        setRange(start, end);
        return this;
    }

    /**
     * 设置约束标签列表
     * @param eTagList
     */
    public void setMatchingETagConstraints(List<String> eTagList) {
        this.matchingETagConstraints.clear();
        if (eTagList != null && !eTagList.isEmpty()) {
            this.matchingETagConstraints.addAll(eTagList);
        }
    }

    /**
     * 清空约束标签列表
     */
    public void clearMatchingETagConstraints() {
        this.matchingETagConstraints.clear();
    }

    /**
     * 设置约束标签列表
     * @param eTagList
     */
    public void setNonmatchingETagConstraints(List<String> eTagList) {
        this.nonmatchingEtagConstraints.clear();
        if (eTagList != null && !eTagList.isEmpty()) {
            this.nonmatchingEtagConstraints.addAll(eTagList);
        }
    }

    /**
     * 清空约束标签列表
     */
    public void clearNonmatchingETagConstraints() {
        this.nonmatchingEtagConstraints.clear();
    }
}
