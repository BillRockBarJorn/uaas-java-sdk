package com.heredata.ncdfs.model;

import lombok.AllArgsConstructor;

/**
 * 编码方式枚举
 * @author wuzz
 * @since 2022/9/30
 */
@AllArgsConstructor
public enum CodeTypeEnum {

    R1("R1"),
    RS("RS"),
    CRS("CRS"),
    EMBR("EMBR");

    private String codeType;
}
