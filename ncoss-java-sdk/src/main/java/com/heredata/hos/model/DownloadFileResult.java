package com.heredata.hos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: DownloadFileResult</p>
 * <p>Description: 下载对象后的结果实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 10:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadFileResult {
    /**
     * 对象元数据信息
     */
    private ObjectMetadata objectMetadata;
}
