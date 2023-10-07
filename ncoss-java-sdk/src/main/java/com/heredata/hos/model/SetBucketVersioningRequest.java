package com.heredata.hos.model;

import com.heredata.hos.model.bucket.BucketVersioningConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * <p>Title: SetBucketVersioningRequest</p>
 * <p>Description: 设置桶版本信息
 *                多版本功能可在用户意外覆盖或删除对象的情况下提供一种恢复手段。
 *                用户可以使用多版本功能来保存、检索和还原对象的各个版本，这样用户能够从意外操作或应用程序故障中轻松恢复数据。
 *                多版本功能还可用于数据保留和存档。
 *                本接口是用来设置桶的多版本状态，用来开启或暂停桶的多版本功能，
 *                默认情况下，桶没有开启多版本功能，当多版本状态开启后就无法关闭，只能暂停或恢复桶的多版本状态。</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 17:06
 */
@Data
@AllArgsConstructor
public class SetBucketVersioningRequest extends GenericRequest {

    /**
     * 桶版本配置类  {@link BucketVersioningConfiguration}
     */
    private BucketVersioningConfiguration versioningConfiguration;

    /**
     * @param bucketName
     *           桶名称
     * @param configuration
     *           桶版本配置类  {@link BucketVersioningConfiguration}
     */
    public SetBucketVersioningRequest(String bucketName, BucketVersioningConfiguration configuration) {
        super(bucketName);
        this.versioningConfiguration = configuration;
    }

    /**
     * 设置桶版本信息
     * @param versioningConfiguration  桶版本配置类  {@link BucketVersioningConfiguration}
     * @return
     */
    public SetBucketVersioningRequest withVersioningConfiguration(
            BucketVersioningConfiguration versioningConfiguration) {
        setVersioningConfiguration(versioningConfiguration);
        return this;
    }

}
