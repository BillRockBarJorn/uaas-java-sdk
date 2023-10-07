package com.heredata.ncdfs.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.heredata.ResponseMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A generic result that contains some basic response options, such as
 * requestId.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class GenericResult {
    @JsonProperty
    private String code;
    @JsonProperty
    private String status;
    private String error;
    private ResponseMessage response;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
