package com.heredata.ncdfs.comm;

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
 * Common class for both HTTP request and HTTP response.
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
