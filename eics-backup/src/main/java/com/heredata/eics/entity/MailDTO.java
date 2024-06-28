package com.heredata.eics.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MailDTO extends NoticeDTO {

    private String[] ccRecipient;

    private String subject;
}
