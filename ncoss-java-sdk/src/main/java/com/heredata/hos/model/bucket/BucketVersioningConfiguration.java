package com.heredata.hos.model.bucket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>Title: BucketVersioningConfiguration</p>
 * <p>Description: 桶版本配置信息实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 17:10
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BucketVersioningConfiguration implements Serializable {

    private static final long serialVersionUID = -5015082031534990114L;

    /**
     * 旧的版本数据继续保留 ；
     * 上传对象时创建对象的版本号为null，上传同名的对象将覆盖原有同名的版本号为null的对象；
     * 可以指定版本号下载对象，不指定版本号默认下载最新对象；
     * 删除对象时可以指定版本号删除，不带版本号删除对象将产生一个版本号为null的删除标记，并删除版本号为null的对象；
     */
    public static final String SUSPENDED = "Suspended";

    /**
     * 上传对象时，系统为每一个对象创建一个唯一版本号，上传同名的对象将不再覆盖旧的对象，而是创建新的不同版本号的同名对象
     * 可以指定版本号下载对象，不指定版本号默认下载最新对象；
     * 删除对象时可以指定版本号删除，不带版本号删除对象仅产生一个带唯一版本号的删除标记，并不删除对象；
     * 列出桶内对象列表时默认列出最新对象列表，可以指定列出桶内所有版本对象列表；
     */
    public static final String ENABLED = "Enabled";

    /**
     * 桶版本信息
     */
    private String status;

    /**
     * 设置配置类信息
     * @param status
     * @return
     */
    public BucketVersioningConfiguration withStatus(String status) {
        setStatus(status);
        return this;
    }

}
