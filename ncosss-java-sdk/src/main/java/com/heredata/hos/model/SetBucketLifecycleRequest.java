package com.heredata.hos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: SetBucketLifecycleRequest</p>
 * <p>Description: 设置桶生命周期请求实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/26 10:36
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SetBucketLifecycleRequest extends GenericRequest {
    /**
     * 最大生命周期限制规则
     */
    public static final int MAX_LIFECYCLE_RULE_LIMIT = 1000;
    /**
     * 规则ID的最大长度
     */
    public static final int MAX_RULE_ID_LENGTH = 255;

    /**
     * 生命周期规则列表  {@link LifecycleRule}
     */
    private List<LifecycleRule> lifecycleRules = new ArrayList<>();

    /**
     * @param bucketName 桶名称
     */
    public SetBucketLifecycleRequest(String bucketName) {
        super(bucketName);
    }

    /**
     * 设置规则列表
     * @param lifecycleRules 生命周期规则列表  {@link LifecycleRule}
     */
    public void setLifecycleRules(List<LifecycleRule> lifecycleRules) {
        if (lifecycleRules == null || lifecycleRules.isEmpty()) {
            throw new IllegalArgumentException("lifecycleRules should not be null or empty.");
        }

        if (lifecycleRules.size() > MAX_LIFECYCLE_RULE_LIMIT) {
            throw new IllegalArgumentException("One bucket not allow exceed one thousand items of LifecycleRules.");
        }

        this.lifecycleRules.clear();
        this.lifecycleRules.addAll(lifecycleRules);
    }

    public void clearLifecycles() {
        this.lifecycleRules.clear();
    }
}
