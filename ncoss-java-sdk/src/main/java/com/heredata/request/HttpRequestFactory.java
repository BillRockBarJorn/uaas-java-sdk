package com.heredata.request;

import com.heredata.HttpHeaders;
import com.heredata.comm.ExecutionContext;
import com.heredata.comm.HttpMethod;
import com.heredata.comm.ServiceClient;
import com.heredata.comm.io.ChunkedInputStreamEntity;
import com.heredata.exception.ClientException;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;

import java.util.Map.Entry;

/**
 * <p>Title: HttpRequestFactory</p>
 * <p>Description: http请求构建工厂，构建不同请求的请求信息 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 15:19
 */
public class HttpRequestFactory {

    public HttpRequestBase createHttpRequest(ServiceClient.Request request, ExecutionContext context) {

        String uri = request.getUri();

        HttpRequestBase httpRequest;
        HttpMethod method = request.getMethod();
        if (method == HttpMethod.POST) {
            HttpPost postMethod = new HttpPost(uri);

            if (request.getContent() != null) {
                postMethod.setEntity(new RepeatableInputStreamEntity(request));
            }

            httpRequest = postMethod;
        } else if (method == HttpMethod.PUT) {
            HttpPut putMethod = new HttpPut(uri);

            if (request.getContent() != null) {
                if (request.isUseChunkEncoding()) {
                    putMethod.setEntity(buildChunkedInputStreamEntity(request));
                } else {
                    putMethod.setEntity(new RepeatableInputStreamEntity(request));
                }
            }

            httpRequest = putMethod;
        } else if (method == HttpMethod.GET) {
            if (request.getContent() != null) {
                HttpGetWithBody httpGetWithBody = new HttpGetWithBody(uri);
                httpGetWithBody.setEntity(new RepeatableInputStreamEntity(request));
                httpRequest = httpGetWithBody;
            } else {
                httpRequest = new HttpGet(uri);
            }
        } else if (method == HttpMethod.DELETE) {
            if (request.getContent() != null) {
                HttpDeleteWithBody deleteMethod = new HttpDeleteWithBody(uri);
                deleteMethod.setEntity(new RepeatableInputStreamEntity(request));
                httpRequest = deleteMethod;
            } else {
                httpRequest = new HttpDelete(uri);
            }
        } else if (method == HttpMethod.HEAD) {
            if (request.getContent() != null) {
                HttpHeadWithBody httpHeadWithBody = new HttpHeadWithBody(uri);
                httpHeadWithBody.setEntity(new RepeatableInputStreamEntity(request));
                httpRequest = httpHeadWithBody;
            } else {
                httpRequest = new HttpHead(uri);
            }
        } else if (method == HttpMethod.OPTIONS) {
            httpRequest = new HttpOptions(uri);
        } else {
            throw new ClientException("Unknown HTTP method name: " + method.toString());
        }

        configureRequestHeaders(request, context, httpRequest);

        return httpRequest;
    }

    private HttpEntity buildChunkedInputStreamEntity(ServiceClient.Request request) {
        return new ChunkedInputStreamEntity(request);
    }

    private void configureRequestHeaders(ServiceClient.Request request, ExecutionContext context,
                                         HttpRequestBase httpRequest) {

        for (Entry<String, String> entry : request.getHeaders().entrySet()) {
            if (entry.getKey().equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)
                    || entry.getKey().equalsIgnoreCase(HttpHeaders.HOST)) {
                continue;
            }

            httpRequest.addHeader(entry.getKey(), entry.getValue());
        }
    }
}
