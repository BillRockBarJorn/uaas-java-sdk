package com.heredata.hos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * <p>Title: HOSObjectSummary</p>
 * <p>Description:  对象摘要信息</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 11:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HOSObjectSummary {

    /**
     * 桶名称
     */
    private String bucketName;
    /**
     * 对象名称
     */
    private String key;
    /**
     * 对象标签
     */
    private String eTag;
    /**
     * 对象大小。单位：Byte
     */
    private Long size;
    /**
     * 对象最后的修改时间
     */
    private Date lastModified;
    /**
     * 对象的存储类型：{@link StorageClass}
     */
    private String storageClass;
    /**
     * 对象的拥有者{@link Owner}
     */
    private Owner owner;
}
