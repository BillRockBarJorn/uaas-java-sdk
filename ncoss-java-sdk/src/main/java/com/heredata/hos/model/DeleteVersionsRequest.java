package com.heredata.hos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides options for deleting multiple objects in a specified bucket. Once
 * deleted, the object(s) can only be restored if versioning was enabled when
 * the object(s) was deleted.
 *
 */

/**
 * <p>Title: DeleteVersionsRequest</p>
 * <p>Description: 批量删除多个版本中的多个对象 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/24 19:14
 */
public class DeleteVersionsRequest extends GenericRequest {

    /**
     * 设置版本号和对象
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KeyVersion implements Serializable {

        private static final long serialVersionUID = 6665103581584979327L;

        private String key;
        private String version;
    }

    /**
     * 需要删除版本对象的列表
     */
    private final List<KeyVersion> keys = new ArrayList<KeyVersion>();

    public DeleteVersionsRequest(String bucketName) {
        super(bucketName);
    }


    @Override
    public DeleteVersionsRequest withBucketName(String bucketName) {
        setBucketName(bucketName);
        return this;
    }

    public void setKeys(List<KeyVersion> keys) {
        this.keys.clear();
        this.keys.addAll(keys);
    }

    public DeleteVersionsRequest withKeys(List<KeyVersion> keys) {
        setKeys(keys);
        return this;
    }

    public List<KeyVersion> getKeys() {
        return keys;
    }

}
