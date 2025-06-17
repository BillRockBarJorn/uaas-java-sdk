package com.heredata.uaas.openstack.client;


import com.heredata.uaas.api.OSClient.OSClientV3;
import com.heredata.uaas.api.client.CloudProvider;
import com.heredata.uaas.api.client.IOSClientBuilder;
import com.heredata.uaas.api.exceptions.AuthenticationException;
import com.heredata.uaas.api.types.Facing;
import com.heredata.uaas.core.transport.Config;
import com.heredata.uaas.model.common.Identifier;
import com.heredata.uaas.openstack.common.Auth;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneAuth;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneAuth.AuthScope;
import com.heredata.uaas.openstack.internal.OSAuthenticator;

/**
 * Builder definitions for creating a Client
 *
 * @author wuzz
 *
 */
public abstract class OSClientBuilder<R, T extends IOSClientBuilder<R, T>> implements IOSClientBuilder<R, T> {

    Config config;
    String endpoint;
    String user;
    String password;
    Facing perspective;
    CloudProvider provider = CloudProvider.UNKNOWN;

    @SuppressWarnings("unchecked")
    @Override
    public T withConfig(Config config) {
        this.config = config;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T provider(CloudProvider provider) {
        this.provider = provider;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T credentials(String user, String password) {
        this.user = user;
        this.password = password;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T endpoint(String endpoint) {
        this.endpoint = endpoint;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T perspective(Facing perspective) {
        this.perspective = perspective;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T useNonStrictSSLClient(boolean useNonStrictSSL) {
        if (config == null) {
            config = Config.newConfig().withSSLVerificationDisabled();
        }
        return (T) this;
    }

    public static class ClientV3 extends OSClientBuilder<OSClientV3, V3> implements V3 {

        Identifier domain;
        AuthScope scope;
        String tokenId;

        @Override
        public ClientV3 domainName(String domainName) {
            this.domain = Identifier.byName(domainName);
            return this;
        }

        @Override
        public ClientV3 domainId(String domainId) {
            this.domain = Identifier.byId(domainId);
            return this;
        }

        @Override
        public ClientV3 credentials(String user, String password, Identifier domain) {
            this.user = user;
            this.password = password;
            this.domain = domain;
            return this;
        }

        @Override
        public ClientV3 token(String tokenId) {
            this.tokenId = tokenId;
            return this;
        }

        @Override
        public OSClientV3 authenticate() throws AuthenticationException {
            // token based authentication
            if (tokenId != null && tokenId.length() > 0) {
                if (scope != null) {
                    return (OSClientV3) OSAuthenticator.invoke(new KeystoneAuth(tokenId, scope), endpoint, perspective, config, provider);
                } else {
                    return (OSClientV3) OSAuthenticator.invoke(new KeystoneAuth(tokenId), endpoint, perspective, config, provider);
                }
            }
            // credential based authentication
            if (user != null && user.length() > 0) {
                return (OSClientV3) OSAuthenticator.invoke(new KeystoneAuth(user, password, domain, scope), endpoint, perspective, config, provider);
            }
            // Use tokenless auth finally
            return (OSClientV3) OSAuthenticator.invoke(new KeystoneAuth(scope, Auth.Type.TOKENLESS), endpoint, perspective, config, provider);
        }

        @Override
        public ClientV3 scopeToProject(Identifier project, Identifier domain) {
            this.scope = AuthScope.project(project, domain);
            return this;
        }

        @Override
        public ClientV3 scopeToProject(Identifier project) {
            this.scope = AuthScope.project(project);
            return this;
        }

        @Override
        public ClientV3 scopeToDomain(Identifier domain) {
            this.scope = AuthScope.domain(domain);
            return this;
        }

    }
}
