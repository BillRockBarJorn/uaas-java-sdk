package com.heredata.ncdfs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileSystem {
    /**
     * 文件系统Id
     */
    @JsonProperty("fileSysId")
    private String fileSysId;
    /**
     * 文件系统Id
     */
    @JsonProperty("fileSysName")
    private String fileSystemName;
    /**
     * 文件系统所属ncdss用户
     */
    @JsonProperty("ncsfUser")
    private String ncsfUser;

    /**
     * 为文件系统设定使用的存储池ID
     */
    @JsonProperty("poolId")
    private Integer poolId;

    /**
     * 删除的文件在系统中保留时间,在此时间内删除的文件可恢复，单位为s
     */
    @JsonProperty("deletedFileKeepSec")
    private Integer deletedFileKeepSec;

    /**
     * 文件系统文件数量配额
     */
    @JsonProperty("fileSysFileNumQuota")
    private Integer fileSysFileNumQuota;

    /**
     * 集群名
     */
    @JsonProperty("ncClusterName")
    private String clusterName;


    /**
     * 文件系统总空间
     */
    @JsonProperty("fileSysTotalSize")
    private Long fileSysTotalSize;

    /**
     * 文件系统已使用空间
     */
    @JsonIgnore
    @JsonProperty("fileSysUseSize")
    private Long fileSysUseSize;

    /**
     * 文件系统中目录文件总数
     */
    @JsonProperty("nodeNum")
    private Long nodeNum;

}
