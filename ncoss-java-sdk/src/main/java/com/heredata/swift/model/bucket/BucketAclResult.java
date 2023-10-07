package com.heredata.swift.model.bucket;

import com.heredata.model.GenericResult;
import com.heredata.swift.model.KeyValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: BucketAclResponser</p>
 * <p>Description: 设置桶ACL成功响应后返回的实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BucketAclResult extends GenericResult {

    /**
     * 任何用户都可以访问对象
     */
    private Boolean allUserReadObject;

    /**
     * 任何用户都可以获取bucket信息等操作，前提是该用户还具有对对象的读取访问权限
     * 前提allUserReadObject属性必须为true
     */
    private Boolean headOrGetBukcet;

    /**
     * 授予用户获取桶信息权限，同时授予获取桶内对象信息权限
     */
    private List<KeyValue> tokenRead = new ArrayList<>();

    /**
     * 授予用户对桶内对象的操作权限
     */
    private List<KeyValue> tokenWrite = new ArrayList<>();


    public void addTokenReadKeyValue(KeyValue keyValue) {
        tokenRead.add(keyValue);
    }

    public void addTokenWriteKeyValue(KeyValue keyValue) {
        tokenWrite.add(keyValue);
    }

    public void setHeadOrGetBukcet(Boolean headOrGetBukcet) {
        if (!this.getAllUserReadObject() && headOrGetBukcet) {
            throw new IllegalArgumentException("the allUserReadObject property is require true !!");
        }
        this.headOrGetBukcet = headOrGetBukcet;
    }
}
