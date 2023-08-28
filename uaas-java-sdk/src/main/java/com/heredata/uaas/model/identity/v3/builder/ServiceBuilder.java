package com.heredata.uaas.model.identity.v3.builder;


import com.heredata.uaas.common.Buildable;
import com.heredata.uaas.model.identity.v3.Service;

import java.util.Map;

/**
 * A Builder which creates an identity v3 service
 *
 *
 */
public interface ServiceBuilder extends Buildable.Builder<ServiceBuilder, Service> {

    /**
     * @see Service#getId()
     */
    ServiceBuilder id(String id);

    /**
     * @see Service#getDescription()
     */
    ServiceBuilder description(String description);

    /**
     * @see Service#getType()
     */
    ServiceBuilder type(String type);

    /**
     * @see Service#getName()
     */
    ServiceBuilder name(String name);

    /**
     * @see Service#getLinks()
     */
    ServiceBuilder links(Map<String, String> links);

    /**
     * @see Service#getVersion()
     */
    ServiceBuilder version(Integer version);

    /**
     * @see Service#isEnabled()
     */
    ServiceBuilder enabled(boolean enabled);

}
