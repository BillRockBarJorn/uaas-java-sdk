package com.heredata.hos.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>Title: RestoreObjectRequest</p>
 * <p>Description: 解冻对象请求实体。 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 16:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestoreObjectRequest extends GenericRequest {
    /**
     * 解冻对象配置类  {@link RestoreConfiguration}
     */
    private RestoreConfiguration restoreConfiguration;

    /**
     * @param bucketName
     *            当前分片所属文件的位置
     * @param key
     *            分片的MD5（哈希值）
     */
    public RestoreObjectRequest(String bucketName, String key) {
        super(bucketName, key);
    }

    /**
     * @param bucketName
     *            当前分片所属文件的位置
     * @param key
     *            分片的MD5（哈希值）
     * @param restoreConfiguration
     *            解冻对象配置类  {@link RestoreConfiguration}
     */
    public RestoreObjectRequest(String bucketName, String key, RestoreConfiguration restoreConfiguration) {
        super(bucketName, key);
        this.restoreConfiguration = restoreConfiguration;
    }

    public RestoreObjectRequest withRestoreConfiguration(RestoreConfiguration restoreConfiguration) {
        setRestoreConfiguration(restoreConfiguration);
        return this;
    }

}
