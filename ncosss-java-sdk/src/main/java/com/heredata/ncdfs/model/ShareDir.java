package com.heredata.ncdfs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShareDir {
    /**
     * Ncsf文件系统名
     */
    @JsonProperty("ncsfFileSysname")
    private String ncsfFileSysName;

    /**
     * Ncsf文件系统中的目录
     */
    @JsonProperty("ncsfShareDir")
    private String ncsfShareDir;

    /**
     * 挂载绑定的系统本地目录
     */
    @JsonProperty("localmountDir")
    private String localMountDir;

    /**
     * 共享目录中节点数
     */
    @JsonProperty("nodeNum")
    private Long nodeNum;

    /**
     * 共享目录下数据总量
     */
    @JsonProperty("usedSize")
    private Long usedSize;

}
