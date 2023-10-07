package com.heredata.hos.model;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * <p>Title: 完成分片上传结果</p>
 * <p>Description: 当所有分片上传完成时传入此实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/24 18:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CompleteMultipartUploadResult extends GenericResult {

    /**
     * 桶名称
     */
    private String bucketName;

    /**
     * 对象名称
     */
    private String key;

    /**
     * 对象标签，MD5值
     */
    private String eTag;

    /**
     * 版本号，当开启版本时会返回此信息
     */
    private String versionId;
}
