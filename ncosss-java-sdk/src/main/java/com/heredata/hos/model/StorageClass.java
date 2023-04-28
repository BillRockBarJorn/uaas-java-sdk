package com.heredata.hos.model;

/**
 * <p>Title: StorageClass</p>
 * <p>Description: 对象存储类型枚举</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 11:54
 */
public enum StorageClass {
    /**
     * 标准储存（热）。默认的存储类型
     */
    STANDARD("STANDARD "),

    /**
     * 低频访问存储 (温)
     */
    IA("IA"),

    /**
     * 归档存储 （冷）
     */
    ARCHIVE("ARCHIVE");


    private String storageClassString;

    private StorageClass(String storageClassString) {
        this.storageClassString = storageClassString;
    }

    @Override
    public String toString() {
        return this.storageClassString;
    }

    public static StorageClass parse(String storageClassString) {
        for (StorageClass st : StorageClass.values()) {
            if (st.toString().equals(storageClassString)) {
                return st;
            }
        }

        throw new IllegalArgumentException("Unable to parse " + storageClassString);
    }
}
