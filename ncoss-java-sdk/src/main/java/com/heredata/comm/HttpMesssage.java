package com.heredata.comm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

/**
 * <p>Title: HttpMesssage</p>
 * <p>Description: http报文抽象类 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 13:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public abstract class HttpMesssage {

    private Map<String, String> headers = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
    private InputStream content;
    private long contentLength;

    private HttpEntity httpEntity;

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void close() throws IOException {
        if (content != null) {
            content.close();
            content = null;
        }
    }
}
