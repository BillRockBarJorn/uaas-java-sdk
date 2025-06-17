package com.heredata.uaas.model.identity.v3;


import com.heredata.uaas.common.Buildable;
import com.heredata.uaas.model.ModelEntity;
import com.heredata.uaas.model.identity.v3.builder.ServiceBuilder;

import java.util.List;
import java.util.Map;

/**
 * Identity V3 Service model
 *
 * @see <a href="http://developer.openstack.org/api-ref-identity-v3.html#service-catalog-v3"> API reference</a>
 */
public interface Service extends ModelEntity, Buildable<ServiceBuilder>, Comparable<Service> {

    /**
     * @return the version of the service
     */
    Integer getVersion();

    /**
     * @return the id of the service
     */
    String getId();

    /**
     * @return the description of the service
     */
    String getDescription();

    /**
     * @return the name of the service
     */
    String getName();

    /**
     * @return the type of the service
     */
    String getType();

    /**
     * @return the links of the service
     */
    Map<String, String> getLinks();

    /**
     * @return the list of endpoints
     */
    List<? extends Endpoint> getEndpoints();

    /**
     * @return the enabled status of the service
     */
    boolean isEnabled();

    /**
     * sets the enabled status of the service
     *
     * @param enabled the enabled
     */
    void setEnabled(Boolean enabled);

}
