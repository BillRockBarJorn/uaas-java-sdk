package com.heredata.uaas.openstack.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.Date;

/**
 * 永久S3令牌实体类
 * @author wuzz
 * @since 2023/8/24
 */
@JsonRootName("AccessKey")
public class AccessKey {
    @JsonProperty("access_key")
    private String accessKey;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("create_time")
    private Date createTime;
    @JsonProperty("secret_key")
    private String secretKey;
    @JsonProperty("status")
    private String status;
    @JsonProperty("description")
    private String description;

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
