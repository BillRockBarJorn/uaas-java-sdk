package com.heredata.request;

import org.apache.http.entity.AbstractHttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>Title: NoAutoClosedInputStreamEntity</p>
 * <p>Description: 默认实体org.apache.http.entity。InputStreamEntity将关闭
 *                 调用wirteTo之后的输入流。为了避免这种情况，我们定制了一个实体
 *                 不会自动关闭流。 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 15:23
 */
public class NoAutoClosedInputStreamEntity extends AbstractHttpEntity {

    private final static int BUFFER_SIZE = 2048;

    private final InputStream content;
    private final long length;

    public NoAutoClosedInputStreamEntity(final InputStream instream, long length) {
        super();
        if (instream == null) {
            throw new IllegalArgumentException("Source input stream may not be null");
        }
        this.content = instream;
        this.length = length;
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public long getContentLength() {
        return this.length;
    }

    @Override
    public InputStream getContent() throws IOException {
        return this.content;
    }

    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream instream = this.content;

        byte[] buffer = new byte[BUFFER_SIZE];
        int l;
        if (this.length < 0) {
            // consume until EOF
            while ((l = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, l);
            }
        } else {
            // consume no more than length
            long remaining = this.length;
            while (remaining > 0) {
                l = instream.read(buffer, 0, (int) Math.min(BUFFER_SIZE, remaining));
                if (l == -1) {
                    break;
                }
                outstream.write(buffer, 0, l);
                remaining -= l;
            }
        }

    }

    @Override
    public boolean isStreaming() {
        return true;
    }

}
