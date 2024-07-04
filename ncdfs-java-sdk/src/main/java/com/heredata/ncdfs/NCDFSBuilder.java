package com.heredata.ncdfs;

/**
 * Fluent builder for HOS Client. Use of the builder is preferred over using
 * constructors of the client class.
 */
public interface NCDFSBuilder {

    /**
     * @Title: HOS连接构造器接口
     * @Description: 构造ncHOS连接
     * @params [endpoint, account, accessKey, secretKey]
     * @return com.heredata.hos.HOS
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:26
     */
    NCDFS build(String endpoint, String token);
}
