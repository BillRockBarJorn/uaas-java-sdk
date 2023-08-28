package com.heredata.uaas.openstack.identity.internal;

import com.google.common.base.Optional;
import com.heredata.uaas.api.exceptions.RegionEndpointNotFoundException;
import com.heredata.uaas.api.identity.EndpointURLResolver;
import com.heredata.uaas.api.types.Facing;
import com.heredata.uaas.api.types.ServiceType;
import com.heredata.uaas.model.identity.URLResolverParams;
import com.heredata.uaas.model.identity.v3.Endpoint;
import com.heredata.uaas.model.identity.v3.Service;
import com.heredata.uaas.model.identity.v3.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves an Endpoint URL based on the Service Type and Facing perspective
 *
 * @author Jeremy Unruh
 */
public class DefaultEndpointURLResolver implements EndpointURLResolver {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultEndpointURLResolver.class);
    private static final Map<Key, String> CACHE = new ConcurrentHashMap<Key, String>();
    private static boolean LEGACY_EP_HANDLING = Boolean.getBoolean(LEGACY_EP_RESOLVING_PROP);
    private String publicHostIP;

    /**
     * 计算公共统一路径前缀
     * @param p
     * @return
     */
    @Override
    public String findURLV3(URLResolverParams p) {

        if (p.type == null) {
            return p.token.getEndpoint();
        }

        Key key = Key.of(p.token.getCacheIdentifier(), p.type, p.perspective, p.region);

        String url = CACHE.get(key);

        if (url != null) {
            return url;
        }
        url = resolveV3(p);

        if (url != null) {
            CACHE.put(key, url);
            return url;
        } else if (p.region != null) {
            throw RegionEndpointNotFoundException.create(p.region, p.type.getServiceName());
        }
        return p.token.getEndpoint();
    }

    private String resolveV3(URLResolverParams p) {
        Token token = p.token;

        //in v3 api, if user has no default project, and token is unscoped, no catalog will be returned
        //then if service is Identity service, should directly return the endpoint back
        if (token.getCatalog() == null) {
            if (ServiceType.IDENTITY.equals(p.type)) {
                return token.getEndpoint();
            } else {
                return null;
            }
        }

        for (Service service : token.getCatalog()) {
            if (p.type == ServiceType.forName(service.getType()) || p.type == ServiceType.forName(service.getName())) {
                if (p.perspective == null) {
                    p.perspective = Facing.PUBLIC;
                }
                for (Endpoint ep : service.getEndpoints()) {
                    if (matches(ep, p)) {
                        return ep.getUrl().toString();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns <code>true</code> for any endpoint that matches a given
     * {@link URLResolverParams}.
     *
     * @param endpoint
     * @param p
     * @return
     */
    private boolean matches(Endpoint endpoint, URLResolverParams p) {
        boolean matches = endpoint.getIface() == p.perspective;
        if (Optional.fromNullable(p.region).isPresent()) {
            matches &= endpoint.getRegion().equals(p.region);
        }
        return matches;
    }

    private static final class Key {
        private final String uid;
        private final ServiceType type;
        private final Facing perspective;

        private Key(String uid, ServiceType type, Facing perspective) {
            this.type = type;
            this.perspective = perspective;
            this.uid = uid;
        }

        static Key of(String uid, ServiceType type, Facing perspective, String region) {
            return new Key((region == null) ? uid : uid + region, type, perspective);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((perspective == null) ? 0 : perspective.hashCode());
            result = prime * result + ((type == null) ? 0 : type.hashCode());
            result = prime * result + ((uid == null) ? 0 : uid.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            if (perspective != other.perspective) {
                return false;
            }
            if (type != other.type) {
                return false;
            }
            if (uid == null) {
                if (other.uid != null) {
                    return false;
                }
            } else if (!uid.equals(other.uid)) {
                return false;
            }
            return true;
        }
    }
}
