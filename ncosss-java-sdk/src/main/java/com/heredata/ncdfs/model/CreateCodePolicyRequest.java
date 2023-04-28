package com.heredata.ncdfs.model;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// 由于继承影响了转换JSON的规则，所以采用这个注解，将指定属性映射成JSON字符串
@JsonIncludeProperties({"codeType", "code_k", "code_m", "code_w", "code_d", "updateType", "segmentSize"})
public class CreateCodePolicyRequest extends GenericRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 编码方式：RAID1 RS CRS EMBR。
     */
    @JsonProperty
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
