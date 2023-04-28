package com.heredata.hos.model;

/**
 * <p>Title: DeleteVersionRequest</p>
 * <p>Description: 删除指定版本对象 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/24 19:12
 */
public class DeleteVersionRequest extends GenericRequest {

    /**
     * 对象版本号
     */
    private String versionId;

    public DeleteVersionRequest(String bucketName, String key, String versionId) {
        super(bucketName, key);
        this.versionId = versionId;
    }

}
