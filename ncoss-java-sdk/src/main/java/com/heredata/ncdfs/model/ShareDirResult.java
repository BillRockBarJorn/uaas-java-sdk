package com.heredata.ncdfs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareDirResult extends GenericResult {

    @JsonProperty(value = "shareDir_info",defaultValue = "")
    private ShareDir shareDir;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ShareDirListResult extends GenericResult {
        @JsonProperty("shareDir_list")
        private List<ShareDir> fuseShareDir;
    }

}
