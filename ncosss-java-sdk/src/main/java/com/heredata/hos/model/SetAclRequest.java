package com.heredata.hos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: SetAclRequest</p>
 * <p>Description: 设置权限信息 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/26 10:32
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SetAclRequest extends GenericRequest {
    /**
     * 桶的拥有者信息  {@link Owner}
     */
    private Owner owner;

    /**
     * 权限控制列表容器 {@link AccessControlList}
     */
    private AccessControlList accessControlList;

    /**
     * @param bucketName
     *              桶名称
     */
    public SetAclRequest(String bucketName) {
        this(bucketName, null);
    }

    /**
     * 设置桶ACL
     * @param bucketName
     *              桶名称
     * @param accessControlList
     *              权限控制列表容器 {@link AccessControlList}
     */
    public SetAclRequest(String bucketName, AccessControlList accessControlList) {
        super(bucketName);
        this.accessControlList = accessControlList;
        this.owner = accessControlList.getOwner();
    }

    /**
     * 设置对象ACL
     * @param bucketName
     *              桶名称
     * @param key
     *              对象名称
     * @param accessControlList
     *              权限控制列表容器 {@link AccessControlList}
     */
    public SetAclRequest(String bucketName, String key, AccessControlList accessControlList) {
        super(bucketName, key);
        this.accessControlList = accessControlList;
    }

    /**
     * 设置 权限控制列表容器 {@link AccessControlList}
     * @param accessControlList 权限控制列表容器 {@link AccessControlList}
     * @return
     */
    public SetAclRequest withCannedACL(AccessControlList accessControlList) {
        setAccessControlList(accessControlList);
        return this;
    }
}
