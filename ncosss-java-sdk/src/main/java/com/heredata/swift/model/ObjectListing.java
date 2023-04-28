package com.heredata.swift.model;

import com.heredata.model.GenericResult;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: ObjectListing</p>
 * <p>Description: TODO </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:55
 */
@Data
public class ObjectListing extends GenericResult {

    /**
     * 查询的对象列表信息  {@link SwiftObjectSummary}
     */
    private List<SwiftObjectSummary> objectSummaries = new ArrayList<>();

    /**
     * 桶名称
     */
    private String bucketName;

    /**
     * 限制查询对象名称的前缀
     */
    private String prefix;

    /**
     * 限制查询对象名称的前缀
     */
    private String startAfter;

    /**
     * 限制查询每次的数量
     */
    private Integer maxKeys;

    /**
     * 实际返回对象列表数量
     */
    private Integer keyCounts;

    /**
     * 下一页查询的起点，用作分页查询
     */
    private String nextStartAfter;

    public List<SwiftObjectSummary> getObjectSummaries() {
        return objectSummaries;
    }

    public void addObjectSummary(SwiftObjectSummary objectSummary) {
        this.objectSummaries.add(objectSummary);
    }

    public void setObjectSummaries(List<SwiftObjectSummary> objectSummaries) {
        this.objectSummaries.clear();
        if (objectSummaries != null && !objectSummaries.isEmpty()) {
            this.objectSummaries.addAll(objectSummaries);
        }
    }

    public void clearObjectSummaries() {
        this.objectSummaries.clear();
    }
}
