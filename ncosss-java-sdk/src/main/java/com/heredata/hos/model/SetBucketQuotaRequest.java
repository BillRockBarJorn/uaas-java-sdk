package com.heredata.hos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: SetBucketQuotaRequest</p>
 * <p>Description: 设置桶配额 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 16:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetBucketQuotaRequest extends GenericRequest {

    /**
     * 字节数限制，单位为Byte
     */
    private Long storageQuota;

    /**
     * 桶内对象 个数限制，非负整数
     */
    private Integer StorageMaxCount;
}
