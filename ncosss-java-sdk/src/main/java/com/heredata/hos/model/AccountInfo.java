package com.heredata.hos.model;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: AccountInfo</p>
 * <p>Description: 获取账户详情信息成功响应后的实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 17:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfo extends GenericResult {

    /**
     * 桶数量
     */
    private Integer bucketCount;

    /**
     * 对象数量
     */
    private Integer objCount;

    // This account used space 该账户下用了多少空间
    /**
     * 已使用空间  单位：Byte
     */
    private Long bytesCount;

    /**
     * 账户配额值，单位：Byte，0值代表不设置上限。
     */
    private Long storageQuota;

    /**
     * 桶内对象 个数限制，非负整数
     */
    private Long storageMaxCount;
}
