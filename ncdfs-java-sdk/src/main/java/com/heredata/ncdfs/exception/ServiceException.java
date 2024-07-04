package com.heredata.ncdfs.exception;

public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 430933593095358673L;

    private String errorMessage;
    private String errorCode;

    private String rawResponseError;

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
     */
    public ServiceException(String errorMessage, String errorCode) {
        this(errorMessage, errorCode, null);
    }

    /**
     * Creates an instance with error message, error code, request id, host id.
     *
     * @param errorMessage
     *            Error message.
     * @param errorCode
     *            Error code.
     * @param cause
     *            A {@link Throwable} instance indicates a specific exception.
     */
    public ServiceException(String errorMessage, String errorCode, Throwable cause) {
        this(errorMessage, errorCode, null, cause);
    }

    /**
     * Creates an instance with error message, error code, request id, host id,
     * HOS response error, and a Throwable instance.
     *
     * @param errorMessage
     *            Error message.
     * @param errorCode
     *            Error code.
     * @param rawResponseError
     *            HOS error message in response.
     * @param cause
     *            A {@link Throwable} instance indicates a specific exception.
     */
    public ServiceException(String errorMessage, String errorCode,
                            String rawResponseError, Throwable cause) {
        this(errorMessage, cause);
        this.errorCode = errorCode;
        this.rawResponseError = rawResponseError;
    }

    /**
     * Gets error message.
     *
     * @return Error message.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Gets the error code.
     *
     * @return The error code in string.
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Gets the error message in HOS response.
     *
     * @return Error response in string.
     */
    public String getRawResponseError() {
        return rawResponseError;
    }

    /**
     * Sets the error response from HOS.
     *
     * @param rawResponseError
     *            The error response from HOS.
     */
    public void setRawResponseError(String rawResponseError) {
        this.rawResponseError = rawResponseError;
    }

    private String formatRawResponseError() {
        if (rawResponseError == null || rawResponseError.equals("")) {
            return "";
        }
        return String.format("\n[ResponseError]:\n%s", this.rawResponseError);
    }

    @Override
    public String getMessage() {
        return getErrorMessage() + "\n[ErrorCode]: " + getErrorCode()
                + "\n[ErrorMessage]: " + getErrorMessage();
    }
}
