package com.heredata.hos.model;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: ObjectListing</p>
 * <p>Description: 查询对象列表成功后返回的实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 15:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObjectListing extends GenericResult {

    /**
     * 查询的对象列表信息  {@link HOSObjectSummary}
     */
    private List<HOSObjectSummary> objectSummaries = new ArrayList<>();

    /**
     * 桶名称
     */
    private String bucketName;

    /**
     * 当前列表后面是否还有数据，用作分页
     */
    private boolean isTruncated;

    /**
     * 限制查询对象名称的前缀
     */
    private String prefix;

    /**
     * 限制查询对象名称的前缀
     */
    private String marker;

    /**
     * 限制查询每次的数量
     */
    private int maxKeys;

    /**
     * 实际返回对象列表数量
     */
    private int keyCounts;

    /**
     * 下一页查询的起点，用作分页查询
     */
    private String delimiter;


    public void addObjectSummary(HOSObjectSummary objectSummary) {
        this.objectSummaries.add(objectSummary);
    }

    public void setObjectSummaries(List<HOSObjectSummary> objectSummaries) {
        this.objectSummaries.clear();
        if (objectSummaries != null && !objectSummaries.isEmpty()) {
            this.objectSummaries.addAll(objectSummaries);
        }
    }

    public void clearObjectSummaries() {
        this.objectSummaries.clear();
    }
}
