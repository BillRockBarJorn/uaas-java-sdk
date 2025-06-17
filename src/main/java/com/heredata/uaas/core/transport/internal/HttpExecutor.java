package com.heredata.uaas.core.transport.internal;

import com.heredata.uaas.api.exceptions.ConnectorNotFoundException;
import com.heredata.uaas.core.transport.HttpExecutorService;
import com.heredata.uaas.core.transport.HttpRequest;
import com.heredata.uaas.core.transport.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * HttpExecutor is a delegate to the underline connector associated to OpenStack4j.
 *
 * @author wuzz
 */
public class HttpExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(HttpExecutor.class);
    private static final HttpExecutor INSTANCE = new HttpExecutor();
    private HttpExecutorService service;

    private HttpExecutor() {
    }

    private HttpExecutorService service() {
        if (service != null) {
            return service;
        }

        Iterator<HttpExecutorService> it = ServiceLoader.load(HttpExecutorService.class).iterator();
        if (!it.hasNext()) {
            LOG.error("No OpenStack4j connector found in classpath");
            throw new ConnectorNotFoundException("No OpenStack4j connector found in classpath");
        }
        return service = it.next();
    }

    public static HttpExecutor create() {
        return INSTANCE;
    }

    public String getExecutorName() {
        System.out.println("======================================" + service().getExecutorDisplayName());
        return service().getExecutorDisplayName();
    }

    /**
     * Delegate to {@link HttpExecutorService#execute(HttpRequest)}
     */
    public <R> HttpResponse execute(HttpRequest<R> request) {
        LOG.debug("Executing Request: {} -> {}", request.getEndpoint(), request.getPath());
        return service().execute(request);
    }
}
