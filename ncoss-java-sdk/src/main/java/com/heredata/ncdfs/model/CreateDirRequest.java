package com.heredata.ncdfs.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO
 * @author wuzz
 * @since 2022/10/9
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIncludeProperties({"path", "createParents", "mode"})
public class CreateDirRequest extends GenericRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 要创建的目录全路径
     */
    @JsonProperty("path")
    private String path;

    /**
     * 是否自动创建上级目录
     * 0：不创建   1：相当于mkdir -p
     */
    @JsonProperty("createParents")
    private Integer createParents;

    /**
     *目录权限
     */
    @JsonProperty("mode")
    private Integer mode;


}
