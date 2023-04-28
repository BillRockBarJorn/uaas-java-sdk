package com.heredata.hos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: ListVersionsRequest</p>
 * <p>Description: 查询版本对象列表 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 15:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListVersionsRequest extends GenericRequest {

    /**
     * 限制查询对象的前缀
     */
    private String prefix;

    /**
     * 限制查询对象名称大于等于当前值
     */
    private String startAfter;

    /**
     * 限制查询大于等于当前版本对象列表
     */
    private String versionIdMarker;

    /**
     * 限制查询数量   [1,1000]
     */
    private Integer maxKeys;


    /**
     * @param bucketName
     *            桶名称
     * @param prefix
     *            限制查询对象的前缀
     * @param startAfter
     *            限制查询对象名称大于等于当前值
     * @param versionIdMarker
     *            限制查询大于等于当前版本对象列表
     * @param maxKeys
     *            限制查询数量   [1,1000]
     *
     * @see ListVersionsRequest#ListVersionsRequest()
     */
    public ListVersionsRequest(String bucketName, String prefix, String startAfter, String versionIdMarker, Integer maxKeys) {
        super(bucketName);
        setPrefix(prefix);
        setStartAfter(startAfter);
        setVersionIdMarker(versionIdMarker);
        setMaxKeys(maxKeys);
    }

    /**
    * @Title: withBucketName
    * @Description: 设置桶名称
    * @params [bucketName]
    * @return com.heredata.hos.model.ListVersionsRequest
    * @author wuzz
    * @version 1.0.0
    * @createtime 2022/10/25 15:14
    */
    @Override
    public ListVersionsRequest withBucketName(String bucketName) {
        setBucketName(bucketName);
        return this;
    }



    /**
    * @Title: withPrefix
    * @Description: 设置对象前缀
    * @params [prefix]
    * @return com.heredata.hos.model.ListVersionsRequest
    * @author wuzz
    * @version 1.0.0
    * @createtime 2022/10/25 15:14
    */
    public ListVersionsRequest withPrefix(String prefix) {
        setPrefix(prefix);
        return this;
    }


    /**
    * @Title: withStartAfter
    * @Description: 限制查询对象名称大于等于当前值
    * @params [startAfter]
    * @return com.heredata.hos.model.ListVersionsRequest
    * @author wuzz
    * @version 1.0.0
    * @createtime 2022/10/25 15:15
    */
    public ListVersionsRequest withStartAfter(String startAfter) {
        setStartAfter(startAfter);
        return this;
    }

    /**
    * @Title: withVersionIdMarker
    * @Description: 限制查询大于等于当前版本对象列表
    * @params [versionIdMarker]
    * @return com.heredata.hos.model.ListVersionsRequest
    * @author wuzz
    * @version 1.0.0
    * @createtime 2022/10/25 15:15
    */
    public ListVersionsRequest withVersionIdMarker(String versionIdMarker) {
        setVersionIdMarker(versionIdMarker);
        return this;
    }

    /**
    * @Title: withMaxKeys
    * @Description: 限制查询大于等于当前版本对象列表
    * @params [maxKeys]
    * @return com.heredata.hos.model.ListVersionsRequest
    * @author wuzz
    * @version 1.0.0
    * @createtime 2022/10/25 15:16
    */
    public ListVersionsRequest withMaxKeys(Integer maxKeys) {
        setMaxKeys(maxKeys);
        return this;
    }
}
