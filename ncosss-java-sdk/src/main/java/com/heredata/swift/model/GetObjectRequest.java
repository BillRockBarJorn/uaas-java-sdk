package com.heredata.swift.model;

import lombok.Data;

/**
 * <p>Title: GetObjectRequest</p>
 * <p>Description: TODO </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:42
 */
@Data
public class GetObjectRequest extends GenericRequest {

    /**
     * 获取对象流文件的范围(end,start]
     */
    private long[] range;


    /**
     * @param bucketName
     *            桶名称
     * @param key
     *            对象名称
     */
    public GetObjectRequest(String bucketName, String key) {
        super(bucketName, key);
    }

    /**
     * 设置流范围。(start,end]
     * @param start
     * @param end
     * @return
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
}
