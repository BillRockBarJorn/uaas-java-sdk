package com.heredata.comm;

import com.heredata.ClientConfiguration;
import com.heredata.HttpHeaders;
import com.heredata.ResponseMessage;
import com.heredata.signer.RequestSigner;
import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import com.heredata.handler.RequestHandler;
import com.heredata.handler.ResponseHandler;
import com.heredata.utils.CodingUtils;
import com.heredata.utils.HttpUtil;
import com.heredata.utils.LogUtils;
import com.heredata.utils.ResourceUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;


/**
 * Abstract service client that provides interfaces to access services.
 */
public abstract class ServiceClient {

    protected ClientConfiguration config;

    protected ServiceClient(ClientConfiguration config) {
        this.config = config;
    }

    public ClientConfiguration getClientConfiguration() {
        return this.config;
    }

    /**
     * Send HTTP request with specified context to HOS and wait for HTTP
     * response.
     */
    public ResponseMessage sendRequest(RequestMessage request, ExecutionContext context)
            throws ServiceException, ClientException {

        CodingUtils.assertParameterNotNull(request, "request");
        CodingUtils.assertParameterNotNull(context, "context");

        try {
            return sendRequestImpl(request, context);
        } finally {
            // Close the request stream as well after the request is completed.
            try {
                request.close();
            } catch (IOException ex) {
                LogUtils.logException("Unexpected io exception when trying to close http request: ", ex);
                throw new ClientException("Unexpected io exception when trying to close http request: ", ex);
            }
        }
    }

    private ResponseMessage sendRequestImpl(RequestMessage request, ExecutionContext context)
            throws ClientException, ServiceException {

        // 获取重试策略
        RetryStrategy retryStrategy = context.getRetryStrategy() != null ? context.getRetryStrategy()
                : this.getDefaultRetryStrategy();

        // Sign the request if a signer provided.
        if (context.getSigner() != null && !request.isUseUrlSignature()) {
            context.getSigner().sign(request);
        }

        // 执行签名处理器
        for (RequestSigner signer : context.getSignerHandlers()) {
            signer.sign(request);
        }

        // 处理输入流的缓存问题
        InputStream requestContent = request.getContent();
        if (requestContent != null && requestContent.markSupported()) {
            requestContent.mark(HttpConstants.DEFAULT_STREAM_BUFFER_SIZE);
        }

        int retries = 0;
        ResponseMessage response = null;

        while (true) {
            try {
                if (retries > 0) {
                    pause(retries, retryStrategy);
                    if (requestContent != null && requestContent.markSupported()) {
                        try {
                            requestContent.reset();
                        } catch (IOException ex) {
                            LogUtils.logException("Failed to reset the request input stream: ", ex);
                            throw new ClientException("Failed to reset the request input stream: ", ex);
                        }
                    }
                }

                /*
                 * The key four steps to send HTTP requests and receive HTTP
                 * responses.
                 */

                // Step 1. 执行实现了RequestHandler接口的实现类
                handleRequest(request, context.getRequestHandlers());

                // Step 2. 构建http请求参数实体   并且将context添加到request中
                Request httpRequest = buildRequest(request, context);


                // Step 3. Send HTTP request to HOS.
                String poolStatsInfo = config.isLogConnectionPoolStatsEnable() ? "Connection pool stats " + getConnectionPoolStats() : "";
                long startTime = System.currentTimeMillis();
                LogUtils.getLog().info("requestURL：：" + httpRequest.uri + "  method:" + httpRequest.method + "  headers:" + httpRequest.getHeaders());
                response = sendRequestCore(httpRequest, context);
                LogUtils.getLog().info("response code::" + response.getStatusCode() + "requestUrl:" + httpRequest.uri);
                long duration = System.currentTimeMillis() - startTime;
                if (duration > config.getSlowRequestsThreshold()) {
                    LogUtils.getLog().warn(formatSlowRequestLog(request, response, duration) + poolStatsInfo);
                }

                // Step 4. Preprocess HTTP response.
                handleResponse(response, context.getResponseHandlers());

                return response;
            } catch (ServiceException sex) {
                LogUtils.logException("[Server]Unable to execute HTTP request: ", sex,
                        request.getOriginalRequest().isLogEnabled());

                // Notice that the response should not be closed in the
                // finally block because if the request is successful,
                // the response should be returned to the callers.
                closeResponseSilently(response);

                if (!shouldRetry(sex, request, response, retries, retryStrategy)) {
                    throw sex;
                }
            } catch (ClientException cex) {
                LogUtils.logException("[Client]Unable to execute HTTP request: ", cex,
                        request.getOriginalRequest().isLogEnabled());

                closeResponseSilently(response);

                if (!shouldRetry(cex, request, response, retries, retryStrategy)) {
                    throw cex;
                }
            } catch (Exception ex) {
                LogUtils.logException("[Unknown]Unable to execute HTTP request: ", ex,
                        request.getOriginalRequest().isLogEnabled());

                closeResponseSilently(response);

                throw new ClientException(
                        ResourceUtils.COMMON_RESOURCE_MANAGER.getFormattedString("ConnectionError", ex.getMessage()), ex);
            } finally {
                retries++;
            }
        }
    }

    /**
     * Implements the core logic to send requests to HOS services.
     */
    protected abstract ResponseMessage sendRequestCore(Request request, ExecutionContext context) throws IOException;

