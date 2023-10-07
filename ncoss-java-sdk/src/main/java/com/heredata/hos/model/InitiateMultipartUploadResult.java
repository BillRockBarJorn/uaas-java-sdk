package com.heredata.hos.model;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <p>Title: InitiateMultipartUploadResult</p>
 * <p>Description: 初始化上传请求成功后的结果实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 14:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InitiateMultipartUploadResult extends GenericResult {
    /**
     * 桶名称
     */
    private String bucketName;
    /**
     * 对象名称
     */
    private String key;
    /**
     * 对象唯一上传ID
     */
    private String uploadId;
}
