package com.heredata.hos.model;

import java.util.ArrayList;
import java.util.List;

import static com.heredata.hos.utils.HOSUtils.validateObjectKey;


/**
 * Options for deleting multiple objects in a specified bucket.
 */

/**
 * <p>Title: 删除对象</p>
 * <p>Description:  删除指定对象请求实体</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/24 19:03
 */
public class DeleteObjectsRequest extends GenericRequest {

    /**
     * 删除对象的最大值
     */
    public static final int DELETE_OBJECTS_ONETIME_LIMIT = 1000;

    /**
     * 删除对象的名称List
     */
    private final List<String> keys = new ArrayList<String>();


    public DeleteObjectsRequest(String bucketName) {
        super(bucketName);
    }

    public List<String> getKeys() {
        return keys;
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
