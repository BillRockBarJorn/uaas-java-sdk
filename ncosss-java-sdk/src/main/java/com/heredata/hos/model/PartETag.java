package com.heredata.hos.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>Title: PartETag</p>
 * <p>Description: 分片上传的标签信息 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 15:31
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"partNumber", "eTag"})
public class PartETag implements Serializable {
    private static final long serialVersionUID = 2471854027355307627L;

    /**
     * 当前分片所属文件的位置
     */
    private int partNumber;
    /**
     * 分片的MD5（哈希值）
     */
    private String eTag;
    /**
     * 分片大小 单位：Byte
     */
    private long partSize;

    /**
     * @param partNumber
     *            当前分片所属文件的位置
     * @param eTag
     *            分片的MD5（哈希值）
     */
    public PartETag(int partNumber, String eTag) {
        this.partNumber = partNumber;
        this.eTag = eTag;
    }

    /**
     * @param partNumber
     *            当前分片所属文件的位置
     * @param eTag
     *            分片的MD5（哈希值）
     * @param partSize
     *            分片大小 单位：Byte
     */
    public PartETag(int partNumber, String eTag, long partSize) {
        this.partNumber = partNumber;
        this.eTag = eTag;
        this.partSize = partSize;
    }
}
