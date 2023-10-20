package com.heredata.swift.operation;

import com.heredata.ResponseMessage;
import com.heredata.signer.RequestSigner;
import com.heredata.comm.*;
import com.heredata.exception.ClientException;
import com.heredata.exception.ExceptionFactory;
import com.heredata.exception.ResponseParseException;
import com.heredata.exception.ServiceException;
import com.heredata.handler.*;
import com.heredata.model.WebServiceRequest;
import com.heredata.parser.ResponseParser;
import com.heredata.request.NoRetryStrategy;
import com.heredata.auth.CredentialsProvider;
import com.heredata.swift.handler.SwiftErrorResponseHandler;
import com.heredata.swift.parser.ResponseParsers.EmptyResponseParser;
import com.heredata.swift.parser.ResponseParsers.RequestIdResponseParser;
import com.heredata.swift.utils.SwiftUtils;
import lombok.Data;

import java.net.URI;
import java.util.List;

import static com.heredata.comm.HttpConstants.DEFAULT_CHARSET_NAME;
import static com.heredata.swift.utils.SwiftUtils.ensureEndpointValid;
import static com.heredata.utils.LogUtils.logException;
import static com.heredata.utils.ResourceUtils.safeCloseResponse;

/**
 * <p>Title: SwiftOperation</p>
 * <p>Description: SWIFT服务操作类基类 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 18:07
 */
@Data
public class SwiftOperation {
    protected volatile URI endpoint;
    protected CredentialsProvider credsProvider;
    protected ServiceClient innerClient;

    protected static SwiftErrorResponseHandler errorResponseHandler = new SwiftErrorResponseHandler();
    protected static EmptyResponseParser emptyResponseParser = new EmptyResponseParser();
    protected static RequestIdResponseParser requestIdResponseParser = new RequestIdResponseParser();
    protected static RetryStrategy noRetryStrategy = new NoRetryStrategy();

    protected SwiftOperation(ServiceClient innerClient, CredentialsProvider credsProvider) {
        this.innerClient = innerClient;
        this.credsProvider = credsProvider;
    }

    public URI getEndpoint(WebServiceRequest request) {
        String reqEndpoint = request.getEndpoint();
        if (reqEndpoint == null) {
            return getEndpoint();
        }
        String defaultProto = this.innerClient.getClientConfiguration().getProtocol().toString();
        URI ret = SwiftUtils.toEndpointURI(reqEndpoint, defaultProto);
        ensureEndpointValid(ret.getHost());
        return ret;
    }

    protected ResponseMessage send(RequestMessage request, ExecutionContext context)
            throws ServiceException, ClientException {
        return send(request, context, false);
    }

    protected ResponseMessage send(RequestMessage request, ExecutionContext context, boolean keepResponseOpen)
            throws ServiceException, ClientException {
        ResponseMessage response = null;
        try {
            response = innerClient.sendRequest(request, context);
            return response;
        } catch (ServiceException e) {
            throw e;
        } finally {
            if (response != null && !keepResponseOpen) {
                safeCloseResponse(response);
            }
        }
    }

    protected <T> T doOperation(RequestMessage request, ResponseParser<T> parser, String bucketName, String key)
            throws ServiceException, ClientException {
        return doOperation(request, parser, bucketName, key, false);
    }

    protected <T> T doOperation(RequestMessage request, ResponseParser<T> parser, String bucketName, String key,
                                boolean keepResponseOpen) throws ServiceException, ClientException {
        return doOperation(request, parser, bucketName, key, keepResponseOpen, null, null);
    }

    protected <T> T doOperation(RequestMessage request, ResponseParser<T> parser, String bucketName, String key,
                                boolean keepResponseOpen, List<RequestHandler> requestHandlers, List<ResponseHandler> reponseHandlers)
            throws ServiceException, ClientException {

        final WebServiceRequest originalRequest = request.getOriginalRequest();
        request.getHeaders().putAll(innerClient.getClientConfiguration().getDefaultHeaders());
        request.getHeaders().putAll(originalRequest.getHeaders());
        request.getParameters().putAll(originalRequest.getParameters());

        /**
         * calculate signature information and so on
         */
        ExecutionContext context = createDefaultContext(request.getMethod(), bucketName, key, originalRequest);

        context.addRequestHandler(new RequestProgressHanlder());
        if (requestHandlers != null) {
            for (RequestHandler handler : requestHandlers) {
                context.addRequestHandler(handler);
            }
        }
        if (innerClient.getClientConfiguration().isCrcCheckEnabled()) {
            context.addRequestHandler(new RequestChecksumHanlder());
        }

        context.addResponseHandler(new ResponseProgressHandler(originalRequest));
        if (reponseHandlers != null) {
            for (ResponseHandler handler : reponseHandlers) {
                context.addResponseHandler(handler);
            }
        }
        if (innerClient.getClientConfiguration().isCrcCheckEnabled()) {
            context.addResponseHandler(new ResponseChecksumHandler());
        }

        List<RequestSigner> signerHandlers = this.innerClient.getClientConfiguration().getSignerHandlers();
        if (signerHandlers != null) {
            for (RequestSigner signer : signerHandlers) {
                context.addSignerHandler(signer);
            }
        }

        ResponseMessage response = send(request, context, keepResponseOpen);

        try {
            return parser.parse(response);
        } catch (ResponseParseException rpe) {
            ServiceException oe = ExceptionFactory.createServiceException(response.getRequestId(), rpe.getMessage(),
                    rpe);
            logException("Unable to parse response error: ", rpe);
            throw oe;
        }
    }

    protected ExecutionContext createDefaultContext(HttpMethod method, String bucketName, String key, WebServiceRequest originalRequest) {
        ExecutionContext context = new ExecutionContext();
        context.setCharset(DEFAULT_CHARSET_NAME);
        context.addResponseHandler(errorResponseHandler);
        if (method == HttpMethod.POST && !isRetryablePostRequest(originalRequest)) {
            context.setRetryStrategy(noRetryStrategy);
        }
        if (innerClient.getClientConfiguration().getRetryStrategy() != null) {
            context.setRetryStrategy(innerClient.getClientConfiguration().getRetryStrategy());
        }
        return context;
    }

    protected ExecutionContext createDefaultContext(HttpMethod method, String bucketName, String key) {
        return this.createDefaultContext(method, bucketName, key, null);
    }

    protected ExecutionContext createDefaultContext(HttpMethod method, String bucketName) {
        return this.createDefaultContext(method, bucketName, null, null);
    }

    protected ExecutionContext createDefaultContext(HttpMethod method) {
        return this.createDefaultContext(method, null, null, null);
    }

    protected boolean isRetryablePostRequest(WebServiceRequest request) {
        return false;
    }
}
