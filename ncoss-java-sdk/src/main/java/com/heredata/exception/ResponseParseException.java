package com.heredata.exception;

/**
 * <p>Title: ResponseParseException</p>
 * <p>Description: 解析异常类，当响应结果解析错误后会抛出此异常信息 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 15:13
 */
public class ResponseParseException extends Exception {
    private static final long serialVersionUID = -6660159156997037589L;

    public ResponseParseException() {
        super();
    }

    public ResponseParseException(String message) {
        super(message);
    }

    public ResponseParseException(Throwable cause) {
        super(cause);
    }

    public ResponseParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
