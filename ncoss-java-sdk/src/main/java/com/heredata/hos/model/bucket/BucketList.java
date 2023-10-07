package com.heredata.hos.model.bucket;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: BucketList</p>
 * <p>Description: 查询桶列表 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 17:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BucketList extends GenericResult {
    /**
     * 桶列表信息  {@link Bucket}
     */
    private List<Bucket> buckets = new ArrayList<>();
    /**
     * 限制桶名称前缀
     */
    private String prefix;
    /**
     * 限制查询每次列表的最大数量
     */
    private Integer maxKeys;

    /**
     * 当前列表后面是否还是数据，用于做分页
     */
    private boolean isTruncated;

    /**
     * 限制桶名称大于等于当前值
     */
    private String startAfter;

    /**
     * 下一页的起点信息，用于做分页
     */
    private String nextStartAfter;

    /**
     * @param buckets 设置桶列表  {@link Bucket}
     */
    public void setBucketList(List<Bucket> buckets) {
        this.buckets.clear();
        if (buckets != null && !buckets.isEmpty()) {
            this.buckets.addAll(buckets);
        }
    }

    public void clearBucketList() {
        this.buckets.clear();
    }
}
