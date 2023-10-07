package com.heredata.hos.model;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: PartListing</p>
 * <p>Description: 请求已上传的分片列表成功后返回的实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 15:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartListing extends GenericResult {
    /**
     * 桶名称
     */
    private String bucketName;
    /**
     * 对象名称
     */
    private String key;
    /**
     * 全局唯一上传id {@link InitiateMultipartUploadResult#getUploadId()}
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
     * 对象存储类型 {@link StorageClass}
     */
    private String storageClass;

    /**
     * 下一页列表是否有数据 ，用于做分页查询
     */
    private boolean isTruncated;
    /**
     * 下一页的起点，用于做分页查询
     */
    private Integer nextPartNumberMarker;

    /**
     * 对象的拥有者信息
     */
    private Owner owner;

    /**
     * 查询出的分片列表信息 {@link PartSummary}
     */
    private List<PartSummary> parts = new ArrayList<>();


    public boolean isTruncated() {
        return isTruncated;
    }

    /**
     * @Title: setParts
     * @Description: 设置分片列表  {@link PartSummary}
     * @params [parts]
     * @return void
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/25 15:52
     */
    public void setParts(List<PartSummary> parts) {
        this.parts.clear();
        if (parts != null && !parts.isEmpty()) {
            this.parts.addAll(parts);
        }
    }

    /**
     * @Title: addPart
     * @Description: 向分片列表中添加元素  {@link PartSummary}
     * @params [partSummary]
     * @return void
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/25 15:52
     */
    public void addPart(PartSummary partSummary) {
        this.parts.add(partSummary);
    }
}
