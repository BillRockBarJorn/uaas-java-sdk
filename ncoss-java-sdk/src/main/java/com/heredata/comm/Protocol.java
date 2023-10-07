package com.heredata.comm;

/**
 * <p>Title: Protocol</p>
 * <p>Description: 向HOS发送请求时使用的通信协议，默认使用http </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 13:54
 */
public enum Protocol {

    HTTP("http"),

    HTTPS("https");

    private final String protocol;

    private Protocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String toString() {
        return protocol;
    }
}
