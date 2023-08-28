package com.heredata.uaas.model.identity.v3;


import com.heredata.uaas.common.Buildable;
import com.heredata.uaas.model.ModelEntity;
import com.heredata.uaas.model.identity.v3.builder.DomainBuilder;

import java.util.Map;

/**
 * Domain model.
 *
 * @see <a href="http://developer.openstack.org/api-ref-identity-v3.html#domains-v3">API reference</a>
 */
public interface Domain extends ModelEntity, Buildable<DomainBuilder> {

    /**
     * Globally unique domain identifier across all domains.
     *
     * @return the Id of the domain
     */
    String getId();

    /**
     * @return the Description of the domain
     */
    String getDescription();

    /**
     * @return the Name of the domain
     */
    String getName();

    /**
     * @return the Links of the domain
     */
    Map<String, String> getLinks();

    /**
     * @return if domain is enabled
     */
    boolean isEnabled();

}
