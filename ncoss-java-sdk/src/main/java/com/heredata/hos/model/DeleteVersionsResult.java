package com.heredata.hos.model;

import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: DeleteVersionsResult</p>
 * <p>Description: 成功删除对象后返回的结果 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/24 19:29
 */
public class DeleteVersionsResult extends GenericResult {

    /**
     * 删除后返回的对象版本信息
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static public class DeletedVersion implements Serializable {

        private static final long serialVersionUID = 4306380535649706669L;
        /**
         * 对象名称
         */
        private String key;
        /**
         * 对象版本号
         */
        private String versionId;
        /**
         * 删除的对象是否为删除标记
         */
        private boolean deleteMarker;
        /**
         * 删除标记版本id号
         * 当桶开启版本控制时，这时做无版本删除，会返回当前值
         */
        private String deleteMarkerVersionId;


        /**
         * Returns whether the object deleted was a delete marker.
         */
        public boolean isDeleteMarker() {
            return deleteMarker;
        }

        public void setDeleteMarker(boolean deleteMarker) {
            this.deleteMarker = deleteMarker;
        }
    }

    private final List<DeletedVersion> deletedVersions = new ArrayList<DeletedVersion>();

    public DeleteVersionsResult(List<DeletedVersion> deletedVersions) {
        if (deletedVersions != null) {
            this.deletedVersions.addAll(deletedVersions);
        }
    }

    public List<DeletedVersion> getDeletedVersions() {
        return deletedVersions;
    }

}
