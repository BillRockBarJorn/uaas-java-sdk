package com.heredata.ncdfs.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIncludeProperties({"ncsfUser", "fileSysSize", "poolId", "deletedFileKeepSec", "fileSysFileNumQuota"})
public class CreateFileSystemRequest extends GenericRequest {
    private static final long serialVersionUID = 1L;
    /**
     * 文件系统所属ncdss用户
     */
    @JsonProperty("ncsfUser")
    private String ncsfUser;

    /**
     * 文件系统总空间（单位M）
     */
    @JsonProperty("fileSysSize")
    private Long fileSysSize;

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
}
