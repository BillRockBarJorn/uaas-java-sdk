package com.heredata.uaas.model.identity.v3;


import com.heredata.uaas.common.Buildable;
import com.heredata.uaas.model.ModelEntity;
import com.heredata.uaas.model.identity.v3.builder.PolicyBuilder;

import java.util.Map;

/**
 * policy model class
 *
 * @see <a href="http://developer.openstack.org/api-ref-identity-v3.html#policies-v3">API reference</a>
 */
public interface Policy extends ModelEntity, Buildable<PolicyBuilder> {

    /**
     * the unique identifier
     *
     * @return the id of the policy
     */
    String getId();

    /**
     * @return the uuid for the associated project
     */
    String getProjectId();

    /**
     * @return the id of the user who owns the policy
     */
    String getUserId();

    /**
     * @return the type of the policy
     */
    String getType();

    /**
     * @return the BLOB of the policy
     */
    String getBlob();

    /**
     * @return the links of the policy
     */
    Map<String, String> getLinks();

}
