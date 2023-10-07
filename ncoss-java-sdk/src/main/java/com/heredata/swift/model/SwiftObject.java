package com.heredata.swift.model;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * <p>Title: HOSObject</p>
 * <p>Description: 对象实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 11:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SwiftObject extends GenericResult implements Closeable {
    /**
     * 桶名称
     */
    private String bucketName;
    /**
     * 对象名称
     */
    private String key;
    /**
     * 对象元数据 {@link ObjectMetadata}
     */
    private ObjectMetadata metadata = new ObjectMetadata();

    /**
     * 对象流文件
     */
    private InputStream objectContent;
    /**
     * 对象标签
     */
    private String eTag;
    /**
     * 对象最后更新时间
     */
    private Date lastModified;
    /**
     * 对象大小，单位：Byte
     */
    private Long size;
    /**
     * 对象的mime类型
     */
    private String mimeType;

    @Override
    public void close() throws IOException {
        if (objectContent != null) {
            objectContent.close();
        }
    }
}
