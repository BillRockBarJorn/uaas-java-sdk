package com.heredata.swift.model;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: DeleteObjectsResult</p>
 * <p>Description: 删除对象成功后的响应结果实体 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:37
 */
@Data
@AllArgsConstructor
public class DeleteObjectsResult extends GenericResult {

    /**
     * 需要删除对象的名称列表
     */
    private final List<String> deletedObjects = new ArrayList<String>();

    public DeleteObjectsResult(List<String> deletedObjects) {
        if (deletedObjects != null && deletedObjects.size() > 0) {
            this.deletedObjects.addAll(deletedObjects);
        }
    }


    public void setDeletedObjects(List<String> deletedObjects) {
        this.deletedObjects.clear();
        this.deletedObjects.addAll(deletedObjects);
    }
}
