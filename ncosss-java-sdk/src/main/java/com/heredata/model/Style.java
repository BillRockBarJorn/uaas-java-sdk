package com.heredata.model;

import lombok.Data;

import java.util.Date;

@Data
public class Style {
    private String styleName;
    private String style;
    private Date creationDate;
    private Date lastModifyTime;
}
