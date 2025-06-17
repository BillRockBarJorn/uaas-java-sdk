package com.heredata.uaas.model.identity.v3.builder;


import com.heredata.uaas.common.Buildable.Builder;
import com.heredata.uaas.model.identity.v3.Role;

import java.util.Map;

/**
 * A Builder which creates an identity v3 role
 *
 *
 */
public interface RoleBuilder extends Builder<RoleBuilder, Role> {

    /**
     * @see Role#getId()
     */
    RoleBuilder id(String id);

    /**
     * @see Role#getName()
     */
    RoleBuilder name(String name);

    /**
     * @see Role#getLinks()
     */
    RoleBuilder links(Map<String, String> links);

    /**
     * @see Role#getLinks()
     */
    RoleBuilder domainId(String domainId);

}
