package com.heredata.connectors.httpclient;

import com.alibaba.fastjson.JSONObject;
import com.heredata.uaas.api.exceptions.ClientResponseException;
import com.heredata.uaas.core.transport.*;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.heredata.uaas.core.transport.ClientConstants.*;

public class HttpResponseImpl implements HttpResponse {

    private static final Logger LOG = LoggerFactory.getLogger(HttpResponseImpl.class);
    private CloseableHttpResponse response;

    private HttpResponseImpl(CloseableHttpResponse response) {
        this.response = response;
    }

    /**
     * Wrap the given Response
     *
     * @param response the response
     * @return the HttpResponse
     */
    public static HttpResponseImpl wrap(CloseableHttpResponse response) {
        return new HttpResponseImpl(response);
    }

    /**
     * Unwrap and return the original Response
     *
     * @return the response
     */
    public CloseableHttpResponse unwrap() {
        return response;
    }

    /**
     * Gets the entity and Maps any errors which will result in a ResponseException
     *
     * @param <T> the generic type
     * @param returnType the return type
     * @return the entity
     */
    @Override
    public <T> T getEntity(Class<T> returnType) {
        return getEntity(returnType, null);
    }

    /**
     * Gets the entity and Maps any errors which will result in a ResponseException
     *
     * @param <T> the generic type
     * @param returnType the return type
     * @param options execution options
     * @return the entity
     */
    @Override
    public <T> T getEntity(Class<T> returnType, ExecutionOptions<T> options) {
        return HttpEntityHandler.handle(this, returnType, options, Boolean.TRUE);
    }

    /**
     * Gets the status from the previous Request
     *
     * @return the status code
     */
    @Override
    public int getStatus() {
        return response.getStatusLine().getStatusCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStatusMessage() {
        return response.getStatusLine().getReasonPhrase();
    }

    /**
     * @return the input stream
     */
    @Override
    public InputStream getInputStream() {
        HttpEntity entity = response.getEntity();
        try {
            if (entity != null) {
                return entity.getContent();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Returns a Header value from the specified name key
     *
     * @param name the name of the header to query for
     * @return the header as a String or null if not found
     */
    @Override
    public String header(String name) {
        Header header = response.getFirstHeader(name);
        return (header != null) ? header.getValue() : null;
    }

    /**
     * @return the a Map of Header Name to Header Value
     */
    @Override
    public Map<String, String> headers() {
        Map<String, String> retHeaders = new HashMap<String, String>();
        Header[] headers = response.getAllHeaders();

        for (Header h : headers) {
            retHeaders.put(h.getName(), h.getValue());
        }
        return retHeaders;
    }

    @Override
    public <T> T readEntity(Class<T> typeToReadAs) {
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            // Normal case if the response has no content, e.g. for a HEAD request
            return null;
        }
        try {
            InputStream content = checkNotNull(entity.getContent(), "Entity content should not be null.");
            if (response.containsHeader(HEADER_X_SUBJECT_TOKEN)) {
                String s = inputStreamToString(content);
                JSONObject headObj = JSONObject.parseObject(s);

                // 获取JSON的根节点
                T t = typeToReadAs.newInstance();
                Field jsonRootName = typeToReadAs.getField(CONTENT_JSON_ROOT_NAME);
                String root = jsonRootName.get(t).toString();

                JSONObject headChildObj = JSONObject.parseObject(s);
                Header[] allHeaders = response.getAllHeaders();
                for (Header item : allHeaders) {
                    headChildObj.put(item.getName(), item.getValue());
                }

                // 将头Object丢进根节点为root的对象里面
                headObj.getJSONObject(root).put(CONTENT_HEAD_JSON_ROOT_NAME, headChildObj);
                return ObjectMapperSingleton.getContext(typeToReadAs).readerFor(typeToReadAs).readValue(headObj.toJSONString());
            } else {
                return ObjectMapperSingleton.getContext(typeToReadAs).readerFor(typeToReadAs).readValue(content);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ClientResponseException(e.getMessage(), 0, e);
        }
    }

    private String inputStreamToString(InputStream inputStream) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int n;
        try {
            while ((n = inputStream.read(b)) != -1) {
                stream.write(b, 0, n);
            }
        } catch (IOException ioException) {
            throw new RuntimeException("inpuStream convert to String failed");
        }
        return stream.toString();
    }

    @Override
    public void close() throws IOException {
        if (response != null) {
            response.close();
        }
    }

    @Override
    public String getContentType() {
        return header(ClientConstants.HEADER_CONTENT_TYPE);
    }
}
