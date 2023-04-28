package com.heredata.hos.model.bucket;

import com.heredata.hos.model.Owner;
import com.heredata.hos.model.StorageClass;
import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * <p>Title: Bucket</p>
 * <p>Description: 查询桶信息成功响应的实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 17:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bucket extends GenericResult {

    /**
     * 桶名称
     */
    private String bucketName;

    /**
     * 桶拥有者
     */
    private Owner owner;

    /**
     * 桶创建日期
     */
    private Date creationDate;

    /**
     * 桶内对象的存储类型  {@link StorageClass}
     */
    private StorageClass storageClass = StorageClass.STANDARD;

    /**
     * 桶内已使用空间  单位：Byte
     */
    private Long bytesUsed;

    /**
     * 桶内对象数量
     */
    private Integer objectCount;

    /**
     * @param bucketName 桶名称
     */
    public Bucket(String bucketName) {
        this.bucketName = bucketName;
    }
}
