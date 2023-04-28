package com.heredata.request;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

/**
 * <p>Title: HttpHeadWithBody</p>
 * <p>Description: head请求带body发送请求，Apache对应的实体类无法添加body参数 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 15:19
 */
public class HttpHeadWithBody extends HttpEntityEnclosingRequestBase {

    public final static String METHOD_NAME = "HEAD";

    public HttpHeadWithBody() {
        super();
    }

    public HttpHeadWithBody(final URI uri) {
        super();
        setURI(uri);
    }

    /**
     * @throws IllegalArgumentException if the uri is invalid.
     */
    public HttpHeadWithBody(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }

}
