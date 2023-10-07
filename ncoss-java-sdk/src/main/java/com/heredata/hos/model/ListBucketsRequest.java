package com.heredata.hos.model;


import com.heredata.hos.utils.HOSUtils;
import com.heredata.model.WebServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: ListBucketsRequest</p>
 * <p>Description: 查询桶列表条件查询对象 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 14:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListBucketsRequest extends WebServiceRequest {

    /**
     * 每次最多查询1000个
     */
    public static final int MAX_RETURNED_KEYS = 1000;

    /**
     * 查询指定桶前缀的桶
     */
    private String prefix;

    /**
     * 查询桶名称大于startAfter的桶（按照字典序大于）
     */
    private String startAfter;

    /**
     * 限制每次查询最大桶数量
     */
    private Integer maxKeys;

    /**
     * @Title: withPrefix
     * @Description: 设置查询桶前缀
     * @params [prefix]
     * @return com.heredata.hos.model.ListBucketsRequest
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/25 14:44
     */
    public ListBucketsRequest withPrefix(String prefix) {
        setPrefix(prefix);
        return this;
    }


    /**
     * @Title: withStartAfter
     * @Description: 查询桶名称大于startAfter的桶（按照字典序大于）
     * @params [marker]
     * @return com.heredata.hos.model.ListBucketsRequest
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/25 14:44
     */
    public ListBucketsRequest withStartAfter(String marker) {
        setStartAfter(marker);
        return this;
    }

    /**
     * @Title: setMaxKeys
     * @Description: 限制每次查询最大桶数量
     * @params [maxKeys]
     * @return void
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/25 14:45
     */
    public void setMaxKeys(Integer maxKeys) {
        int tmp = maxKeys.intValue();
        if (tmp < 0 || tmp > MAX_RETURNED_KEYS) {
            throw new IllegalArgumentException(HOSUtils.HOS_RESOURCE_MANAGER.getString("MaxKeysOutOfRange"));
        }
        this.maxKeys = maxKeys;
    }

    /**
     * @Title: withMaxKeys
     * @Description: 限制每次查询最大桶数量
     * @params [maxKeys]
     * @return void
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/25 14:45
     */
    public ListBucketsRequest withMaxKeys(Integer maxKeys) {
        setMaxKeys(maxKeys);
        return this;
    }
}
