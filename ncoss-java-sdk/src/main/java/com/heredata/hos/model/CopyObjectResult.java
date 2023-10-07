package com.heredata.hos.model;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * <p>Title: 复制对象后返回的结果信息</p>
 * <p>Description: 复制对象后返回的结果信息 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/24 19:00
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class CopyObjectResult extends GenericResult {
    /**
     * 新对象的标签值
     */
    private String etag;
    /**
     * 新对象最新的修改时间
     */
    private Date lastModified;

    /**
     *新复制对象的版本ID
     * 如果新对象桶开启的版本控制，会返回该值
     */
    private String versionId;
}
