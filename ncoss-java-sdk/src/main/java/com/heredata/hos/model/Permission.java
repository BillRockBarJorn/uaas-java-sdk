package com.heredata.hos.model;

/**
 * Access Permission enum
 */

/**
 * <p>Title: Permission</p>
 * <p>Description: 授权人权限 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 15:56
 */
public enum Permission {

    /**
     * “；”前半部分是对桶的权限，后半部分对对象的权限
     */
    READ("READ", "获取该桶内对象列表、桶内多段任务、桶的元数据、桶的多版本；可以获取该对象内容和元数据"),
    WRITE("WRITE", "可以上传、覆盖和删除该桶内任何对象和段；此权限在对象上不适用"),
    READ_ACP("READ_ACP", "可以获取对应的桶的权限控制列表（ACL）；可以获取对应的对象的权限控制列表（ACL）"),
    WRITE_ACP("WRITE_ACP", "更新对应桶的权限控制列表（ACL）；更新对应对象的权限控制列表（ACL）。"),
    FULL_CONTROL("FULL_CONTROL", "拥有READ、WRITE的权限；拥有READ、WRITE、READ_ACP和WRITE_ACP的权限。");

    private String cannedAcl;

    private String description;

    Permission(String cannedAcl) {
        this.cannedAcl = cannedAcl;
    }

    Permission(String cannedAcl, String description) {
        this.cannedAcl = cannedAcl;
        this.description = description;
    }

    public String getCannedAcl() {
        return cannedAcl;
    }

    @Override
    public String toString() {
        return this.cannedAcl;
    }

    public static Permission parse(String acl) {
        for (Permission cacl : Permission.values()) {
            if (cacl.toString().equals(acl)) {
                return cacl;
            }
        }

        throw new IllegalArgumentException("Unable to parse the provided acl " + acl);
    }

}
