package com.heredata.ncdfs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CodePolicy {

    private static final long serialVersionUID = 1L;

    @JsonProperty
    private Integer policyId;

    /**
     * 编码方式：RAID1 RS CRS EMBR。
     */
    @JsonIgnore
    private CodeTypeEnum codeType;

    @JsonProperty("codeType")
    private String codeTypeStr;

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

    public void setCodeType(CodeTypeEnum codeType) {
        this.codeType = codeType;
        this.codeTypeStr = codeType.name();
    }

    public void setCodeTypeStr(String codeTypeStr) {
        this.codeTypeStr = codeTypeStr;
        this.codeType = CodeTypeEnum.value(codeTypeStr);
    }
}
