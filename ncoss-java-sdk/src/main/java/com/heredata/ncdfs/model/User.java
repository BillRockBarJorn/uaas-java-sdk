package com.heredata.ncdfs.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class User {

    /**
     * 用户号
     */
    @JsonProperty("useruid")
    private Integer userId;

    /**
     * 新建用户名
     */
    @JsonProperty("username")
    private String userName;

    /**
     * 用户失效日期
     */
    @JsonProperty("expirydate")
    @JsonFormat(pattern = "yyyyMMddHHmmss")
    private Date expiryDate;

    /**
     * 用户主目录
     */
    @JsonProperty("homepath")
    private String homePath;

    /**
     * 用户所属用户组
     */
    @JsonProperty("usergname")
    private String userGroup;


    /**
     * 用户过期后多少天关闭账户，默认-1账户一直可用
     */
    @JsonProperty("inactiveday")
    private Integer inactiveDay;

}
