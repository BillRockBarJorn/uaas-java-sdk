package com.heredata.uaas.openstack.internal;

import com.heredata.uaas.api.OSClient;
import com.heredata.uaas.api.client.CloudProvider;
import com.heredata.uaas.api.types.Facing;
import com.heredata.uaas.core.transport.*;
import com.heredata.uaas.core.transport.internal.HttpExecutor;
import com.heredata.uaas.model.identity.AuthStore;
import com.heredata.uaas.model.identity.v3.Authentication;
import com.heredata.uaas.model.identity.v3.Token;
import com.heredata.uaas.openstack.common.Auth.Type;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneAuth;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneToken;
import com.heredata.uaas.openstack.identity.v3.domain.TokenAuth;
import com.heredata.uaas.openstack.internal.OSClientSession.OSClientSessionV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.heredata.uaas.api.exceptions.ResponseException.mapException;


/**
 * Responsible for authenticating and re-authenticating sessions for V2 and V3
 * of the Identity API
 */
public class OSAuthenticator {

    private static final String TOKEN_INDICATOR = "Tokens";
    private static final Logger LOG = LoggerFactory.getLogger(OSAuthenticator.class);

    /**
     * Invokes authentication to obtain a valid V3 Token, throws an
     * UnsupportedOperationException for an V2 attempt.
     *
     * @param auth the authentication credentials
     * @param endpoint the identity endpoint
     * @param perspective the network facing perspective
     * @param config the client configuration
     * @return the OSClient
     */
    @SuppressWarnings("rawtypes")
    public static OSClient invoke(AuthStore auth, String endpoint, Facing perspective, Config config,
                                  CloudProvider provider) {
        SessionInfo info = new SessionInfo(endpoint, perspective, false, provider);
        return authenticateV3((KeystoneAuth) auth, info, config);
    }

    /**
     * Invokes V3 authentication via an existing token
     *
     * @param auth the token authentication
     * @param endpoint the identity endpoint
     * @param perspective the network facing perspective
     * @param config the client configuration
     * @return the OSClient
     */
    @SuppressWarnings("rawtypes")
    public static OSClient invoke(KeystoneAuth auth, String endpoint, Facing perspective, Config config,
                                  CloudProvider provider) {
        SessionInfo info = new SessionInfo(endpoint, perspective, false, provider);
        return authenticateV3(auth, info, config);
    }

    /**
     * Re-authenticates/renews the token for the current Session
     */
    @SuppressWarnings("rawtypes")
    public static void reAuthenticate() {

        LOG.debug("Re-Authenticating session due to expired Token or invalid response");

        OSClientSession session = OSClientSession.getCurrent();

        switch (session.getAuthVersion()) {
            case V3:
            default:
                Token token = ((OSClientSessionV3) session).getToken();
                SessionInfo info = new SessionInfo(token.getEndpoint(), session.getPerspective(), true, session.getProvider());
                authenticateV3((KeystoneAuth) token.getCredentials(), info, session.getConfig());
                break;
        }
    }

    private static OSClient.OSClientV3 authenticateV3(KeystoneAuth auth, SessionInfo info, Config config) {
        if (auth.getType().equals(Type.TOKENLESS)) {
            Map headers = new HashMap();
            Authentication.Scope.Project project = auth.getScope().getProject();
            if (project != null) {
                if (!isEmpty(project.getId())) {
                    headers.put(ClientConstants.HEADER_X_PROJECT_ID, project.getId());
                }
                if (!isEmpty(project.getName())) {
                    headers.put(ClientConstants.HEADER_X_PROJECT_NAME, project.getName());
                }
                Authentication.Scope.Domain domain = project.getDomain();
                if (domain != null) {
                    if (!isEmpty(domain.getId())) {
                        headers.put(ClientConstants.HEADER_X_PROJECT_DOMAIN_ID, domain.getId());
                    }
                    if (!isEmpty(domain.getName())) {
                        headers.put(ClientConstants.HEADER_X_PROJECT_DOMAIN_NAME, domain.getName());
                    }
                }
            } else {
                Authentication.Scope.Domain domain = auth.getScope().getDomain();
                if (domain != null) {
                    if (!isEmpty(domain.getId())) {
                        headers.put(ClientConstants.HEADER_X_DOMAIN_ID, domain.getId());
                    }
                    if (!isEmpty(domain.getName())) {
                        headers.put(ClientConstants.HEADER_X_DOMAIN_NAME, domain.getName());
                    }
                }
            }
            KeystoneToken keystoneToken = new KeystoneToken();
            keystoneToken.setEndpoint(info.endpoint);
            return OSClientSessionV3.createSession(keystoneToken, null, null, config).headers(headers);
        }

        HttpRequest<KeystoneToken> request = HttpRequest.builder(KeystoneToken.class)
                .endpoint(info.endpoint)
                .method(HttpMethod.POST).path("/auth/tokens").config(config).entity(auth).build();

        HttpResponse response = HttpExecutor.create().execute(request);

        if (response.getStatus() >= 400) {
            try {
                throw mapException(response.getStatusMessage(), response.getStatus());
            } finally {
                HttpEntityHandler.closeQuietly(response);
            }
        }
        KeystoneToken token = response.getEntity(KeystoneToken.class);
        token.setId(response.header(ClientConstants.HEADER_X_SUBJECT_TOKEN));

        if (auth.getType().equals(Type.CREDENTIALS)) {
            token = token.applyContext(info.endpoint, auth);
        } else {
            if (token.getProject() != null) {
                token = token.applyContext(info.endpoint, new TokenAuth(token.getId(),
                        auth.getScope().getProject().getName(), auth.getScope().getProject().getId()));

            } else if (token.getDomain() != null) {
                token = token.applyContext(info.endpoint, new TokenAuth(token.getId(),
                        auth.getScope().getDomain().getName(), auth.getScope().getDomain().getId()));
            } else {
                token = token.applyContext(info.endpoint, new TokenAuth(token.getId(), null, null));
            }
        }

        if (!info.reLinkToExistingSession) {
            OSClientSessionV3 v3 = OSClientSessionV3.createSession(token, info.perspective, info.provider, config);
            return v3;
        }

        OSClientSessionV3 current = (OSClientSessionV3) OSClientSessionV3.getCurrent();
        current.token = token;

        return current;
    }

    private static class SessionInfo {
        String endpoint;
        Facing perspective;
        boolean reLinkToExistingSession;
        CloudProvider provider;

        SessionInfo(String endpoint, Facing perspective, boolean reLinkToExistingSession, CloudProvider provider) {
            this.endpoint = endpoint;
            this.perspective = perspective;
            this.reLinkToExistingSession = reLinkToExistingSession;
            this.provider = provider;
        }
    }

    private static boolean isEmpty(String str) {
        if (str != null && str.length() > 0) {
            return false;
        }
        return true;
    }
}
