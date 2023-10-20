package com.heredata.hos.operation;

import com.heredata.ClientConfiguration;
import com.heredata.ResponseMessage;
import com.heredata.signer.RequestSigner;
import com.heredata.comm.*;
import com.heredata.exception.ClientException;
import com.heredata.exception.ExceptionFactory;
import com.heredata.exception.ResponseParseException;
import com.heredata.exception.ServiceException;
import com.heredata.handler.*;
import com.heredata.auth.Credentials;
import com.heredata.auth.CredentialsProvider;
import com.heredata.hos.handler.HOSErrorResponseHandler;
import com.heredata.hos.parser.ResponseParsers.EmptyResponseParser;
import com.heredata.hos.parser.ResponseParsers.RequestIdResponseParser;
import com.heredata.hos.signer.HOSSignerParams;
import com.heredata.hos.signer.HOSUaasSigner;
import com.heredata.hos.utils.HOSUtils;
import com.heredata.model.WebServiceRequest;
import com.heredata.parser.ResponseParser;
import com.heredata.request.NoRetryStrategy;
import lombok.Data;

import java.net.URI;
import java.util.List;

import static com.heredata.comm.HttpConstants.DEFAULT_CHARSET_NAME;
import static com.heredata.hos.utils.HOSUtils.ensureEndpointValid;
import static com.heredata.utils.CodingUtils.assertParameterNotNull;
import static com.heredata.utils.LogUtils.logException;
import static com.heredata.utils.ResourceUtils.safeCloseResponse;
import static com.heredata.utils.ResourceUtils.urlEncodeKey;


/**
 * <p>Title: HOSOperation</p>
 * <p>Description: hos操作基类 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:12
 */
@Data
public class HOSOperation {
    protected volatile URI endpoint;
    protected CredentialsProvider credsProvider;
    protected ServiceClient innerClient;

    protected static HOSErrorResponseHandler errorResponseHandler = new HOSErrorResponseHandler();
    protected static EmptyResponseParser emptyResponseParser = new EmptyResponseParser();
    protected static RequestIdResponseParser requestIdResponseParser = new RequestIdResponseParser();
    protected static RetryStrategy noRetryStrategy = new NoRetryStrategy();

    protected HOSOperation(ServiceClient innerClient, CredentialsProvider credsProvider) {
        this.innerClient = innerClient;
        this.credsProvider = credsProvider;
    }

    public URI getEndpoint(WebServiceRequest request) {
        String reqEndpoint = request.getEndpoint();
        if (reqEndpoint == null) {
            return getEndpoint();
        }
        String defaultProto = this.innerClient.getClientConfiguration().getProtocol().toString();
        URI ret = HOSUtils.toEndpointURI(reqEndpoint, defaultProto);
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

        // 获取客户端连接配置，将默认的请求的头添加到request对象中
        request.getHeaders().putAll(innerClient.getClientConfiguration().getDefaultHeaders());
        // 获取原始请求对象
        final WebServiceRequest originalRequest = request.getOriginalRequest();
        // 将原始请求对象的请求头添加到request对象中
        request.getHeaders().putAll(originalRequest.getHeaders());
        // 将原始请求路由信息添加到request中
        request.getParameters().putAll(originalRequest.getParameters());

        // 构建默认请求报文信息，包含证书、编码、签名处理器、响应处理器、请求失败尝试策略。以上信息全部是默认的
        ExecutionContext context = createDefaultContext(request.getMethod(), bucketName, key, originalRequest);

        // 添加请求处理器
        context.addRequestHandler(new RequestProgressHanlder());
        if (requestHandlers != null) {
            for (RequestHandler handler : requestHandlers) {
                context.addRequestHandler(handler);
            }
        }

        // 是否启用CRC校验
        if (innerClient.getClientConfiguration().isCrcCheckEnabled()) {
            context.addRequestHandler(new RequestChecksumHanlder());
        }

        // 添加响应处理器
        context.addResponseHandler(new ResponseProgressHandler(originalRequest));
        if (reponseHandlers != null) {
            for (ResponseHandler handler : reponseHandlers) {
                context.addResponseHandler(handler);
            }
        }
        if (innerClient.getClientConfiguration().isCrcCheckEnabled()) {
            context.addResponseHandler(new ResponseChecksumHandler());
        }

        // 添加签名处理器
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

    private RequestSigner createSigner(String bucketName, String key, Credentials creds, ClientConfiguration config) {
        String resourcePath = "/" + (bucketName != null ? urlEncodeKey(bucketName) : "") + (key != null ? ("/" + urlEncodeKey(key)) : "");

        HOSSignerParams params = new HOSSignerParams(resourcePath, creds);
        params.setTickOffset(config.getTickOffset());
        return new HOSUaasSigner(params);
    }

    protected ExecutionContext createDefaultContext(HttpMethod method, String bucketName, String key, WebServiceRequest originalRequest) {
        // 创建执行报文实体
        ExecutionContext context = new ExecutionContext();
        // 从证书提供器中获取证书
        Credentials credentials = credsProvider.getCredentials();
        assertParameterNotNull(credentials, "credentials");
        // 设置默认编码
        context.setCharset(DEFAULT_CHARSET_NAME);
        // 设置签名计算处理器
        context.setSigner(createSigner(bucketName, key, credentials, innerClient.getClientConfiguration()));
        // 设置响应处理器
        context.addResponseHandler(errorResponseHandler);
        // 如果请求方法为post，设置重试策略为无策略
        if (method == HttpMethod.POST && !isRetryablePostRequest(originalRequest)) {
            context.setRetryStrategy(noRetryStrategy);
        }
        // 如果客户端配置类中有自定义重试策略，则添加到报文中
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
