package com.heredata;

import com.heredata.comm.HttpMesssage;
import com.heredata.comm.ServiceClient;
import lombok.Data;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;

/**
 * <p>Title: ResponseMessage</p>
 * <p>Description: 响应信息类 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 15:40
 */
@Data
public class ResponseMessage extends HttpMesssage {

    private static final int HTTP_SUCCESS_STATUS_CODE = 200;

    private String uri;
    private Integer statusCode;
    private String requestId;
    private ServiceClient.Request request;
    private CloseableHttpResponse httpResponse;

    // For convenience of logging invalid response
    private String errorResponseAsString;

    public ResponseMessage(ServiceClient.Request request) {
        this.request = request;
    }

    public boolean isSuccessful() {
        if (statusCode != null) {
            return statusCode / 100 == HTTP_SUCCESS_STATUS_CODE / 100;
        } else {
            return false;
        }
    }

    public void abort() throws IOException {
        if (httpResponse != null) {
            httpResponse.close();
        }
    }

}
