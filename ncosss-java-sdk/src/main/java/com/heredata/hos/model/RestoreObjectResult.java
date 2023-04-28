package com.heredata.hos.model;

import com.heredata.model.GenericResult;
import lombok.Data;

/**
 * Successful response of restore object operation.
 */

/**
 * <p>Title: RestoreObjectResult</p>
 * <p>Description: 解冻对象成功后返回的响应实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 16:13
 */
@Data
public class RestoreObjectResult extends GenericResult {
    /**
     * 响应状态码   200：解冻成功  202：解冻请求已被接受执行
     */
    private int statusCode;

    public RestoreObjectResult(int statusCode) {
        super();
        this.statusCode = statusCode;
    }
}
