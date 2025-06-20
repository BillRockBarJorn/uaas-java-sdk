package com.heredata.uaas.openstack.identity.v3.internal;


import com.heredata.uaas.api.identity.v3.CredentialService;
import com.heredata.uaas.model.common.ActionResponse;
import com.heredata.uaas.model.identity.v3.Credential;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneCredential;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.heredata.uaas.core.transport.ClientConstants.PATH_CREDENTIALS;

public class CredentialServiceImpl extends BaseIdentityServices implements CredentialService {

    @Override
    public Credential create(Credential credential) {
        checkNotNull(credential);
        return post(KeystoneCredential.class, uri(PATH_CREDENTIALS)).entity(credential).execute();
    }

    @Override
    public Credential create(String blob, String type, String projectId, String userId) {
        checkNotNull(blob);
        checkNotNull(type);
        checkNotNull(projectId);
        checkNotNull(userId);
        return create(KeystoneCredential.builder().blob(blob).type(type).projectId(projectId).userId(userId).build());
    }

    @Override
    public Credential get(String credentialId) {
        checkNotNull(credentialId);
        return get(KeystoneCredential.class, PATH_CREDENTIALS, "/", credentialId).execute();
    }

    @Override
    public Credential update(Credential credential) {
        checkNotNull(credential);
        return patch(KeystoneCredential.class, PATH_CREDENTIALS, "/", credential.getId()).entity(credential).execute();
    }

    @Override
    public ActionResponse delete(String credentialId) {
        checkNotNull(credentialId);
        return deleteWithResponse(PATH_CREDENTIALS, "/", credentialId).execute();
    }

    @Override
    public List<? extends Credential> list() {
        return get(KeystoneCredential.Credentials.class, uri(PATH_CREDENTIALS)).execute().getList();
    }

}
