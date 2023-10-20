package com.heredata.s3.comm;

import com.heredata.comm.RequestMessage;
import com.heredata.model.WebServiceRequest;
import lombok.Data;

/**
 * Represent HTTP requests sent to HOS.
 */
@Data
public class S3RequestMessage extends RequestMessage {

    /* bucket name */
    private String bucket;

    /* object name */
    private String key;

    /* account */
    private String account;

    public S3RequestMessage(String bucketName, String key) {
        this(null, bucketName, key);
    }

    public S3RequestMessage(WebServiceRequest originalRequest, String bucketName, String key) {
        super((originalRequest == null) ? WebServiceRequest.NOOP : originalRequest);
    }
}
