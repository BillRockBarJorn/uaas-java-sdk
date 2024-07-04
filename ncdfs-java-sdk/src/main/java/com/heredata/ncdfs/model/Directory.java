package com.heredata.ncdfs.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Directory {

    /**
     * 节点ID
     */
    @JsonProperty("d_id")
    private Long directoryId;

    /**
     * 节点名
     */
    @JsonProperty("d_name")
    private String dName;

    /**
     *节点类型
     */
    @JsonProperty("d_type")
    private String dType;

    /**
     * 设备号
     */
    @JsonProperty("st_dev")
    private Integer stDev;

    /**
     * 用户号
     */
    @JsonProperty("st_uid")
    private Integer stUid;

    /**
     * 用户组号
     */
    @JsonProperty("st_gid")
    private Integer stGid;

    /**
     *
     */
    @JsonProperty("st_perm")
    private Integer stPerm;

    /**
     * 连接数
     */
    @JsonProperty("st_link")
    private Integer stLink;

    /**
     * 大小
     */
    @JsonProperty("st_size")
    private Long stSize;

    /**
     * 块数
     */
    @JsonProperty("st_count")
    private Long stCount;

    /**
     * 行数
     */
    @JsonProperty("st_line")
    private Long stLine;

    /**
     * 创建时间
     */
    @JsonProperty("ctime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS")
    private Date cTime;

    /**
     * 最后访问时间
     */
    @JsonProperty("atime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS")
    private Date aTime;

    /**
     * 最后修改时间
     */
    @JsonProperty("mtime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:SS")
    private Date mTime;

    /**
     * 所属用户名
     */
    @JsonProperty("uname")
    private String uName;

    /**
     * 所属用户组名
     */
    @JsonProperty("gname")
    private String gName;
}
