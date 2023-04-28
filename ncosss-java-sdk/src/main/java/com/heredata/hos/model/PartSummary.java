package com.heredata.hos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * <p>Title: PartSummary</p>
 * <p>Description: 分片信息摘要 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 15:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartSummary {
    /**
     * 分片在文件中所在的索引位置，从1开始
     */
    private int partNumber;
    /**
     * 最新的修改时间
     */
    private Date lastModified;
    /**
     * 分片的标签值（MD5/哈希值）
     */
    private String eTag;
    /**
     * 分片大小  单位：Byte
     */
    private long size;
}
