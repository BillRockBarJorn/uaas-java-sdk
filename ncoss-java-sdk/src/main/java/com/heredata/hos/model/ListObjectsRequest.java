package com.heredata.hos.model;

import com.heredata.hos.utils.HOSUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: ListObjectsRequest</p>
 * <p>Description: 对象列表请求实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 14:49
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ListObjectsRequest extends GenericRequest {
    /**
     * 每次查询对象数量列表的做大致
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
     * 是否开启版本查询
     */
    private boolean isVersion = false;

    /**
     * 下一页的起始对象名称，用来做分页查询
     */
    private String nextStartAfter;

    /**
     * @param bucketName
     *           桶名称
     */
    public ListObjectsRequest(String bucketName) {
        this(bucketName, null, null, null);
    }

    /**
     * @param bucketName
     *            桶名称
     * @param prefix
     *            限制对象名称前缀查询
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
     * @Title: withPrefix
     * @Description: 限制对象名称前缀查询
     * @params [prefix]
     * @return com.heredata.hos.model.ListObjectsRequest
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/25 14:57
     */
    public ListObjectsRequest withPrefix(String prefix) {
        setPrefix(prefix);
        return this;
    }


    /**
     * @Title: withStartAfter
     * @Description: 限制查询大于当前值的对象
     * @params [marker]
     * @return com.heredata.hos.model.ListObjectsRequest
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/25 14:57
     */
    public ListObjectsRequest withStartAfter(String marker) {
        setStartAfter(marker);
        return this;
    }

    /**
     * @Title: setMaxKeys
     * @Description: 限制每次查询最大值数量
     * @params [maxKeys]
     * @return void
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/25 14:57
     */
    public void setMaxKeys(Integer maxKeys) {
        if (maxKeys < 0 || maxKeys > MAX_RETURNED_KEYS_LIMIT) {
            throw new IllegalArgumentException(HOSUtils.HOS_RESOURCE_MANAGER.getString("MaxKeysOutOfRange"));
        }

        this.maxKeys = maxKeys;
    }

    /**
     * @Title: setMaxKeys
     * @Description: 限制每次查询最大值数量
     * @params [maxKeys]
     * @return void
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/25 14:57
     */
    public ListObjectsRequest withMaxKeys(Integer maxKeys) {
        setMaxKeys(maxKeys);
        return this;
    }

    /**
     * 获取查询列表的容量。{@link ListObjectsRequest#maxKeys}<=容量值
     * @return
     */
    public static int getMaxReturnedKeysLimit() {
        return MAX_RETURNED_KEYS_LIMIT;
    }

    /**
     * 是否开启版本查询
     * @return
     */
    public boolean isVersion() {
        return isVersion;
    }
}
