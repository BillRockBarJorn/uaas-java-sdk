package com.heredata.hos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>Title: HOSVersionSummary</p>
 * <p>Description: 版本对象信息摘要 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 11:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HOSVersionSummary implements Serializable {

    private static final long serialVersionUID = -1811213462343527452L;

    /**
     * 存储此版本的存储桶的名称
     */
    protected String bucketName;

    /**
     * 存储此版本的存储桶的对象
     */
    private String key;

    /**
     * 唯一标识对象此版本的版本ID
     */
    private String versionId;

    /**
     * 如果这是关联对象的最新版本，则为True
     */
    private boolean isLatest;

    /**
     * 该版本上次修改的日期
     */
    private Date lastModified;

    /**
     * 对象拥有着
     */
    private Owner owner;

    /**
     * 对象的标签值（MD5，哈希值）
     */
    private String eTag;
    /**
     * 对象的大小，单位：Byte
     */
    private long size;

    /**
     * 此版本对象的存储类型 {@link StorageClass}
     */
    private String storageClass;

    /**
     * 如果此对象表示删除标记，则为True
     */
    private boolean isDeleteMarker;

    /**
     * 如果这是关联对象的最新版本，则为True
     * @return
     */
    public boolean isLatest() {
        return this.isLatest;
    }

    /**
     * 如果此对象表示删除标记，则为True
     * @return
     */
    public boolean isDeleteMarker() {
        return isDeleteMarker;
    }
}
