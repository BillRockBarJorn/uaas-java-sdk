package com.heredata;

/**
 * <p>Title: ClientErrorCode</p>
 * <p>Description: 客户端错误码 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 13:55
 */
public interface ClientErrorCode {

    /**
     * Unknown error. This means the error is not expected.
     */
    static final String UNKNOWN = "Unknown";

    /**
     * Unknown host. This error is returned when a
     * {@link java.net.UnknownHostException} is thrown.
     */
    static final String UNKNOWN_HOST = "UnknownHost";

    /**
     * connection times out.
     */
    static final String CONNECTION_TIMEOUT = "ConnectionTimeout";

    /**
     * Socket times out
     */
    static final String SOCKET_TIMEOUT = "SocketTimeout";

    /**
     * Socket exception
     */
    static final String SOCKET_EXCEPTION = "SocketException";

    /**
     * Connection is refused by server side.
     */
    static final String CONNECTION_REFUSED = "ConnectionRefused";

    /**
     * The input stream is not repeatable for reading.
     */
    static final String NONREPEATABLE_REQUEST = "NonRepeatableRequest";

    /**
     * Thread interrupted while reading the input stream.
     */
    static final String INPUTSTREAM_READING_ABORTED = "InputStreamReadingAborted";

    /**
     * Ssl exception
     */
    static final String SSL_EXCEPTION = "SslException";

    /**
     * EncodingFailed
     */
    static final String ENCODING_FAILED = "EncodingFailed";
}
