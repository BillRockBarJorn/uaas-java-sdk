package com.heredata.connectors.httpclient;

import com.heredata.uaas.api.exceptions.ConnectionException;
import com.heredata.uaas.api.exceptions.ResponseException;
import com.heredata.uaas.core.transport.ClientConstants;
import com.heredata.uaas.core.transport.HttpExecutorService;
import com.heredata.uaas.core.transport.HttpRequest;
import com.heredata.uaas.core.transport.HttpResponse;
import com.heredata.uaas.openstack.internal.OSAuthenticator;
import com.heredata.uaas.openstack.internal.OSClientSession;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HttpExecutor is the default implementation for HttpExecutorService which is responsible for interfacing with HttpClient and mapping common status codes, requests and responses
 * back to the common API
 *
 * @author wuzz
 */
public class HttpExecutorServiceImpl implements HttpExecutorService {

    private static final String NAME = "Apache HttpClient Connector";
    private static final Logger LOG = LoggerFactory.getLogger(HttpExecutorServiceImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> HttpResponse execute(HttpRequest<R> request) {
        try {
            return invoke(request);
        } catch (ResponseException re) {
            throw re;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Invokes the given request
     *
     * @param <R> the return type
     * @param request the request to invoke
     * @return the response
     * @throws Exception the exception
     */
    private <R> HttpResponse invoke(HttpRequest<R> request) throws Exception {

        HttpCommand<R> command = HttpCommand.create(request);

        try {
            return invokeRequest(command);
        } catch (ResponseException re) {
            throw re;
        } catch (Exception pe) {
            throw new ConnectionException(pe.getMessage(), 0, pe);
        }
    }

    private <R> HttpResponse invokeRequest(HttpCommand<R> command) throws Exception {
        CloseableHttpResponse response = command.execute();

        // 如果报401状态码未授权的话就重新构建权限（包括token、session等信息）
        if (command.getRetries() == 0 && response.getStatusLine().getStatusCode() == 401) {
            try {
                OSAuthenticator.reAuthenticate();
                command.getRequest().getHeaders().put(ClientConstants.HEADER_X_AUTH_TOKEN, OSClientSession.getCurrent().getTokenId());
            } finally {
                response.close();
            }
            return invokeRequest(command.incrementRetriesAndReturn());
        }

        return HttpResponseImpl.wrap(response);
    }

    @Override
    public String getExecutorDisplayName() {
        return NAME;
    }
}
