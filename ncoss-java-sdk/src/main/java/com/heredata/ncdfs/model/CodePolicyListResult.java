package com.heredata.ncdfs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CodePolicyListResult extends GenericResult {

    private static final long serialVersionUID = 1L;
    @JsonProperty("NcPolicy_List")
    private List<CodePolicy> list;
}
