package com.heredata.hos.model;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: MultipartUploadListing</p>
 * <p>Description: 查询初始化任务上传列表成功后返回的结果实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 15:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultipartUploadListing extends GenericResult {
    /**
     * 桶名称
     */
    private String bucketName;
    /**
     * 限制查询对象名称大于等于当前值
     */
    private String startAfter;
    /**
     * 限制查询对象名称的前缀
     */
    private String prefix;
    /**
     * 限制查询每次的数量
     */
    private int maxKeys;

    /**
     * 当前列表后面是否还有数据，用作分页
     */
    private boolean isTruncated;
    /**
     * 下一页查询的起点，用作分页查询
     */
    private String nextStartAfter;
    /**
     * 初始化任务列表信息  {@link MultipartUpload}
     */
    private List<MultipartUpload> multipartUploads = new ArrayList<MultipartUpload>();


    /**
     * @param bucketName
     *            桶名称
     */
    public MultipartUploadListing(String bucketName) {
        this.bucketName = bucketName;
    }

    public void setMultipartUploads(List<MultipartUpload> multipartUploads) {
        this.multipartUploads.clear();
        if (multipartUploads != null && !multipartUploads.isEmpty()) {
            this.multipartUploads.addAll(multipartUploads);
        }
    }

    public void addMultipartUpload(MultipartUpload multipartUpload) {
        this.multipartUploads.add(multipartUpload);
    }
}
