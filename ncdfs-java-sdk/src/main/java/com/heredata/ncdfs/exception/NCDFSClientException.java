package com.heredata.ncdfs.exception;

import com.heredata.ncdfs.ClientErrorCode;

public class NCDFSClientException extends RuntimeException {

    private static final long serialVersionUID = 1870835486798448798L;

    private String errorMessage;
    private String requestId;
    private String errorCode;

    /**
     * Creates a default instance.
     */
    public NCDFSClientException() {
        super();
    }

    /**
     * Creates an instance with error message.
     *
     * @param errorMessage
     *            Error message.
     */
    public NCDFSClientException(String errorMessage) {
        this(errorMessage, null);
    }

    /**
     * Creates an instance with an exception
     *
     * @param cause
     *            An exception.
     */
    public NCDFSClientException(Throwable cause) {
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
    public NCDFSClientException(String errorMessage, Throwable cause) {
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
    public NCDFSClientException(String errorMessage, String errorCode, String requestId) {
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
    public NCDFSClientException(String errorMessage, String errorCode, String requestId, Throwable cause) {
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
                : "");
    }
}
