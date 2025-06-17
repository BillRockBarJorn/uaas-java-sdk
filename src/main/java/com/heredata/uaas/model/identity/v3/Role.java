package com.heredata.uaas.model.identity.v3;


import com.heredata.uaas.common.Buildable;
import com.heredata.uaas.model.ModelEntity;
import com.heredata.uaas.model.identity.v3.builder.RoleBuilder;

import java.util.Map;

/**
 * identity v3 role model class
 *
 * @see <a href="http://developer.openstack.org/api-ref-identity-v3.html#roles-v3">API reference</a>
 */
public interface Role extends ModelEntity, Buildable<RoleBuilder> {

    /**
     * Globally unique across all domains.
     *
     * @return the id of the role
     */
    String getId();

    /**
     * @return the name of the role
     */
    String getName();

    /**
     * @return the links of the role
     */
    Map<String, String> getLinks();

    /**
     * @return the domain id of the role
     */
    String getDomainId();

}
