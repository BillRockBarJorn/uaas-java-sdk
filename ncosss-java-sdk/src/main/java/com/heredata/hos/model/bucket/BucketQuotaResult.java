package com.heredata.hos.model.bucket;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <p>Title: BucketQuotaResult</p>
 * <p>Description: 查询桶配额成功响应的实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 17:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BucketQuotaResult extends GenericResult {

    /**
     * 字节数限制，单位为Byte
     */
    private Long storageQuota;

    /**
     * 桶内对象 个数限制，非负整数
     */
    private Integer StorageMaxCount;
}
