package com.heredata.ncdfs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DirectoryResult extends GenericResult {

    @JsonProperty("Inode_Object")
    private Directory directory;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class DirectoryListResult extends GenericResult {

        @JsonProperty("Inode_Objects")
        private List<Directory> directoryList;

    }

}
