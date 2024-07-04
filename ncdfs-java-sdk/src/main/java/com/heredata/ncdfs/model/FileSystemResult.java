package com.heredata.ncdfs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileSystemResult extends GenericResult {

    @JsonProperty("fileSysInfo")
    private FileSystem fileSystem;
    
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class FileSystemListResult extends GenericResult {

        @JsonProperty("fileSys_List")
        private List<FileSystem> fileSystemList;
    }

}
