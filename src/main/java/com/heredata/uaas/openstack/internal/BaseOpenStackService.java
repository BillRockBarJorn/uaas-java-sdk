package com.heredata.uaas.openstack.internal;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.heredata.uaas.api.client.CloudProvider;
import com.heredata.uaas.api.exceptions.OS4JException;
import com.heredata.uaas.api.types.ServiceType;
import com.heredata.uaas.core.transport.*;
import com.heredata.uaas.core.transport.HttpRequest.RequestBuilder;
import com.heredata.uaas.core.transport.internal.HttpExecutor;
import com.heredata.uaas.model.ModelEntity;
import com.heredata.uaas.model.common.ActionResponse;
import com.heredata.uaas.model.common.Payload;
import com.heredata.uaas.model.identity.AuthVersion;
import com.heredata.uaas.model.identity.v3.Service;

import java.util.*;


public class BaseOpenStackService {

    ServiceType serviceType = ServiceType.IDENTITY;
    Function<String, String> endpointFunc;

    protected BaseOpenStackService() {
    }

    protected BaseOpenStackService(ServiceType serviceType) {
        this(serviceType, null);
    }

    protected BaseOpenStackService(ServiceType serviceType, Function<String, String> endpointFunc) {
        this.serviceType = serviceType;
        this.endpointFunc = endpointFunc;
    }

    protected <R> Invocation<R> get(Class<R> returnType, String... path) {
        return builder(returnType, path, HttpMethod.GET);
    }

    protected <R> Invocation<R> post(Class<R> returnType, String... path) {
        return builder(returnType, path, HttpMethod.POST);
    }

    protected <R> Invocation<R> post(Class<R> returnType, ModelEntity modelEntity, String... path) {
        Invocation<R> builder = builder(returnType, path, HttpMethod.POST);
        return builder.entity(modelEntity);
    }

    protected <R> Invocation<ActionResponse> postWithResponse(String... path) {
        return builder(ActionResponse.class, path, HttpMethod.POST);
    }

    protected <R> Invocation<R> put(Class<R> returnType, String... path) {
        return builder(returnType, path, HttpMethod.PUT);
    }

    protected <R> Invocation<R> patch(Class<R> returnType, String... path) {
        return builder(returnType, path, HttpMethod.PATCH);
    }

    protected <R> Invocation<ActionResponse> patchWithResponse(String... path) {
        return builder(ActionResponse.class, path, HttpMethod.PATCH);
    }

    protected <R> Invocation<R> delete(Class<R> returnType, String... path) {
        return builder(returnType, path, HttpMethod.DELETE);
    }

    protected <R> Invocation<ActionResponse> deleteWithResponse(String... path) {
        return builder(ActionResponse.class, path, HttpMethod.DELETE);
    }

    protected <R> Invocation<R> head(Class<R> returnType, String... path) {
        return builder(returnType, path, HttpMethod.HEAD);
    }

    protected <R> Invocation<R> request(HttpMethod method, Class<R> returnType, String path) {
        return builder(returnType, path, method);
    }

    protected String uri(String path, Object... params) {
        if (params.length == 0) {
            return path;
        }
        return String.format(path, params);
    }

    private <R> Invocation<R> builder(Class<R> returnType, String[] path, HttpMethod method) {
        return builder(returnType, Joiner.on("").join(path), method);
    }

    @SuppressWarnings("rawtypes")
    private <R> Invocation<R> builder(Class<R> returnType, String path, HttpMethod method) {
        OSClientSession ses = OSClientSession.getCurrent();
        if (ses == null) {
            throw new OS4JException(
                    "Unable to retrieve current session. Please verify thread has a current session available.");
        }
        RequestBuilder<R> req = HttpRequest.builder(returnType).endpointTokenProvider(ses).config(ses.getConfig())
                .method(method).path(path);
        Map headers = ses.getHeaders();
        if (headers != null && headers.size() > 0) {
            return new Invocation<R>(req, serviceType, endpointFunc).headers(headers);
        } else {
            return new Invocation<R>(req, serviceType, endpointFunc);
        }
    }

    protected static class Invocation<R> {
        RequestBuilder<R> req;

        protected Invocation(RequestBuilder<R> req, ServiceType serviceType, Function<String, String> endpointFunc) {
            this.req = req;
            req.serviceType(serviceType);
            req.endpointFunction(endpointFunc);
        }

        public HttpRequest<R> getRequest() {
            return req.build();
        }

        public Invocation<R> param(String name, Object value) {
            req.queryParam(name, value);
            return this;
        }

        public Invocation<R> updateParam(String name, Object value) {
            req.updateQueryParam(name, value);
            return this;
        }

        public Invocation<R> params(Map<String, ? extends Object> params) {
            if (params != null) {
                for (String name : params.keySet()) {
                    req.queryParam(name, params.get(name));
                }
            }
            return this;
        }

        public Invocation<R> param(boolean condition, String name, Object value) {
            if (condition) {
                req.queryParam(name, value);
            }
            return this;
        }

        public Invocation<R> paramLists(Map<String, ? extends Iterable<? extends Object>> params) {
            if (params != null) {
                for (Map.Entry<String, ? extends Iterable<? extends Object>> pair : params.entrySet()) {
                    for (Object value : pair.getValue()) {
                        req.queryParam(pair.getKey(), value);
                    }
                }
            }
            return this;
        }

        public Invocation<R> serviceType(ServiceType serviceType) {
            req.serviceType(serviceType);
            return this;
        }

        public Invocation<R> entity(ModelEntity entity) {
            req.entity(entity);
            return this;
        }

        public Invocation<R> entity(Payload<?> entity) {
            req.entity(entity);
            req.contentType(ClientConstants.CONTENT_TYPE_OCTECT_STREAM);
            return this;
        }

        public Invocation<R> contentType(String contentType) {
            req.contentType(contentType);
            return this;
        }

        public Invocation<R> json(String json) {
            req.json(json);
            return this;
        }

        public Invocation<R> headers(Map<String, ? extends Object> headers) {
            if (headers != null) {
                req.headers(headers);
            }
            return this;
        }

        public Invocation<R> header(String name, Object value) {
            req.header(name, value);
            return this;
        }

        public R execute() {
            return execute(null);
        }

        public R execute(ExecutionOptions<R> options) {
//            header(HEADER_USER_AGENT, USER_AGENT);
            HttpRequest<R> request = req.build();
            HttpResponse res = HttpExecutor.create().execute(request);
            return res.getEntity(request.getReturnType(), options);
        }

        public HttpResponse executeWithResponse() {
            HttpResponse res = HttpExecutor.create().execute(req.build());
            return res;
        }

    }

    @SuppressWarnings("rawtypes")
    protected int getServiceVersion() {
        OSClientSession session = OSClientSession.getCurrent();
        if (session.getAuthVersion() == AuthVersion.V3) {
            SortedSet<? extends Service> services = ((OSClientSession.OSClientSessionV3) session).getToken().getAggregatedCatalog().get(serviceType.getType());
            Service service = ((OSClientSession.OSClientSessionV3) session).getToken().getAggregatedCatalog().get(serviceType.getType()).first();

            if (services.isEmpty()) {
                return 1;
            }
            return service.getVersion();
        }
        return 0;
    }

    protected <T> List<T> toList(T[] arr) {
        if (arr == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(arr);
    }

    protected CloudProvider getProvider() {
        return OSClientSession.getCurrent().getProvider();
    }
}
