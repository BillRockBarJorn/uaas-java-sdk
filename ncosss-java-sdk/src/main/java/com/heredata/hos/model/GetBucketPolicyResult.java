package com.heredata.hos.model;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <p>Title: GetBucketPolicyResult</p>
 * <p>Description: 获取桶策略结果实体，桶的策略字符串要符合策略语法。详情请参考
 *      《HOS-JAVA-SDK使用手册》中3.4.8.1章节桶策略语法</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 10:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GetBucketPolicyResult extends GenericResult {
    private String policyText;
}
