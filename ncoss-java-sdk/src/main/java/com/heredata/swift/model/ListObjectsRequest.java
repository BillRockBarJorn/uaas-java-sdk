package com.heredata.swift.model;


import com.heredata.swift.utils.SwiftUtils;
import lombok.Data;

/**
 * <p>Title: ListObjectsRequest</p>
 * <p>Description: TODO </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:50
 */
@Data
public class ListObjectsRequest extends GenericRequest {
    /**
     * 每次查询对象数量列表的做大值
     */
    private static final int MAX_RETURNED_KEYS_LIMIT = 1000;

    /**
     * 限制对象名称前缀查询
     */
    private String prefix;

    /**
     * 限制查询大于当前值的对象
     */
    private String startAfter;

    /**
     * 限制每次查询最大值数量
     */
    private Integer maxKeys;

    /**
     * 下一页的起始对象名称，用来做分页查询
     */
    private String nextStartAfter;

    public ListObjectsRequest(String bucketName) {
        this(bucketName, null, null, null);
    }

    /**
     * @param bucketName
     *            桶名称
     * @param prefix
     *            对象名称前缀
     * @param startAfter
     *            限制查询大于当前值的对象
     * @param maxKeys
     *            限制每次查询最大值数量
     */
    public ListObjectsRequest(String bucketName, String prefix, String startAfter, Integer maxKeys) {
        super(bucketName);
        setPrefix(prefix);
        setStartAfter(startAfter);
        if (maxKeys != null) {
            setMaxKeys(maxKeys);
        }
    }

    /**
     * 限制对象名称前缀查询
     * @param prefix 对象前缀
     * @return
     */
    public ListObjectsRequest withPrefix(String prefix) {
        setPrefix(prefix);
        return this;
    }

    /**
     * 限制查询大于当前值的对象
     * @param marker 限制查询对象名称大于等于当前值
     * @return
     */
    public ListObjectsRequest withStartAfter(String marker) {
        setStartAfter(marker);
        return this;
    }

    /**
     * 限制每次查询最大值数量
     * @param maxKeys
     */
    public void setMaxKeys(Integer maxKeys) {
        if (maxKeys < 0 || maxKeys > MAX_RETURNED_KEYS_LIMIT) {
            throw new IllegalArgumentException(SwiftUtils.SWIFT_RESOURCE_MANAGER.getString("MaxKeysOutOfRange"));
        }

        this.maxKeys = maxKeys;
    }

    /**
     * 限制每次查询最大值数量
     * @param maxKeys
     * @return
     */
    public ListObjectsRequest withMaxKeys(Integer maxKeys) {
        setMaxKeys(maxKeys);
        return this;
    }

    /**
     * 获取查询列表的容量。{@llink com.heredata.hos.model.ListObjectsRequest#maxKeys}<=容量值
     * @return
     */
    public static int getMaxReturnedKeysLimit() {
        return MAX_RETURNED_KEYS_LIMIT;
    }
}
