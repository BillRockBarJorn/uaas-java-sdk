package com.heredata.hos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: ListMultipartUploadsRequest</p>
 * <p>Description: 已初始化上传的信息列表请求对象 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 14:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListMultipartUploadsRequest extends GenericRequest {


    /**
     * 限制查询对象前缀
     */
    private String prefix;

    /**
     * 限制查询对象名称大于当前值
     */
    private String startAfter;

    /**
     * 限制每次查询数量的最大值
     */
    private Integer maxKeys;

    /**
     * @param bucketName
     *            桶名称
     */
    public ListMultipartUploadsRequest(String bucketName) {
        super(bucketName);
    }
}
