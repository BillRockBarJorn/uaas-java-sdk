package com.heredata.eics.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MailEntity {

    /**
     * 桶名
     */
    private String bucketName;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件大小
     */
    private Long size;
    private String sizeStr;
    /**
     * 上传次数
     */
    private int count;
    /**
     * 上传结果，true代表成功，false代表失败
     */
    private boolean isSuccess;

    /**
     * 上传耗时,单位ms
     */
    private long consumerTime;


    public void setSize(Long size) {
        this.size = size;
        String[] arr = new String[]{"B", "KB", "MB", "GB", "TB"};
        int index = 0;
        while (size > 1024) {
            size /= 1024;
            index++;
        }
        this.sizeStr = size + arr[index];
    }

    @Override
    public String toString() {
        return "MailEntity{" +
                "bucketName='" + bucketName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", size=" + size +
                ", sizeStr='" + sizeStr + '\'' +
                ", count=" + count +
                ", isSuccess=" + isSuccess +
                ", consumerTime=" + consumerTime +
                '}';
    }
}
