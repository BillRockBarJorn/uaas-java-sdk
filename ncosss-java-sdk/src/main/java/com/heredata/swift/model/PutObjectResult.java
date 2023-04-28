package com.heredata.swift.model;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: PutObjectResult</p>
 * <p>Description: 上传对象成功响应后返回的实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 16:06
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PutObjectResult extends GenericResult {

    /**
     * 对象标签值（MD5/哈希值）
     */
    private String eTag;
}
