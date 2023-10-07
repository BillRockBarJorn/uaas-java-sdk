package com.heredata.hos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * <p>Title: MultipartUpload</p>
 * <p>Description: 多文件上传实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 15:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultipartUpload {
    /**
     * 对象名称
     */
    private String key;

    /**
     * 全局唯一上传ID
     */
    private String uploadId;

    /**
     * 对象存储类型  {@link StorageClass}
     */
    private String storageClass;

    /**
     * 对象拥有者
     */
    private Owner owner;

    /**
     * 初始化上传时间
     */
    private Date initiated;
}