    private Request buildRequest(RequestMessage requestMessage, ExecutionContext context) throws ClientException {

        Request request = new Request();
        request.setMethod(requestMessage.getMethod());
        request.setUseChunkEncoding(requestMessage.isUseChunkEncoding());

        if (requestMessage.isUseUrlSignature()) {
            request.setUrl(requestMessage.getAbsoluteUrl().toString());
            request.setUseUrlSignature(true);

            request.setContent(requestMessage.getContent());
            request.setContentLength(requestMessage.getContentLength());
            request.setHeaders(requestMessage.getHeaders());

            return request;
        }


        request.setHeaders(requestMessage.getHeaders());
        // The header must be converted after the request is signed,
        // otherwise the signature will be incorrect.
        if (request.getHeaders() != null) {
            HttpUtil.convertHeaderCharsetFromIso88591(request.getHeaders());
        }

        final String delimiter = "/";
        String uri = requestMessage.getEndpoint().toString();
        if (!uri.endsWith(delimiter) && (requestMessage.getResourcePath() == null
                || !requestMessage.getResourcePath().startsWith(delimiter))) {
            uri += delimiter;
        }

        if (requestMessage.getResourcePath() != null) {
            uri += requestMessage.getResourcePath();
        }
        String paramString = HttpUtil.paramToQueryString(requestMessage.getParameters(), context.getCharset());

        /*
         * For all non-POST requests, and any POST requests that already have a
         * payload, we put the encoded params directly in the URI, otherwise,
         * we'll put them in the POST request's payload.
         */
        boolean requestHasNoPayload = requestMessage.getContent() != null;
        boolean requestIsPost = requestMessage.getMethod() == HttpMethod.POST;
        boolean putParamsInUri = !requestIsPost || requestHasNoPayload;
        if (paramString != null && putParamsInUri) {
            uri += "?" + paramString;
        }

        request.setUrl(uri);

        if (requestIsPost && requestMessage.getContent() == null && paramString != null) {
            // Put the param string to the request body if POSTing and
            // no content.
            try {
                byte[] buf = paramString.getBytes(context.getCharset());
                ByteArrayInputStream content = new ByteArrayInputStream(buf);
                request.setContent(content);
                request.setContentLength(buf.length);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(
                        ResourceUtils.COMMON_RESOURCE_MANAGER.getFormattedString("EncodingFailed", e.getMessage()));
            }
        } else {
            request.setContent(requestMessage.getContent());
            request.setContentLength(requestMessage.getContentLength());
        }

        // body
        if (requestMessage.getHttpEntity() != null) {
            request.setHttpEntity(requestMessage.getHttpEntity());
        }

        return request;
    }

    private void handleResponse(ResponseMessage response, List<ResponseHandler> responseHandlers)
            throws ServiceException, ClientException {
        for (ResponseHandler h : responseHandlers) {
            h.handle(response);
        }
    }

    private void handleRequest(RequestMessage message, List<RequestHandler> resquestHandlers)
            throws ServiceException, ClientException {
        for (RequestHandler h : resquestHandlers) {
            h.handle(message);
        }
    }

    private void pause(int retries, RetryStrategy retryStrategy) throws ClientException {

        long delay = retryStrategy.getPauseDelay(retries);

        LogUtils.getLog().debug(
                "An retriable error request will be retried after " + delay + "(ms) with attempt times: " + retries);

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }

    private boolean shouldRetry(Exception exception, RequestMessage request, ResponseMessage response, int retries,
                                RetryStrategy retryStrategy) {

        if (retries >= config.getMaxErrorRetry()) {
            return false;
        }

        if (!request.isRepeatable()) {
            return false;
        }

        if (retryStrategy.shouldRetry(exception, request, response, retries)) {
            LogUtils.getLog().debug("Retrying on " + exception.getClass().getName() + ": " + exception.getMessage());
            return true;
        }
        return false;
    }

    private void closeResponseSilently(ResponseMessage response) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException ioe) {
                /* silently close the response. */
            }
        }
    }

    private String formatSlowRequestLog(RequestMessage request, ResponseMessage response, long useTimesMs) {
        return String.format(
                "Request cost %d seconds, endpoint %s, resourcePath %s, " + "method %s, Date '%s', statusCode %d.",
                useTimesMs / 1000, request.getEndpoint(), request.getResourcePath(), request.getMethod(), request.getHeaders().get(HttpHeaders.DATE),
                response.getStatusCode());
    }

    protected abstract RetryStrategy getDefaultRetryStrategy();

    public abstract void shutdown();

    public String getConnectionPoolStats() {
        return "";
    }

    ;

    /**
     * Wrapper class based on {@link HttpMesssage} that represents HTTP request
     * message to HOS.
     */
    public static class Request extends HttpMesssage {
        private String uri;
        private HttpMethod method;
        private boolean useUrlSignature = false;
        private boolean useChunkEncoding = false;

        public String getUri() {
            return this.uri;
        }

        public void setUrl(String uri) {
            this.uri = uri;
        }

        public HttpMethod getMethod() {
            return method;
        }

        public void setMethod(HttpMethod method) {
            this.method = method;
        }

        public boolean isUseUrlSignature() {
            return useUrlSignature;
        }

        public void setUseUrlSignature(boolean useUrlSignature) {
            this.useUrlSignature = useUrlSignature;
        }

        public boolean isUseChunkEncoding() {
            return useChunkEncoding;
        }

        public void setUseChunkEncoding(boolean useChunkEncoding) {
            this.useChunkEncoding = useChunkEncoding;
        }
    }
}

