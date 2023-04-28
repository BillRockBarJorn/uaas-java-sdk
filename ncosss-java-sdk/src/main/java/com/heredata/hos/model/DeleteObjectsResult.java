package com.heredata.hos.model;

import com.heredata.model.GenericResult;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
* <p>Title: DeleteObjectsResult</p>
* <p>Description: 删除对象成功后的响应结果 </p>
* <p>Copyright: Copyright (c) 2022</p>
* <p>Company: Here-Data </p>
* @author wuzz
* @version 1.0.0
* @createtime 2022/10/24 19:09
*/
@Data
@NoArgsConstructor
public class DeleteObjectsResult extends GenericResult {

    /**
     * 删除对象的名称List
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
