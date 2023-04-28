package com.heredata.swift.model;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: DownloadFileResult</p>
 * <p>Description: 下载对象成功后返回的结果实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:39
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class DownloadFileResult extends GenericResult {

    /**
     * 对象元数据
     */
    private ObjectMetadata objectMetadata;
}
