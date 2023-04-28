package com.heredata.swift.model;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
@ToString
public class AccountInfo extends GenericResult {

    //  桶数量
    private Integer bucketCount;

    // 账户中对象数量
    private Integer objCount;

    // 该账户下用了多少空间
    private Long bytesCount;
}
