package com.heredata.uaas.model.identity.v3.builder;


import com.heredata.uaas.common.Buildable;
import com.heredata.uaas.model.identity.v3.Domain;

import java.util.Map;

/**
 * A Builder which creates a identity v3 domain.
 *
 *
 */
public interface DomainBuilder extends Buildable.Builder<DomainBuilder, Domain> {

    /**
     * @see Domain#getId()
     */
    DomainBuilder id(String id);

    /**
     * @see Domain#getDescription()
     */
    DomainBuilder description(String description);

    /**
     * @see Domain#getName()
     */
    DomainBuilder name(String name);

    /**
     * @see Domain#getLinks()
     */
    DomainBuilder links(Map<String, String> links);

    /**
     * @see Domain#isEnabled()
     */
    DomainBuilder enabled(boolean enabled);

}
