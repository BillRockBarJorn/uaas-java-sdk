package com.heredata.uaas.openstack.internal;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.heredata.uaas.api.Apis;
import com.heredata.uaas.api.EndpointTokenProvider;
import com.heredata.uaas.api.OSClient;
import com.heredata.uaas.api.OSClient.OSClientV3;
import com.heredata.uaas.api.client.CloudProvider;
import com.heredata.uaas.api.identity.EndpointURLResolver;
import com.heredata.uaas.api.identity.v3.IdentityService;
import com.heredata.uaas.api.types.Facing;
import com.heredata.uaas.api.types.ServiceType;
import com.heredata.uaas.core.transport.Config;
import com.heredata.uaas.model.identity.AuthVersion;
import com.heredata.uaas.model.identity.URLResolverParams;
import com.heredata.uaas.model.identity.v3.Token;
import com.heredata.uaas.openstack.identity.internal.DefaultEndpointURLResolver;
import com.heredata.uaas.openstack.identity.v3.functions.ServiceToServiceType;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

/**
 * A client which has been identified. Any calls spawned from this session will
 * automatically utilize the original authentication that was successfully
 * validated and authorized
 *
 * @author Jeremy Unruh
 */
public abstract class OSClientSession<R, T extends OSClient<T>> implements EndpointTokenProvider {

    @SuppressWarnings("rawtypes")
    private static final ThreadLocal<OSClientSession> sessions = new ThreadLocal<OSClientSession>();

    Config config;
    Facing perspective;
    String region;
    Set<ServiceType> supports;
    CloudProvider provider;
    Map<String, ? extends Object> headers;
    EndpointURLResolver fallbackEndpointUrlResolver = new DefaultEndpointURLResolver();

    @SuppressWarnings("rawtypes")
    public static OSClientSession getCurrent() {
        return sessions.get();
    }

    @SuppressWarnings("unchecked")
    @VisibleForTesting
    public R useConfig(Config config) {
        this.config = config;
        return (R) this;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T useRegion(String region) {
        this.region = region;
        return (T) this;
    }

    /**
     * {@inheritDoc}
     */
    public T removeRegion() {
        return useRegion(null);
    }

    /**
     * @return the current perspective
     */
    public Facing getPerspective() {
        return perspective;
    }

    /**
     * @return the original client configuration associated with this session
     */
    public Config getConfig() {
        return config;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public T perspective(Facing perspective) {
        this.perspective = perspective;
        return (T) this;
    }

    public CloudProvider getProvider() {
        return (provider == null) ? CloudProvider.UNKNOWN : provider;
    }

    /**
     * {@inheritDoc}
     */
    public T headers(Map<String, ? extends Object> headers) {
        this.headers = headers;
        return (T) this;
    }

    public Map<String, ? extends Object> getHeaders() {
        return this.headers;
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsIdentity() {
        return getSupportedServices().contains(ServiceType.IDENTITY);
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsNetwork() {
        return getSupportedServices().contains(ServiceType.NETWORK);
    }


    /**
     * {@inheritDoc}
     */
    public boolean supportsHeat() {
        return getSupportedServices().contains(ServiceType.ORCHESTRATION);
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsMurano() {
        return getSupportedServices().contains(ServiceType.APP_CATALOG);
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsBlockStorage() {
        return getSupportedServices().contains(ServiceType.BLOCK_STORAGE);
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsObjectStorage() {
        return getSupportedServices().contains(ServiceType.OBJECT_STORAGE);
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsTelemetry() {
        return getSupportedServices().contains(ServiceType.TELEMETRY);
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsTelemetry_aodh() {
        return getSupportedServices().contains(ServiceType.TELEMETRY_AODH);
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsShare() {
        return getSupportedServices().contains(ServiceType.SHARE);
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsTrove() {
        return getSupportedServices().contains(ServiceType.DATABASE);
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsDNS() {
        return getSupportedServices().contains(ServiceType.DNS);
    }

    public Set<ServiceType> getSupportedServices() {
        return null;
    }

    public AuthVersion getAuthVersion() {
        return null;
    }

    public static class OSClientSessionV3 extends OSClientSession<OSClientSessionV3, OSClientV3> implements OSClientV3 {

        Token token;

        private OSClientSessionV3(Token token, String endpoint, Facing perspective, CloudProvider provider, Config config) {
            this.token = token;
            this.config = config;
            this.perspective = perspective;
            this.provider = provider;
            sessions.set(this);
        }

        private OSClientSessionV3(Token token, OSClientSessionV3 parent, String region) {
            this.token = parent.token;
            this.perspective = parent.perspective;
            this.region = region;
        }

        public static OSClientSessionV3 createSession(Token token) {
            return new OSClientSessionV3(token, token.getEndpoint(), null, null, null);
        }

        public static OSClientSessionV3 createSession(Token token, Facing perspective, CloudProvider provider, Config config) {
            return new OSClientSessionV3(token, token.getEndpoint(), perspective, provider, config);
        }

        @Override
        public Token getToken() {
            return token;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getEndpoint() {
            return token.getEndpoint();
        }

        @Override
        public AuthVersion getAuthVersion() {
            return AuthVersion.V3;
        }

        private String addNATIfApplicable(String url) {
            if (config != null && config.isBehindNAT()) {
                try {
                    URI uri = new URI(url);
                    return url.replace(uri.getHost(), config.getEndpointNATResolution());
                } catch (URISyntaxException e) {
                    LoggerFactory.getLogger(OSClientSessionV3.class).error(e.getMessage(), e);
                }
            }
            return url;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getEndpoint(ServiceType service) {

            final EndpointURLResolver eUrlResolver = (config != null && config.getEndpointURLResolver() != null)
                    ? config.getEndpointURLResolver() : fallbackEndpointUrlResolver;

            return addNATIfApplicable(
                    eUrlResolver.findURLV3(
                            URLResolverParams.create(token, service).resolver(config != null ? config.getResolver() : null)
                                    .perspective(perspective).region(region)
                    )
            );
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getTokenId() {
            return token.getId();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public IdentityService identity() {
            return Apis.getIdentityV3Services();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Set<ServiceType> getSupportedServices() {
            if (supports == null) {
                try {
                    supports = Sets.immutableEnumSet(Iterables.transform(token.getCatalog(),
                            new ServiceToServiceType()));
                } catch (NullPointerException e) {
                    throw new RuntimeException(" token's catalogs property is empty!");
                }
            }
            return supports;
        }

        @Override
        public boolean supportsCompute() {
            return getSupportedServices().contains(ServiceType.COMPUTE);
        }

        @Override
        public boolean supportsImage() {
            return getSupportedServices().contains(ServiceType.IMAGE);
        }
    }
}
