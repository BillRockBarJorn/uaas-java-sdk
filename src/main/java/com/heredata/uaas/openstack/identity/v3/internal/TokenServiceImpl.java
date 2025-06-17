package com.heredata.uaas.openstack.identity.v3.internal;


import com.heredata.uaas.api.identity.v3.TokenService;
import com.heredata.uaas.model.common.ActionResponse;
import com.heredata.uaas.model.identity.v3.Domain;
import com.heredata.uaas.model.identity.v3.Project;
import com.heredata.uaas.model.identity.v3.Service;
import com.heredata.uaas.model.identity.v3.Token;
import com.heredata.uaas.openstack.common.AccessKey;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneAccessKey;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneAuth;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneDomain.Domains;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneProject.Projects;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneService.Catalog;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneToken;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneToken.TokenS3;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.heredata.uaas.core.transport.ClientConstants.*;

public class TokenServiceImpl extends BaseIdentityServices implements TokenService {

    @Override
    public Token get(KeystoneAuth keystoneAuth) {
        KeystoneToken execute = post(KeystoneToken.class, PATH_TOKENS).entity(keystoneAuth).execute();
        execute.setId(execute.getHead().getxSubjectToken());
        execute.applyContext(execute.getEndpoint(), keystoneAuth);
        return execute;
    }

    @Override
    public Token get(String tokenId) {
        checkNotNull(tokenId);
        return get(KeystoneToken.class, PATH_TOKENS).header(HEADER_X_SUBJECT_TOKEN, tokenId).execute();
    }

    @Override
    public ActionResponse check(String tokenId) {
        checkNotNull(tokenId);
        return get(ActionResponse.class, PATH_TOKENS).header(HEADER_X_SUBJECT_TOKEN, tokenId).execute();
    }

    @Override
    public ActionResponse delete(String tokenId) {
        checkNotNull(tokenId);
        return deleteWithResponse(PATH_TOKENS).header(HEADER_X_SUBJECT_TOKEN, tokenId).execute();
    }

    @Override
    public ActionResponse deleteS3Key(String tokenId, String accessKey) {
        checkNotNull(tokenId);
        checkNotNull(accessKey);
        return delete(ActionResponse.class, uri(PATH_S3KEY))
                .header(HEADER_X_AUTH_TOKEN, tokenId).header(HEADER_X_ACCESS_KEY, accessKey)
                .execute();
    }

    @Override
    public List<? extends Service> getServiceCatalog(String tokenId) {
        checkNotNull(tokenId);
        return get(Catalog.class, uri(PATH_SERVICE_CATALOGS)).header(HEADER_X_SUBJECT_TOKEN, tokenId).execute().getList();
    }

    @Override
    public List<? extends Project> getProjectScopes(String tokenId) {
        checkNotNull(tokenId);
        return get(Projects.class, uri(PATH_PROJECT_SCOPES)).header(HEADER_X_SUBJECT_TOKEN, tokenId).execute().getList();
    }

    @Override
    public List<? extends Domain> getDomainScopes(String tokenId) {
        checkNotNull(tokenId);
        return get(Domains.class, uri(PATH_DOMAIN_SCOPES)).header(HEADER_X_SUBJECT_TOKEN, tokenId).execute().getList();
    }

    @Override
    public TokenS3 createS3Keys(KeystoneAuth keystoneAuth) {
        TokenS3 execute = post(TokenS3.class, PATH_TOKENS).header(AUTH_S3_REQUIRED, "Y").entity(keystoneAuth).execute();
        execute.getKeystoneToken().setId(execute.getKeystoneToken().getHead().getxSubjectToken());
        return execute;
    }

    @Override
    public TokenS3 getS3SecretKey(String tokenId, String accessKey) {
        checkNotNull(tokenId);
        checkNotNull(accessKey);
        return get(TokenS3.class, uri(PATH_S3KEY)).header(HEADER_X_AUTH_TOKEN, tokenId).header(HEADER_X_ACCESS_KEY, accessKey).execute();
    }

    @Override
    public ActionResponse createPublicKey(String tokenId) {
        checkNotNull(tokenId);
        return post(ActionResponse.class, uri(PATH_PUBLUC_KEY)).header(HEADER_X_AUTH_TOKEN, tokenId).execute();
    }

    @Override
    public AccessKey createAccessKey(String userName, String projectId, String description) {
        KeystoneAccessKey keystoneAccessKey = new KeystoneAccessKey(userName, projectId, description);
        return post(AccessKey.class, PATH_ACCESSKEY)
                .header("content-type", CONTENT_TYPE_JSON)
                .header("Accept", CONTENT_TYPE_JSON)
                .entity(keystoneAccessKey)
                .execute();
    }
}
