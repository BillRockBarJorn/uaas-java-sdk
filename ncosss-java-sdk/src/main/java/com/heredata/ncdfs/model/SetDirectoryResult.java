package com.heredata.ncdfs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SetDirectoryResult extends GenericResult {

    @JsonProperty("d_id")
    private Integer directoryId;

}
