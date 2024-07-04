package com.heredata.ncdfs.comm;

import com.heredata.ncdfs.ResponseHandler;
import com.heredata.ncdfs.auth.Credentials;
import com.heredata.ncdfs.auth.RequestSigner;
import com.heredata.ncdfs.internal.NCDFSConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;

/**
 * HTTP request context.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExecutionContext {

    /* Request signer */
    private RequestSigner signer;

    /* The request handlers that handle request content in as a pipeline. */
    private List<RequestHandler> requestHandlers = new LinkedList<>();

    /* The response handlers that handle response message in as a pipeline. */
    private List<ResponseHandler> responseHandlers = new LinkedList<>();

    /* The signer handlers that handle sign request in as a pipeline. */
    private List<RequestSigner> signerHandlers = new LinkedList<>();

    private String charset = NCDFSConstants.DEFAULT_CHARSET_NAME;

    /* Retry strategy when HTTP request fails. */
    private RetryStrategy retryStrategy;

    private Credentials credentials;

    public void addResponseHandler(ResponseHandler handler) {
        responseHandlers.add(handler);
    }

    public void insertResponseHandler(int position, ResponseHandler handler) {
        responseHandlers.add(position, handler);
    }

    public void removeResponseHandler(ResponseHandler handler) {
        responseHandlers.remove(handler);
    }

    public void addRequestHandler(RequestHandler handler) {
        requestHandlers.add(handler);
    }

    public void insertRequestHandler(int position, RequestHandler handler) {
        requestHandlers.add(position, handler);
    }

    public void removeRequestHandler(RequestHandler handler) {
        requestHandlers.remove(handler);
    }

    public void addSignerHandler(RequestSigner handler) {
        signerHandlers.add(handler);
    }

    public void insertSignerHandler(int position, RequestSigner handler) {
        signerHandlers.add(position, handler);
    }

    public void removeSignerHandler(RequestSigner handler) {
        signerHandlers.remove(handler);
    }
}
