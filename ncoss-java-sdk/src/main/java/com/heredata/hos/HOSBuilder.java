package com.heredata.hos;

import com.heredata.ClientConfiguration;

/**
 * <p>Title: HOSBuilder</p>
 * <p>Description: HOS构造器 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:16
 */
public interface HOSBuilder {

    /**
     * @Title: HOS连接构造器接口  适用对象存储版本3.*
     * @Description: 构造ncHOS连接
     * @params [endpoint, account, accessKey, secretKey]
     * @return com.heredata.hos.HOS
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:26
     */
    HOS build(String endpoint, String account, String accessKey, String secretKey);

    /**
     * @Title: HOS连接构造器接口   适用版本3.*
     * @Description: 构造ncHOS连接
     * @params [endpoint, account, accessKey, secretKey,clientConfiguration]
     * @return com.heredata.hos.HOS
     * @author wuzz
     * @version 1.0.0
     * @createtime 2023/07/04 10:26
     */
    HOS build(String endpoint, String account, String accessKey, String secretKey, ClientConfiguration clientConfiguration);

    /**
     * @Title: HOS连接构造器接口  适用版本3.*
     * @Description: 构造ncHOS连接
     * @params [endpoint, account, accessKey, secretKey,clientConfiguration]
     * @return com.heredata.hos.HOS
     * @author wuzz
     * @version 1.0.0
     * @createtime 2023/07/04 10:26
     */
    HOS build(String endpoint, String account, String accessKey, String secretKey, ClientConfiguration clientConfiguration, String bucket);


    /**
     * HOS连接构造器接口，秘钥必须使用永久秘钥  适用版本4.*
     * @param endpoint NCOSS的endpoint
     * @param accessKey 接入秘钥
     * @param secretKey 秘钥
     * @return
     */
    HOS build(String endpoint, String accessKey, String secretKey);

    /**
     * HOS连接构造器接口，秘钥必须使用永久秘钥  适用版本4.*
     * @param endpoint NCOSS的endpoint
     * @param accessKey 接入秘钥
     * @param secretKey 秘钥
     * @param clientConfiguration  客户端配置类
     * @return
     */
    HOS build(String endpoint, String accessKey, String secretKey, ClientConfiguration clientConfiguration);

    /**
     * HOS连接构造器接口，秘钥必须使用永久秘钥  适用版本4.*
     * @param endpoint NCOSS的endpoint
     * @param accessKey 接入秘钥
     * @param secretKey 秘钥
     * @param clientConfiguration 客户端配置类
     * @param bucket  将桶名固定，使用该接口创建hos连接，如果其他接口不传桶名，默认使用该桶名
     * @return
     */
    HOS build(String endpoint, String accessKey, String secretKey, ClientConfiguration clientConfiguration, String bucket);

}
