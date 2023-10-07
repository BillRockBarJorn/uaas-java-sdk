package com.heredata.hos.model;


/**
 * <p>Title: GroupGrantee</p>
 * <p>Description: 范围授权,按照组进行授权 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 11:38
 */
public enum GroupGrantee implements Grantee {
    /**
     * 所有人包括匿名用户
     */
    AllUsers("http://www.heredata.com/groups/global/AllUsers"),
    /**
     * 代表经过身份认证的所有用户
     */
    AuthenticatedUsers("http://www.heredata.com/groups/global/AuthenticatedUsers");

    private String groupUri;

    private GroupGrantee(String groupUri) {
        this.groupUri = groupUri;
    }

    public String getGroupUri() {
        return groupUri;
    }

    public void setGroupUri(String groupUri) {
        this.groupUri = groupUri;
    }

    /**
     * The group's Id.
     */
    @Override
    public String getIdentifier() {
        return this.groupUri;
    }
}
