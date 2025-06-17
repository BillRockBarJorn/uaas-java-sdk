package com.heredata.uaas.openstack.identity.v3.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.heredata.uaas.model.ModelEntity;

/**
 * 永久S3令牌请求类
 * @author wuzz
 * @since 2023/8/24
 */
@JsonRootName("AccessKey")
public class KeystoneAccessKey implements ModelEntity {
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("project_id")
    private String projectId;
    @JsonProperty("description")
    private String description;

    public KeystoneAccessKey() {
    }

    public KeystoneAccessKey(String userName, String projectId, String description) {
        this.userName = userName;
        this.projectId = projectId;
        this.description = description;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
