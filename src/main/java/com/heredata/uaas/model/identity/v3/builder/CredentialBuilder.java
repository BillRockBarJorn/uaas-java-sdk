package com.heredata.uaas.model.identity.v3.builder;


import com.heredata.uaas.common.Buildable.Builder;
import com.heredata.uaas.model.identity.v3.Credential;

import java.util.Map;

public interface CredentialBuilder extends Builder<CredentialBuilder, Credential> {

    /**
     * @see Credential#getId()
     */
    CredentialBuilder id(String id);

    /**
     * @see Credential#getUserId()
     */
    CredentialBuilder userId(String userId);

    /**
     * @see Credential#getProjectId()
     */
    CredentialBuilder projectId(String projectId);

    /**
     * @see Credential#getType()
     */
    CredentialBuilder type(String type);

    /**
     * @see Credential#getBlob()
     */
    CredentialBuilder blob(String blob);

    /**
     * @see Credential#getLinks()
     */
    CredentialBuilder links(Map<String, String> links);

}
