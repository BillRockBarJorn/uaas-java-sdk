package com.heredata.ncdfs.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

/**
 * 编码方式枚举
 * @author wuzz
 * @since 2022/9/30
 */
@AllArgsConstructor
public enum CodeTypeEnum {

    RAID1("RAID1"),
    RS("RS"),
    CRS("CRS"),
    EMBR("EMBR");

    private String codeType;

    public static CodeTypeEnum value(String codeType) {
        if ("r1".equals(codeType.toLowerCase())) {
            return CodeTypeEnum.RAID1;
        }
        return valueOf(codeType);
    }

}
