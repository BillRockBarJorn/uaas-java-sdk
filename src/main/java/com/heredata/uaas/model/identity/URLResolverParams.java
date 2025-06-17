package com.heredata.uaas.model.identity;

import com.heredata.uaas.api.types.Facing;
import com.heredata.uaas.api.types.ServiceType;
import com.heredata.uaas.model.common.resolvers.LatestServiceVersionResolver;
import com.heredata.uaas.model.common.resolvers.ServiceVersionResolver;
import com.heredata.uaas.model.common.resolvers.StableServiceVersionResolver;
import com.heredata.uaas.model.identity.v3.Token;

/**
 * Dynamic parameters used for URL resolution with Endpoints
 *
 * @author wuzz
 */
public class URLResolverParams {

    public Token token;
    public ServiceType type;
    public String region;
    public Facing perspective;
    private ServiceVersionResolver resolver;

    private URLResolverParams(Token token, ServiceType type) {
        this.token = token;
        this.type = (type == null) ? ServiceType.IDENTITY : type;
    }

    public static URLResolverParams create(Token token, ServiceType type) {
        return new URLResolverParams(token, type);
    }

    public static URLResolverParams create(ServiceType type) {
        return new URLResolverParams(type);
    }

    private URLResolverParams(ServiceType type) {
        this.type = (type == null) ? ServiceType.IDENTITY : type;
    }

    public URLResolverParams region(String region) {
        this.region = region;
        return this;
    }

    public URLResolverParams perspective(Facing perspective) {
        this.perspective = perspective;
        return this;
    }

    public URLResolverParams resolver(ServiceVersionResolver resolver) {
        this.resolver = resolver;
        return this;
    }

    public ServiceVersionResolver getResolver() {
        return (resolver != null) ? resolver : LatestServiceVersionResolver.INSTANCE;
    }

    public ServiceVersionResolver getV2Resolver() {
        return (resolver != null) ? resolver : StableServiceVersionResolver.INSTANCE;
    }

}
