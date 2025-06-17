package com.heredata.uaas.core.transport;

import com.heredata.uaas.api.exceptions.ClientResponseException;

/**
 * Allows for propagation depending on the state of a response.  If applied to an HttpExecution the execution will
 * call the {@link #propagate(HttpResponse)} method.  The method will either throw a ClientResponseException variant
 * or do nothing letting the execution code handle like normal
 *
 * @author wuzz
 */
public interface PropagateResponse {

    /**
     * Called to allow for optional exception propagation depending on the HttpResponse state
     * @param response the http response
     * @throws ClientResponseException variant if execution deemed a failure
     */
    void propagate(HttpResponse response);

}
