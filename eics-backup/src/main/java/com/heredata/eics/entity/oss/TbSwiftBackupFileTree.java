package com.heredata.eics.entity.oss;


import java.util.Date;

public class TbSwiftBackupFileTree {
    /**
     * 主键
     */

    private String id;

    /**
     * 名字
     */
    private String name;

    /**
     * 虚拟目录ID
     */
    private String virtualpathid;

    /**
     * 类型（1 文件，2 目录）
     */
    private String itype;

    /**
     * 源服务器
     */
    private String sourceserver;

    /**
     * 文件路径
     */

    private String fileUrl;

    /**
     * 大小
     */
    private String size;

    /**
     * 父节点ID
     */

    private String parentId;

    /**
     * 文件节点存储至Swift别名
     */
    private String alias;

    /**
     * 文件状态：0，在Swift已删除，1，文件正常;2 定时删除；3，文件信息保存，文件未上传到存储
     */
    private String status;

    /**
     * 关联备份Id
     */

    private String backupid;

    /**
     * 用户ID
     */
    private String userid;

    /**
     * 插入时间
     */

    private Date updateTime;

    /**
     * 顶级目录ID 文件11、目录12、虚拟机13、数据库14、其它15
     */
    private String topid;


    private Date endTime;


    private String vmPath;

    /**
     * 备份类型 F1全量 F2增量
     */
    private String subtype;

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public String getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取名字
     *
     * @return name - 名字
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名字
     *
     * @param name 名字
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取虚拟目录ID
     *
     * @return virtualpathid - 虚拟目录ID
     */
    public String getVirtualpathid() {
        return virtualpathid;
    }

    /**
     * 设置虚拟目录ID
     *
     * @param virtualpathid 虚拟目录ID
     */
    public void setVirtualpathid(String virtualpathid) {
        this.virtualpathid = virtualpathid;
    }

    /**
     * 获取类型（1 文件，2 目录）
     *
     * @return itype - 类型（1 文件，2 目录）
     */
    public String getItype() {
        return itype;
    }

    /**
     * 设置类型（1 文件，2 目录）
     *
     * @param itype 类型（1 文件，2 目录）
     */
    public void setItype(String itype) {
        this.itype = itype;
    }

    /**
     * 获取源服务器
     *
     * @return sourceserver - 源服务器
     */
    public String getSourceserver() {
        return sourceserver;
    }

    /**
     * 设置源服务器
     *
     * @param sourceserver 源服务器
     */
    public void setSourceserver(String sourceserver) {
        this.sourceserver = sourceserver;
    }

    /**
     * 获取文件路径
     *
     * @return file_url - 文件路径
     */
    public String getFileUrl() {
        return fileUrl;
    }

    /**
     * 设置文件路径
     *
     * @param fileUrl 文件路径
     */
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    /**
     * 获取大小
     *
     * @return size - 大小
     */
    public String getSize() {
        return size;
    }

    /**
     * 设置大小
     *
     * @param size 大小
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * 获取父节点ID
     *
     * @return parent_id - 父节点ID
     */
    public String getParentId() {
        return parentId;
    }

    /**
     * 设置父节点ID
     *
     * @param parentId 父节点ID
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * 获取文件节点存储至Swift别名
     *
     * @return alias - 文件节点存储至Swift别名
     */
    public String getAlias() {
        return alias;
    }

    /**
     * 设置文件节点存储至Swift别名
     *
     * @param alias 文件节点存储至Swift别名
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * 获取文件状态：0，在Swift已删除，1，文件正常;2 定时删除；3，文件信息保存，文件未上传到存储
     *
     * @return status - 文件状态：0，在Swift已删除，1，文件正常;2 定时删除；3，文件信息保存，文件未上传到存储
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置文件状态：0，在Swift已删除，1，文件正常;2 定时删除；3，文件信息保存，文件未上传到存储
     *
     * @param status 文件状态：0，在Swift已删除，1，文件正常;2 定时删除；3，文件信息保存，文件未上传到存储
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取关联备份Id
     *
     * @return backupId - 关联备份Id
     */
    public String getBackupid() {
        return backupid;
    }

    /**
     * 设置关联备份Id
     *
     * @param backupid 关联备份Id
     */
    public void setBackupid(String backupid) {
        this.backupid = backupid;
    }

    /**
     * 获取用户ID
     *
     * @return userid - 用户ID
     */
    public String getUserid() {
        return userid;
    }

    /**
     * 设置用户ID
     *
     * @param userid 用户ID
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * 获取插入时间
     *
     * @return update_time - 插入时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置插入时间
     *
     * @param updateTime 插入时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取顶级目录ID 文件11、目录12、虚拟机13、数据库14、其它15
     *
     * @return topid - 顶级目录ID 文件11、目录12、虚拟机13、数据库14、其它15
     */
    public String getTopid() {
        return topid;
    }

    /**
     * 设置顶级目录ID 文件11、目录12、虚拟机13、数据库14、其它15
     *
     * @param topid 顶级目录ID 文件11、目录12、虚拟机13、数据库14、其它15
     */
    public void setTopid(String topid) {
        this.topid = topid;
    }

    /**
     * @return end_time
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return vm_path
     */
    public String getVmPath() {
        return vmPath;
    }

    /**
     * @param vmPath
     */
    public void setVmPath(String vmPath) {
        this.vmPath = vmPath;
    }
}