package com.heredata.handler;

import com.heredata.HttpHeaders;
import com.heredata.ResponseMessage;
import com.heredata.event.ProgressInputStream;
import com.heredata.event.ProgressListener;
import com.heredata.event.ProgressPublisher;
import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import com.heredata.model.WebServiceRequest;
import com.heredata.utils.LogUtils;

import java.io.InputStream;
import java.util.Map;

/**
 * <p>Title: ResponseProgressHandler</p>
 * <p>Description: 发送请求的过程即发送前的必要计算，例如计算content-length处理器 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 14:15
 */
public class ResponseProgressHandler<T> implements ResponseHandler<T> {

    private final WebServiceRequest originalRequest;

    public ResponseProgressHandler(WebServiceRequest originalRequest) {
        this.originalRequest = originalRequest;
    }

    @Override
    public void handle(ResponseMessage response) throws ClientException {

        final ProgressListener listener = this.originalRequest.getProgressListener();
        Map<String, String> headers = response.getHeaders();
        String s = headers.get(HttpHeaders.CONTENT_LENGTH);
        if (s != null) {
            try {
                long contentLength = Long.parseLong(s);
                ProgressPublisher.publishResponseContentLength(listener, contentLength);
            } catch (NumberFormatException e) {
                LogUtils.logException("Cannot parse the Content-Length header of the response: ", e);
            }
        }

        InputStream content = response.getContent();
        if (content != null && listener != ProgressListener.NOOP) {
            InputStream progressInputStream = ProgressInputStream.inputStreamForResponse(content, originalRequest);
            response.setContent(progressInputStream);
        }
    }

    @Override
    public void handle(ResponseMessage response, Class<T> responseClass) throws ServiceException, ClientException {

    }
}
