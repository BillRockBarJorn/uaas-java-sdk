package com.heredata.swift.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.heredata.swift.utils.SwiftUtils.validateObjectKey;

/**
 * <p>Title: DeleteObjectsRequest</p>
 * <p>Description: 删除对象请求实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:36
 */
@Data
public class DeleteObjectsRequest extends GenericRequest {

    /**
     * 每次删除逇最大值
     */
    public static final int DELETE_OBJECTS_ONETIME_LIMIT = 1000;

    /**
     * 需要删除对象的名称列表
     */
    private final List<String> keys = new ArrayList<String>();

    public DeleteObjectsRequest(String bucketName) {
        super(bucketName);
    }

    public void setKeys(List<String> keys) {
        if (keys == null || keys.size() == 0) {
            throw new IllegalArgumentException("Keys to delete must be specified");
        }

        if (keys.size() > DELETE_OBJECTS_ONETIME_LIMIT) {
            throw new IllegalArgumentException(
                    "The count of keys to delete exceed max limit " + DELETE_OBJECTS_ONETIME_LIMIT);
        }

        for (String key : keys) {
            if (key == null || key.equals("") || !validateObjectKey(key)) {
                throw new IllegalArgumentException("Illegal object key " + key);
            }
        }

        this.keys.clear();
        this.keys.addAll(keys);
    }

    public DeleteObjectsRequest withKeys(List<String> keys) {
        setKeys(keys);
        return this;
    }
}
