package com.heredata.swift.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <p>Title: SetBucketQuotaRequest</p>
 * <p>Description: 设置桶配额信息 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 17:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SetBucketQuotaRequest extends GenericRequest {

    /**
     * 字节数限制，单位为Byte
     */
    private Long quotaByte;

    /**
     * 桶内对象 个数限制，非负整数
     */
    private Integer objCount;

}
