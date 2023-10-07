package com.heredata.hos.model;

/**
 * <p>Title: Grantee</p>
 * <p>Description: 授权信信息容器 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 11:37
 */
public interface Grantee {

    /**
     * 获取有效值，有效值目前为CanonicalUser和Group
     * @return
     */
    String getIdentifier();
}
