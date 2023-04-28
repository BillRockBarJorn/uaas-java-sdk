package com.heredata.model;

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

    private String requestId;
    private Long clientCRC;
    private Long serverCRC;
    private ResponseMessage response;
}
