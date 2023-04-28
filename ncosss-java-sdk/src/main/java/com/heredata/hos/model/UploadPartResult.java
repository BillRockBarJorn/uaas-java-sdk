package com.heredata.hos.model;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: UploadPartResult</p>
 * <p>Description: 分片上传成功后的响应实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 16:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadPartResult extends GenericResult {
    /**
     * 当前分片在文件的索引位置，从1开始
     */
    private int partNumber;
    /**
     * 分片大小  单位：Byte
     */
    private long partSize;
    /**
     * 分片标签值（MD5/哈希值）
     */
    private String eTag;
}
