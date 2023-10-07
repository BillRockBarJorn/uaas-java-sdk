package com.heredata.hos.model;

/**
 * <p>Title: AlgorithmEnum</p>
 * <p>Description: 算法枚举信息，用于客户端加密 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 17:21
 */
public enum AlgorithmEnum {
    AES256("AES256");

    private final String algorithm;

    public String getAlgorithm() {
        return algorithm;
    }

    private AlgorithmEnum(String algorithm) {
        this.algorithm = algorithm;
    }
}
