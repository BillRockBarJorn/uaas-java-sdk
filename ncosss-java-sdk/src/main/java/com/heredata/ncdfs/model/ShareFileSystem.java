package com.heredata.ncdfs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.heredata.model.GenericResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareFileSystem {

    /**
     * Ncsf文件系统名
     */
    @JsonProperty("ncsfFileSystem")
    private String ncsfFileSystem;

    /**
     * 文件系统Id
     */
    @JsonProperty("fileSysId")
    private Integer fileSysId;

    /**
     * 挂载绑定的系统本地目录
     */
    @JsonProperty("localmountDir")
    private String localmountDir;

    /**
     * 文件系统总空间
     */
    @JsonProperty("totalSize")
    private Integer totalSize;

    /**
     * 已经使用空间
     */
    @JsonProperty("usedSize")
    private Integer usedSize;

    /**
     * 文件系统内节点数
     */
    @JsonProperty("nodeNum")
    private Integer nodeNum;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class ShareFileSystemList extends GenericResult {

        @JsonProperty("ClientFuse_list")
        private List<ShareFileSystem> shareFileSystemList;

    }
}
