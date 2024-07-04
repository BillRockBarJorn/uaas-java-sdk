package com.heredata.ncdfs;

import com.heredata.ncdfs.comm.HttpMesssage;
import com.heredata.ncdfs.comm.ServiceClient;
import lombok.Data;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;

@Data
public class ResponseMessage extends HttpMesssage {

    private static final int HTTP_SUCCESS_STATUS_CODE = 200;

    private String uri;
    private int statusCode;

    private ServiceClient.Request request;
    private CloseableHttpResponse httpResponse;

    // For convenience of logging invalid response
    private String errorResponseAsString;

    public ResponseMessage(ServiceClient.Request request) {
        this.request = request;
    }

    public ServiceClient.Request getRequest() {
        return request;
    }

    public boolean isSuccessful() {
        return statusCode / 100 == HTTP_SUCCESS_STATUS_CODE / 100;
    }

    public String getErrorResponseAsString() {
        return errorResponseAsString;
    }

    public void setErrorResponseAsString(String errorResponseAsString) {
        this.errorResponseAsString = errorResponseAsString;
    }

    public void abort() throws IOException {
        if (httpResponse != null) {
            httpResponse.close();
        }
    }

    public CloseableHttpResponse getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(CloseableHttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    @Override
    public String toString() {
        return "ResponseMessage{" +
                "uri='" + uri + '\'' +
                ", statusCode=" + statusCode +
                ", request=" + request +
                ", httpResponse=" + httpResponse +
                ", errorResponseAsString='" + errorResponseAsString + '\'' +
                '}';
    }
}
