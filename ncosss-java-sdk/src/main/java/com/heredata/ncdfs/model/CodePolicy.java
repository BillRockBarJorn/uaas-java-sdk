package com.heredata.ncdfs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CodePolicy {

    private static final long serialVersionUID = 1L;

    @JsonProperty
    private Integer policyId;

    @JsonProperty("codeType")
    private CodeTypeEnum codeType;

    /**
     * 编码参数k
     */
    @JsonProperty("code_k")
    private Integer codeK;
    /**
     * 编码参数m
     */
    @JsonProperty("code_m")
    private Integer codeM;
    /**
     * 编码参数W
     */
    @JsonProperty("code_w")
    private Integer codeW;
    /**
     * 编码参数D
     */
    @JsonProperty("code_d")
    private Integer codeD;
    /**
     * 数据更新策略WL。
     */
    @JsonProperty
    private String updateType;
    /**
     * segment大小，默认为2M
     */
    @JsonProperty
    private Integer segmentSize;
}
