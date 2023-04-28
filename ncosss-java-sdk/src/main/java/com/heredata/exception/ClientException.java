package com.heredata.exception;

import com.heredata.ClientErrorCode;

/**
* <p>Title: 客户端异常</p>
* <p>Description: ClientException是表示客户端中任何异常的类。
 *                通常，ClientException发生在发送请求之前或收到服务器端的响应之后。 </p>
* <p>Copyright: Copyright (c) 2022</p>
* <p>Company: Here-Data </p>
* @author wuzz
* @version 1.0.0
* @createtime 2022/10/14 14:34
*
*/
public class ClientException extends RuntimeException {

    private static final long serialVersionUID = 1870835486798448798L;

    private String errorMessage;
    private String requestId;
    private String errorCode;

    /**
     * Creates a default instance.
     */
    public ClientException() {
        super();
    }

    /**
     * Creates an instance with error message.
     *
     * @param errorMessage
     *            Error message.
     */
    public ClientException(String errorMessage) {
        this(errorMessage, null);
    }

    /**
     * Creates an instance with an exception
     *
     * @param cause
     *            An exception.
     */
    public ClientException(Throwable cause) {
        this(null, cause);
    }

    /**
     * Creates an instance with error message and an exception.
     *
     * @param errorMessage
     *            Error message.
     * @param cause
     *            An exception.
     */
    public ClientException(String errorMessage, Throwable cause) {
        super(null, cause);
        this.errorMessage = errorMessage;
        this.errorCode = ClientErrorCode.UNKNOWN;
        this.requestId = "Unknown";
    }

    /**
     * Creates an instance with error message, error code, request Id
     *
     * @param errorMessage
     *            Error message.
     * @param errorCode
     *            Error code, which typically is from a set of predefined
     *            errors. The handler code could do action based on this.
     * @param requestId
     *            Request Id.
     */
    public ClientException(String errorMessage, String errorCode, String requestId) {
        this(errorMessage, errorCode, requestId, null);
    }

    /**
     * Creates an instance with error message, error code, request Id and an
     * exception.
     *
     * @param errorMessage
     *            Error message.
     * @param errorCode
     *            Error code.
     * @param requestId
     *            Request Id.
     * @param cause
     *            An exception.
     */
    public ClientException(String errorMessage, String errorCode, String requestId, Throwable cause) {
        this(errorMessage, cause);
        this.errorCode = errorCode;
        this.requestId = requestId;
    }

    /**
     * Get error message.
     *
     * @return Error message in string.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Get error code.
     *
     * @return Error code.
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Gets request id.
     *
     * @return The request Id.
     */
    public String getRequestId() {
        return requestId;
    }

    @Override
    public String getMessage() {
        return getErrorMessage() + "\n[ErrorCode]: " + (errorCode != null ? errorCode
                : "") + "\n[RequestId]: " + (requestId != null ? requestId : "");
    }
}
