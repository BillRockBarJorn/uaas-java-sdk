package com.heredata.hos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: SetAccountQuotaRequest</p>
 * <p>Description: 设置账户配置请求实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 16:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetAccountQuotaRequest extends GenericRequest {
    /**
     * 账户配额值必须为非负整数。
     * 默认配额为0，表示没有配额限制。
     * 配额设置后，如果想取消配额限制，可以把配额设置为0
     * 单位：Byte
     */
    private Long accountQuota;

    private Long storageMaxCount;



}
