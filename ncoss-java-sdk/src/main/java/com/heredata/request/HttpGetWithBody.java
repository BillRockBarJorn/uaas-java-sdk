package com.heredata.request;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

/**
 * <p>Title: HttpGetWithBody</p>
 * <p>Description: get请求带body发送请求，Apache对应的实体类无法添加body信息 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 15:18
 */
public class HttpGetWithBody extends HttpEntityEnclosingRequestBase {
    public final static String METHOD_NAME = "GET";

    public HttpGetWithBody() {
        super();
    }

    public HttpGetWithBody(final URI uri) {
        super();
        setURI(uri);
    }

    /**
     * @throws IllegalArgumentException if the uri is invalid.
     */
    public HttpGetWithBody(final String uri, HttpEntity entity) {
        super();
        setURI(URI.create(uri));
        setEntity(entity);
    }

    /**
     * @throws IllegalArgumentException if the uri is invalid.
     */
    public HttpGetWithBody(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}
