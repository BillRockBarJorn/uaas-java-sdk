package com.heredata.swift.model;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: CopyObjectResult</p>
 * <p>Description: TODO </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CopyObjectResult extends GenericResult {
    /**
     * 新对象的标签值
     */
    private String etag;

    /**
     * 对象名称
     */
    private String key;
}
