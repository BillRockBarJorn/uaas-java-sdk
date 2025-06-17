package com.heredata.uaas.api.exceptions;

/**
 * Base OpenStackj Exception
 *
 * @author wuzz
 */
public class OS4JException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OS4JException(String message) {
        super(message);
    }

    public OS4JException(String message, Throwable cause) {
        super(message, cause);
    }

    public OS4JException(Throwable cause) {
        super(cause);
    }
}
