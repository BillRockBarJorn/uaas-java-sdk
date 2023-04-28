package com.heredata.ncdfs.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
// 由于继承影响了转换JSON的规则，所以采用这个注解，将指定属性映射成JSON字符串
@JsonIncludeProperties({"username", "expirydate", "homepath", "useruid", "usergname", "inactiveday", "usernewname"})
public class CreateUserRequest {

    private static final long serialVersionUID = 1L;

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
     * 用户号
     */
    @JsonProperty("useruid")
    private Integer userId;

    /**
     * 用户过期后多少天关闭账户，默认-1账户一直可用
     */
    @JsonProperty("inactiveday")
    private Integer inactiveDay;

    /**
     * 新的用户名。只要修改用户名时才会用到该属性
     */
    @JsonProperty("usernewname")
    private String newUserName;
}
