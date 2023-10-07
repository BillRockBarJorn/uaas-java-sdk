package com.heredata.ncdfs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.heredata.model.WebServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericRequest extends WebServiceRequest {

    /**
     * 集群名，格式NCDSS_[A-Z]。如NCDSS_A
     */
    @JsonIgnore
    private String clusterName;
}
