package com.heredata.hos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: ListPartsRequest</p>
 * <p>Description: 查询指定uploadId分片列表列表 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 14:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListPartsRequest extends GenericRequest {
    /**
     * 全局唯一上传ID {@link InitiateMultipartUploadResult#getUploadId()}
     */
    private String uploadId;

    /**
     * 限制每次查询最大值数量
     */
    private Integer maxKeys;

    /**
     * part 编号，约束返回的段对象编号大于此值
     */
    private Integer partNumberMarker;


    /**
     * @param bucketName
     *            桶名称
     * @param key
     *            对象名称
     * @param uploadId
     *            全局唯一上传id
     */
    public ListPartsRequest(String bucketName, String key, String uploadId) {
        super(bucketName, key);
        this.uploadId = uploadId;
    }
}
