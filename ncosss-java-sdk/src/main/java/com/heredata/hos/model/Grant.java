package com.heredata.hos.model;

import lombok.Data;

/**
 * <p>Title: Grant</p>
 * <p>Description: ACL的权限授予信息 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 11:28
 */
@Data
public class Grant {

    /**
     * 授权信信息容器
     * 两个实现类，{@link CanonicalUserGrantee}和{@link GroupGrantee}
     */
    private Grantee grantee;

    /**
     * 授权人权限  {@link Permission}
     */
    private Permission permission;

    public Grant(Grantee grantee, Permission permission) {
        if (grantee == null || permission == null) {
            throw new NullPointerException();
        }

        this.grantee = grantee;
        this.permission = permission;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Grant)) {
            return false;
        }
        Grant g = (Grant) o;
        return this.getGrantee().getIdentifier().equals(g.getGrantee().getIdentifier())
                && this.getPermission().equals(g.getPermission());
    }

    @Override
    public int hashCode() {
        return (grantee.getIdentifier() + ":" + this.getPermission().toString()).hashCode();
    }

    @Override
    public String toString() {
        return "Grant [grantee=" + getGrantee() + ",permission=" + getPermission() + "]";
    }
}
