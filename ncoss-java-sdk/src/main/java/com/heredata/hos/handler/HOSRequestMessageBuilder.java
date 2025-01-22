package com.heredata.hos.handler;

import com.heredata.ClientConfiguration;
import com.heredata.comm.HttpMethod;
import com.heredata.comm.ServiceClient;
import com.heredata.comm.io.FixedLengthInputStream;
import com.heredata.hos.comm.HOSRequestMessage;
import com.heredata.model.WebServiceRequest;
import org.apache.http.HttpEntity;

import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.heredata.comm.HttpConstants.DEFAULT_FILE_SIZE_LIMIT;
import static com.heredata.hos.utils.HOSUtils.determineFinalEndpoint;
import static com.heredata.hos.utils.HOSUtils.determineResourcePath;
import static com.heredata.utils.CodingUtils.assertParameterInRange;


/**
 * <p>Title: HOSRequestMessageBuilder</p>
 * <p>Description: RequestMessage构造器 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:05
 */
public class HOSRequestMessageBuilder {

    private URI endpoint;
    private HttpMethod method = HttpMethod.GET;
    private String bucket;
    private String key;
    private String account;
    private Map<String, String> headers = new HashMap<String, String>();
    private Map<String, String> parameters = new LinkedHashMap<String, String>();
    private InputStream inputStream;
    private long inputSize = 0;
    private ServiceClient innerClient;
    private boolean useChunkEncoding = false;
    private HttpEntity httpEntity;
    private WebServiceRequest originalRequest;

    public HOSRequestMessageBuilder(ServiceClient innerClient) {
        this.innerClient = innerClient;
    }

    public URI getEndpoint() {
        return endpoint;
    }

    public HOSRequestMessageBuilder setEndpoint(URI endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HOSRequestMessageBuilder setMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    public HttpEntity getHttpEntity() {
        return httpEntity;
    }

    public HOSRequestMessageBuilder setHttpEntity(HttpEntity httpEntity) {
        this.httpEntity = httpEntity;
        return this;
    }

    public String getBucket() {
        return bucket;
    }

    public HOSRequestMessageBuilder setBucket(String bucket) {
        this.bucket = bucket;
        return this;
    }

    public String getKey() {
        return key;
    }

    public HOSRequestMessageBuilder setKey(String key) {
        this.key = key;
        return this;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public HOSRequestMessageBuilder setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public HOSRequestMessageBuilder addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    public HOSRequestMessageBuilder setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }

    public HOSRequestMessageBuilder addParameter(String key, String value) {
        parameters.put(key, value);
        return this;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public HOSRequestMessageBuilder setInputStream(InputStream instream) {
        this.inputStream = instream;
        return this;
    }

    public HOSRequestMessageBuilder setInputStreamWithLength(FixedLengthInputStream instream) {
        assertParameterInRange(inputSize, -1, DEFAULT_FILE_SIZE_LIMIT);
        this.inputStream = instream;
        this.inputSize = instream.getLength();
        return this;
    }

    public long getInputSize() {
        return inputSize;
    }

    public HOSRequestMessageBuilder setInputSize(long inputSize) {
        assertParameterInRange(inputSize, -1, DEFAULT_FILE_SIZE_LIMIT);
        this.inputSize = inputSize;
        return this;
    }

    public boolean isUseChunkEncoding() {
        return useChunkEncoding;
    }

    public HOSRequestMessageBuilder setUseChunkEncoding(boolean useChunkEncoding) {
        this.useChunkEncoding = useChunkEncoding;
        return this;
    }

    public HOSRequestMessageBuilder setOriginalRequest(WebServiceRequest originalRequest) {
        this.originalRequest = originalRequest;
        return this;
    }

    public String getAccount() {
        return account;
    }

    public HOSRequestMessageBuilder setAccount(String account) {
        this.account = account;
        return this;
    }

    public HOSRequestMessage build() {
        ClientConfiguration clientCofig = this.innerClient.getClientConfiguration();
        Map<String, String> sentHeaders = new HashMap<>(this.headers);
        Map<String, String> sentParameters = new LinkedHashMap<>(this.parameters);

        HOSRequestMessage request = new HOSRequestMessage(this.originalRequest, this.bucket, this.key);
        request.setBucket(bucket);
        request.setKey(key);
        request.setEndpoint(determineFinalEndpoint(this.endpoint, this.bucket, clientCofig));
        String resourcePath = null;
        if ("HOS_null".equals(this.account)){
            // 走对象存储4的底层
            resourcePath = determineResourcePath(this.bucket, this.key, clientCofig.isSLDEnabled());
        } else{
            // 走对象存储3的底层
            resourcePath = determineResourcePath(this.account, this.bucket, this.key, clientCofig.isSLDEnabled());
        }
        request.setResourcePath(resourcePath);
        request.setHeaders(sentHeaders);
        request.setParameters(sentParameters);
        request.setMethod(this.method);
        request.setContent(this.inputStream);
        request.setContentLength(this.inputSize);
        request.setUseChunkEncoding(this.inputSize == -1 ? true : this.useChunkEncoding);
        request.setHttpEntity(this.httpEntity);
        return request;
    }

}
