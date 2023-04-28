package com.heredata.swift.comm;

import com.heredata.comm.RequestMessage;
import com.heredata.model.WebServiceRequest;
import lombok.Data;

@Data
public class SWIFTRequestMessage extends RequestMessage {

    /* bucket name */
    private String bucket;

    /* object name */
    private String key;

    /* account */
    private String account;

    public SWIFTRequestMessage(String bucketName, String key) {
        this(null, bucketName, key);
    }

    public SWIFTRequestMessage(WebServiceRequest originalRequest, String bucketName, String key) {
        super((originalRequest == null) ? WebServiceRequest.NOOP : originalRequest);
        this.bucket = bucketName;
        this.key = key;
    }

    public SWIFTRequestMessage(WebServiceRequest originalRequest) {
        super((originalRequest == null) ? WebServiceRequest.NOOP : originalRequest);
    }

    /**
     * Indicate whether the request should be repeatedly sent.
     */
    @Override
    public boolean isRepeatable() {
        return this.getContent() == null || this.getContent().markSupported();
    }

    @Override
    public String toString() {
        return "Endpoint: " + this.getEndpoint().getHost() + ", ResourcePath: " + this.getResourcePath() + ", Headers:"
                + this.getHeaders();
    }
}
