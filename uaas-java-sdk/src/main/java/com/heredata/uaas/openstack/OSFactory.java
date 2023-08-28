package com.heredata.uaas.openstack;

import com.heredata.uaas.api.OSClient.OSClientV3;
import com.heredata.uaas.api.client.CloudProvider;
import com.heredata.uaas.api.client.IOSClientBuilder;
import com.heredata.uaas.api.types.Facing;
import com.heredata.uaas.core.transport.Config;
import com.heredata.uaas.core.transport.internal.HttpLoggingFilter;
import com.heredata.uaas.model.common.Identifier;
import com.heredata.uaas.model.identity.v3.Token;
import com.heredata.uaas.openstack.client.OSClientBuilder;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneAuth;
import com.heredata.uaas.openstack.internal.OSClientSession.OSClientSessionV3;

/**
 * A Factory which sets up the APIs to be used a previously non-expired authorization or new authorization.
 *
 * @author Jeremy Unruh
 */
public abstract class OSFactory<T extends OSFactory<T>> {

    private OSFactory() {
    }

    /**
     * Skips Authentication and created the API around a previously cached Token object.  This can be useful in multi-threaded environments
     * or scenarios where a client should not be re-authenticated due to a token lasting 24 hours.
     *
     * @param token an authorized token entity which is to be used to create the API
     * @return the OSClient
     */
    public static OSClientV3 clientFromToken(Token token, String userName, String password, String accountName) {
        // 根据token创建clientSession
        OSClientSessionV3 session = OSClientSessionV3.createSession(token);
        // 构建授权对象。需要传入用户名账户和密码以及租户名称
        KeystoneAuth.AuthIdentity authIdentity = KeystoneAuth.AuthIdentity.createCredentialType(userName, password);
        KeystoneAuth.AuthScope scope = KeystoneAuth.AuthScope.project(new Identifier(Identifier.Type.NAME, accountName));
        KeystoneAuth keystoneAuth = new KeystoneAuth(authIdentity, scope);

        Token tokenServer = session.identity().tokens().get(keystoneAuth);
        // 重新设置token的重要属性
        tokenServer.setEndpoint(token.getEndpoint());
        return OSClientSessionV3.createSession(tokenServer);
    }

    /**
     * Skips Authentication and created the API around a previously cached Token object.  This can be useful in multi-threaded environments
     * or scenarios where a client should not be re-authenticated due to a token lasting 24 hours
     *
     * @param token an authorized token entity which is to be used to create the API
     * @param perspective the current endpoint perspective to use
     * @return the OSClient
     */
    public static OSClientV3 clientFromToken(Token token, Facing perspective) {
        return OSClientSessionV3.createSession(token, perspective, null, null);
    }

    /**
     * Skips Authentication and created the API around a previously cached Token object.  This can be useful in multi-threaded environments
     * or scenarios where a client should not be re-authenticated due to a token lasting 24 hours
     *
     * @param token an authorized token entity which is to be used to create the API
     * @param config OpenStack4j configuration options
     * @return the OSClient
     */
    public static OSClientV3 clientFromToken(Token token, Config config) {
        return OSClientSessionV3.createSession(token, null, null, config);
    }

    /**
     * Skips Authentication and created the API around a previously cached Token object.  This can be useful in multi-threaded environments
     * or scenarios where a client should not be re-authenticated due to a token lasting 24 hours
     *
     * @param token an authorized token entity which is to be used to create the API
     * @param perspective the current endpoint perspective to use
     * @param config OpenStack4j configuration options
     * @return the OSClient
     */
    public static OSClientV3 clientFromToken(Token token, Facing perspective, Config config) {
        return OSClientSessionV3.createSession(token, perspective, null, config);
    }

    /**
     * Skips Authentication and created the API around a previously cached Token object.  This can be useful in multi-threaded environments
     * or scenarios where a client should not be re-authenticated due to a token lasting 24 hours
     *
     * @param token an authorized token entity which is to be used to create the API
     * @param perspective the current endpoint perspective to use
     * @param provider the cloud provider
     * @param config OpenStack4j configuration options
     * @return the OSClient
     */
    public static OSClientV3 clientFromToken(Token token, Facing perspective, CloudProvider provider, Config config) {
        return OSClientSessionV3.createSession(token, perspective, provider, config);
    }

    /**
     * Globally enables or disables verbose HTTP Request and Response logging useful for debugging
     * @param enabled true to enable, false to enable
     */
    public static void enableHttpLoggingFilter(boolean enabled) {
        System.getProperties().setProperty(HttpLoggingFilter.class.getName(), String.valueOf(enabled));
    }

    /**
     * Creates builder for OpenStack V3 based authentication
     * @return V3 Authentication builder
     */
    public static IOSClientBuilder.V3 builderV3() {
        return new OSClientBuilder.ClientV3();
    }
}
