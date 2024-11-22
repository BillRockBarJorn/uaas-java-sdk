package com.heredata.eics.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
        BigDecimal bigDecimal = new BigDecimal(size);
        String[] arr = new String[]{"B", "KB", "MB", "GB", "TB"};
        int index = 0;
        while (bigDecimal.longValue() > 1024) {
            bigDecimal = bigDecimal.divide(new BigDecimal(1024));
            index++;
        }
        this.sizeStr = bigDecimal.setScale(4, RoundingMode.HALF_UP).toString() + arr[index];
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

    public static void main(String[] args) {

        MailEntity mailEntity = new MailEntity();
        mailEntity.setSize(33996852348L);
        System.out.println(mailEntity.getSizeStr());

    }

}
