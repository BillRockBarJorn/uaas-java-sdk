package com.heredata.hos.model;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: VersionListing</p>
 * <p>Description: 查询版本对象列表成功响应后返回的实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/26 10:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VersionListing extends GenericResult {
    /**
     * 对象列表  {@link HOSVersionSummary}
     */
    private List<HOSVersionSummary> versionSummaries =
            new ArrayList<HOSVersionSummary>();

    /**
     * 桶名称
     */
    private String bucketName;

    /**
     * 对象名称下一些的起点，用于分页查询
     */
    private String nextStartAfter;

    /**
     * 下一页版本Id的起始值，用于分页查询
     */
    private String nextVersionIdMarker;

    /**
     * 列表是否被阶段即当前列表后面是否还有数据
     */
    private boolean isTruncated;

    /**
     * 限制对象名称前缀
     */
    private String prefix;

    /**
     * 限制对象名称大于等于当前值
     */
    private String startAfter;

    /**
     * 限制版本信息大于等于当前值
     */
    private String versionIdMarker;

    /**
     * 每次查询时做多查多少条数据信息
     */
    private int maxKeys;
}
