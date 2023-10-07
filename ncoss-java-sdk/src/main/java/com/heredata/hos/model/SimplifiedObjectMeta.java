package com.heredata.hos.model;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * <p>Title: SimplifiedObjectMeta</p>
 * <p>Description: 简化的一个对象元数据实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 17:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimplifiedObjectMeta extends GenericResult {
    /**
     * 对象标签值（MD5/哈希值）
     */
    private String eTag;
    /**
     * 对象大小   单位：Byte
     */
    private long size;
    /**
     * 对象最新的修改时间
     */
    private Date lastModified;

    /**
     * 对象的版本号
     */
    private String versionId;
}
