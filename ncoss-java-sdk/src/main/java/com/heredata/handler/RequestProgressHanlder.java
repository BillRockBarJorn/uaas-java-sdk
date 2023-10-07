package com.heredata.handler;

import com.heredata.HttpHeaders;
import com.heredata.comm.RequestMessage;
import com.heredata.event.ProgressInputStream;
import com.heredata.event.ProgressListener;
import com.heredata.event.ProgressPublisher;
import com.heredata.exception.ClientException;
import com.heredata.model.WebServiceRequest;
import com.heredata.utils.LogUtils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * <p>Title: RequestProgressHanlder</p>
 * <p>Description: 发送请求的过程即发送前的必要计算，例如计算content-length处理器 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 14:04
 */
public class RequestProgressHanlder implements RequestHandler {

    @Override
    public void handle(RequestMessage request) throws ClientException {

        final WebServiceRequest originalRequest = request.getOriginalRequest();
        final ProgressListener listener = originalRequest.getProgressListener();
        Map<String, String> headers = request.getHeaders();
        String s = headers.get(HttpHeaders.CONTENT_LENGTH);
        if (s != null) {
            try {
                long contentLength = Long.parseLong(s);
                ProgressPublisher.publishRequestContentLength(listener, contentLength);
            } catch (NumberFormatException e) {
                LogUtils.logException("Cannot parse the Content-Length header of the request: ", e);
            }
        }

        InputStream content = request.getContent();
        if (content == null) {
            return;
        }
        if (!content.markSupported()) {
            content = new BufferedInputStream(content);
        }
        request.setContent(listener == ProgressListener.NOOP ? content
                : ProgressInputStream.inputStreamForRequest(content, originalRequest));
    }

}
