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
     * @Title: HOS连接构造器接口
     * @Description: 构造ncHOS连接
     * @params [endpoint, account, accessKey, secretKey]
     * @return com.heredata.hos.HOS
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:26
     */
    HOS build(String endpoint, String account, String accessKey, String secretKey);

    /**
     * @Title: HOS连接构造器接口
     * @Description: 构造ncHOS连接
     * @params [endpoint, account, accessKey, secretKey,clientConfiguration]
     * @return com.heredata.hos.HOS
     * @author wuzz
     * @version 1.0.0
     * @createtime 2023/07/04 10:26
     */
    HOS build(String endpoint, String account, String accessKey, String secretKey, ClientConfiguration clientConfiguration);

}
