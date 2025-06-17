package com.heredata.uaas.model.identity.v3;


import com.heredata.uaas.common.Buildable;
import com.heredata.uaas.model.ModelEntity;
import com.heredata.uaas.model.identity.v3.builder.ProjectBuilder;

import java.util.Map;

/**
 * Project Model class use to group/isolate resources and/or identity objects
 *
 * @see <a href="http://developer.openstack.org/api-ref-identity-v3.html#projects-v3">API reference</a>
 */
public interface Project extends ModelEntity, Buildable<ProjectBuilder> {

    /**
     * Globally unique within the owning domain.
     *
     * @return the Id of the project
     */
    String getId();

    /**
     * @return the DomainId of the project
     */
    Domain getDomain();

    /**
     * @return the DomainId of the project
     */
    String getDomainId();

    /**
     * @return the Description of the project
     */
    String getDescription();

    /**
     *
     * @return the Name of the project
     */
    String getName();

    /**
     *
     * @return the links of the project
     */
    Map<String, String> getLinks();

    /**
     *
     * @return the parentId of the project
     */
    String getParentId();

    /**
     *
     * @return if the project is enabled
     */
    boolean isEnabled();

    /**
     *
     * @return value for the given key
     */
    String getExtra(String key);
}
