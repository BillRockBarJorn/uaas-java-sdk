package com.heredata.ncdfs.exception;

/**
 * The HOSException is thrown upon error when accessing HOS.
 */
public class NCDFSException extends ServiceException {

    private static final long serialVersionUID = -1979779664334663173L;

    private String resourceType;
    private String header;
    private String method;

    public NCDFSException() {
        super();
    }

    public NCDFSException(String errorMessage) {
        super(errorMessage);
    }

    public NCDFSException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

    public NCDFSException(String errorMessage, String errorCode, String header,
                        String resourceType, String method) {
        this(errorMessage, errorCode, header, resourceType, method, null, null);
    }

    public NCDFSException(String errorMessage, String errorCode, String header,
                        String resourceType, String method, Throwable cause) {
        this(errorMessage, errorCode, header, resourceType, method, null, cause);
    }

    public NCDFSException(String errorMessage, String errorCode, String header,
                        String resourceType, String method, String rawResponseError) {
        this(errorMessage, errorCode, header, resourceType, method, rawResponseError, null);
    }

    public NCDFSException(String errorMessage, String errorCode, String header,
                        String resourceType, String method, String rawResponseError, Throwable cause) {
        super(errorMessage, errorCode, rawResponseError, cause);
        this.resourceType = resourceType;
        this.header = header;
        this.method = method;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getHeader() {
        return header;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + (resourceType == null ? "" : "\n[ResourceType]: " + resourceType)
                + (header == null ? "" : "\n[Header]: " + header) + (method == null ? "" : "\n[Method]: " + method);
    }
}
