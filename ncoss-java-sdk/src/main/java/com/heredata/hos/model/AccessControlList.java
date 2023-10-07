package com.heredata.hos.model;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * <p>Title: AccessControlList</p>
 * <p>Description:  请求桶的访问控制列表。请求成功后返回的实体</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 19:10
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccessControlList extends GenericResult implements Serializable {

    private static final long serialVersionUID = 211267925081748283L;

    /**
     * 桶的所属账户ID
     */
    private Owner owner;

    /**
     * 权限控制信息容器 {@link Grant}
     */
    private HashSet<Grant> grants = new HashSet<Grant>();

    /**
     * @param grantee
     *       授权信信息容器
     *       两个实现类，{@link CanonicalUserGrantee}和{@link GroupGrantee}
     * @param permission
     *       授权人权限  {@link Permission}
     */
    public void grantPermission(Grantee grantee, Permission permission) {
        if (grantee == null || permission == null) {
            throw new NullPointerException();
        }

        grants.add(new Grant(grantee, permission));
    }

    /**
     * 移除所有权限
     * @param grantee
     *        授权信信息容器
     *        两个实现类，{@link CanonicalUserGrantee}和{@link GroupGrantee}
     */
    public void revokeAllPermissions(Grantee grantee) {
        if (grantee == null) {
            throw new NullPointerException();
        }

        ArrayList<Grant> grantsToRemove = new ArrayList<Grant>();
        for (Grant g : grants) {
            if (g.getGrantee().equals(grantee)) {
                grantsToRemove.add(g);
            }
        }
        grants.removeAll(grantsToRemove);
    }
}
