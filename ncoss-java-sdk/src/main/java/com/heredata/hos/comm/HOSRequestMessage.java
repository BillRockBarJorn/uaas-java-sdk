package com.heredata.hos.comm;

import com.heredata.comm.RequestMessage;
import com.heredata.model.WebServiceRequest;
import lombok.Data;

/**
 * Represent HTTP requests sent to HOS.
 */
@Data
public class HOSRequestMessage extends RequestMessage {

    /* bucket name */
    private String bucket;

    /* object name */
    private String key;

    /* account */
    private String account;

    public HOSRequestMessage(String bucketName, String key) {
        this(null, bucketName, key);
    }

    public HOSRequestMessage(WebServiceRequest originalRequest, String bucketName, String key) {
        super((originalRequest == null) ? WebServiceRequest.NOOP : originalRequest);
    }
}
