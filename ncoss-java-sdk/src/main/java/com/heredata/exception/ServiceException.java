package com.heredata.exception;

import lombok.Data;

/**
 * <p>Title: 任何预期或意外OSS服务器端错误的基本异常类</p>
 * <p>Description:  非成功状态码都会抛出此异常</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/14 14:35
 */
@Data
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 430933593095358673L;

    private String errorMessage;
    private String errorCode;
    private String rawResponseError;
    private String requestId;
    private String hostId;
    /**
     * 响应状态码
     */
    private Integer statusCode;

    private String resourceType;
    private String header;
    private String method;

    /**
     * Creates a default instance.
     */
    public ServiceException() {
        super();
    }

    /**
     * Creates an instance with the error message.
     *
     * @param errorMessage
     *            Error message.
     */
    public ServiceException(String errorMessage) {
        super((String) null);
        this.errorMessage = errorMessage;
    }

    /**
     * Creates an instance with a {@link Throwable} instance.
     *
     * @param cause
     *            A {@link Throwable} instance.
     */
    public ServiceException(Throwable cause) {
        super(null, cause);
    }

    /**
     * Creates an instance with a {@link Throwable} instance and error message.
     *
     * @param errorMessage
     *            Error message.
     * @param cause
     *            A {@link Throwable} instance.
     */
    public ServiceException(String errorMessage, Throwable cause) {
        super(null, cause);
        this.errorMessage = errorMessage;
    }

    /**
     * Creates an instance with error message, error code, request id, host id.
     *
     * @param errorMessage
     *            Error message.
     * @param errorCode
     *            Error code.
     * @param requestId
     *            Request Id.
     */
    public ServiceException(String errorMessage, String errorCode, String requestId) {
        this(errorMessage, errorCode, requestId, null, null);
    }

    /**
     * Creates an instance with error message, error code, request id, host id.
     *
     * @param errorMessage
     *            Error message.
     * @param errorCode
     *            Error code.
     * @param requestId
     *            Request Id.
     * @param hostId
     *            Host Id.
     */
    public ServiceException(String errorMessage, String errorCode, String requestId, String hostId) {
        this(errorMessage, errorCode, requestId, hostId, null);
    }

    /**
     * Creates an instance with error message, error code, request id, host id.
     *
     * @param errorMessage
     *            Error message.
     * @param errorCode
     *            Error code.
     * @param requestId
     *            Request Id.
     * @param hostId
     *            Host Id.
     * @param cause
     *            A {@link Throwable} instance indicates a specific exception.
     */
    public ServiceException(String errorMessage, String errorCode, String requestId, String hostId, Throwable cause) {
        this(errorMessage, errorCode, requestId, hostId, null, cause);
    }

    /**
     * Creates an instance with error message, error code, request id, host id,
     * OSS response error, and a Throwable instance.
     *
     * @param errorMessage
     *            Error message.
     * @param errorCode
     *            Error code.
     * @param requestId
     *            Request Id.
     * @param hostId
     *            Host Id.
     * @param rawResponseError
     *            OSS error message in response.
     * @param cause
     *            A {@link Throwable} instance indicates a specific exception.
     */
    public ServiceException(String errorMessage, String errorCode, String requestId, String hostId,
                            String rawResponseError, Throwable cause) {
        this(errorMessage, cause);
        this.errorCode = errorCode;
        this.requestId = requestId;
        this.hostId = hostId;
        this.rawResponseError = rawResponseError;
    }

    public ServiceException(String errorMessage, String errorCode, String requestId, String hostId, String header,
                            String resourceType, String method, Throwable cause) {
        this(errorMessage, errorCode, requestId, hostId, cause);
        this.resourceType = resourceType;
        this.header = header;
        this.method = method;
    }

    public ServiceException(String errorMessage, String errorCode, String requestId, String hostId, String header,
                            String resourceType, String method, String rawResponseError, Throwable cause) {
        this(errorMessage, errorCode, requestId, hostId, rawResponseError, cause);
        this.resourceType = resourceType;
        this.header = header;
        this.method = method;
    }

    private String formatRawResponseError() {
        if (rawResponseError == null || rawResponseError.equals("")) {
            return "";
        }
        return String.format("\n[ResponseError]:\n%s", this.rawResponseError);
    }

    @Override
    public String getMessage() {
        return getErrorMessage() + "\n[ErrorCode]: " + getErrorCode() + "\n[RequestId]: " + getRequestId()
                + "\n[HostId]: " + getHostId() + formatRawResponseError();
    }
}
