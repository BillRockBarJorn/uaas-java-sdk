package com.heredata.request;

import com.heredata.HttpHeaders;
import com.heredata.comm.ServiceClient;
import org.apache.http.entity.BasicHttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>Title: RepeatableInputStreamEntity</p>
 * <p>Description: TODO </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 15:21
 */
public class RepeatableInputStreamEntity extends BasicHttpEntity {

    private boolean firstAttempt = true;

    private NoAutoClosedInputStreamEntity innerEntity;

    private InputStream content;

    public RepeatableInputStreamEntity(ServiceClient.Request request) {
        setChunked(false);

        String contentType = request.getHeaders().get(HttpHeaders.CONTENT_TYPE);
        content = request.getContent();
        long contentLength = request.getContentLength();

        innerEntity = new NoAutoClosedInputStreamEntity(content, contentLength);
        innerEntity.setContentType(contentType);

        setContent(content);
        setContentType(contentType);
        setContentLength(request.getContentLength());
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public boolean isRepeatable() {
        return content.markSupported() || innerEntity.isRepeatable();
    }

    @Override
    public void writeTo(OutputStream output) throws IOException {
        if (!firstAttempt && isRepeatable()) {
            content.reset();
        }

        firstAttempt = false;
        innerEntity.writeTo(output);
    }
}
