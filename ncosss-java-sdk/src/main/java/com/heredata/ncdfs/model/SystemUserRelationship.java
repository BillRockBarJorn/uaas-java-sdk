package com.heredata.ncdfs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SystemUserRelationship {

    /**
     * Ncdss集群用户
     */
    @JsonProperty("ncdssuser")
    private String ncdssUser;

    /**
     * 系统用户
     */
    @JsonProperty("localuser")
    private String localUser;

    /**
     * 主机ip
     */
    @JsonProperty("osip")
    private String osIP;


}
