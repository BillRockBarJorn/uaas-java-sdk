package com.heredata.swift.internal;

import com.heredata.ClientConfiguration;
import com.heredata.comm.HttpMethod;
import com.heredata.comm.ServiceClient;
import com.heredata.comm.io.FixedLengthInputStream;
import com.heredata.model.WebServiceRequest;
import com.heredata.swift.comm.SWIFTRequestMessage;
import org.apache.http.HttpEntity;

import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.heredata.comm.HttpConstants.DEFAULT_FILE_SIZE_LIMIT;
import static com.heredata.swift.utils.SwiftUtils.determineFinalEndpoint;
import static com.heredata.swift.utils.SwiftUtils.determineResourcePath;
import static com.heredata.utils.CodingUtils.assertParameterInRange;


/*
 * HTTP request message builder.
 */
public class RequestMessageBuilder {

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

    public RequestMessageBuilder(ServiceClient innerClient) {
        this.innerClient = innerClient;
    }

    public URI getEndpoint() {
        return endpoint;
    }

    public RequestMessageBuilder setEndpoint(URI endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public RequestMessageBuilder setMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    public HttpEntity getHttpEntity() {
        return httpEntity;
    }

    public RequestMessageBuilder setHttpEntity(HttpEntity httpEntity) {
        this.httpEntity = httpEntity;
        return this;
    }

    public String getBucket() {
        return bucket;
    }

    public RequestMessageBuilder setBucket(String bucket) {
        this.bucket = bucket;
        return this;
    }

    public String getKey() {
        return key;
    }

    public RequestMessageBuilder setKey(String key) {
        this.key = key;
        return this;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public RequestMessageBuilder setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public RequestMessageBuilder addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    public RequestMessageBuilder setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }

    public RequestMessageBuilder addParameter(String key, String value) {
        parameters.put(key, value);
        return this;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public RequestMessageBuilder setInputStream(InputStream instream) {
        this.inputStream = instream;
        return this;
    }

    public RequestMessageBuilder setInputStreamWithLength(FixedLengthInputStream instream) {
        assertParameterInRange(inputSize, -1, DEFAULT_FILE_SIZE_LIMIT);
        this.inputStream = instream;
        this.inputSize = instream.getLength();
        return this;
    }

    public long getInputSize() {
        return inputSize;
    }

    public RequestMessageBuilder setInputSize(long inputSize) {
        assertParameterInRange(inputSize, -1, DEFAULT_FILE_SIZE_LIMIT);
        this.inputSize = inputSize;
        return this;
    }

    public boolean isUseChunkEncoding() {
        return useChunkEncoding;
    }

    public RequestMessageBuilder setUseChunkEncoding(boolean useChunkEncoding) {
        this.useChunkEncoding = useChunkEncoding;
        return this;
    }

    public RequestMessageBuilder setOriginalRequest(WebServiceRequest originalRequest) {
        this.originalRequest = originalRequest;
        return this;
    }

    public String getAccount() {
        return account;
    }

    public RequestMessageBuilder setAccount(String account) {
        this.account = account;
        return this;
    }

    public SWIFTRequestMessage build() {
        ClientConfiguration clientCofig = this.innerClient.getClientConfiguration();
        Map<String, String> sentHeaders = new HashMap<>(this.headers);
        Map<String, String> sentParameters = new LinkedHashMap<>(this.parameters);


        SWIFTRequestMessage request = new SWIFTRequestMessage(this.originalRequest, this.bucket, this.key);
        request.setBucket(bucket);
        request.setKey(key);
        request.setEndpoint(determineFinalEndpoint(this.endpoint, this.bucket, clientCofig));
        request.setResourcePath(determineResourcePath(this.account, this.bucket, this.key, clientCofig.isSLDEnabled()));
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
