package com.heredata.eics.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AgentDTO {


    private String bucketName;

    private String ipAddress;

    private int port;

    private Path localPath;

    private String frequ;


}
