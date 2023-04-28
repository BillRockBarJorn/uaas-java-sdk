package com.heredata.model;

import java.io.InputStream;

/**
 * The result of the callback.
 */
public interface CallbackResult {

    /**
     * Gets the response body of the callback request. The caller needs to close
     * it after usage.
     *
     * @return The {@link InputStream} instance of the response body.
     */
    public InputStream getCallbackResponseBody();

    /**
     * Sets the callback response body.
     *
     * @param callbackResponseBody
     *            The {@link InputStream} instance of the response body.
     */
    public void setCallbackResponseBody(InputStream callbackResponseBody);
}
