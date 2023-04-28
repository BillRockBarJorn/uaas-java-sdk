package com.heredata.swift;

/**
 * Fluent builder for swift Client. Use of the builder is preferred over using
 * constructors of the client class.
 */
public interface SwiftBuilder {

    /**
     * @Title: swift连接构造器接口
     * @Description: 构造swift连接
     * @params [endpoint, account, accessKey, secretKey]
     * @return com.heredata.swift.Swift
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:26
     */
    Swift build(String endpoint, String account, String token);
}
