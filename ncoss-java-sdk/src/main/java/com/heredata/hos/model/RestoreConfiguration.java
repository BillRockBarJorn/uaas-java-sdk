package com.heredata.hos.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>Title: RestoreConfiguration</p>
 * <p>Description: 解冻对象配置 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 16:09
 */
@Data
@AllArgsConstructor
public class RestoreConfiguration {
    /**
     * 临时对象副本保存时间，单位：天，以对象的最后更新时间为准
     */
    private Integer days;
}
