package com.heredata.eics.entity;

import lombok.Data;

@Data
public class NoticeDTO {
    /*
    * 接受者
    * */
    private String[] recipient;
    /*
    * 正文
    * */
    private String content;

}
