package com.heredata.uaas.model.identity.v3.builder;


import com.heredata.uaas.common.Buildable;
import com.heredata.uaas.model.identity.v3.Domain;
import com.heredata.uaas.model.identity.v3.Project;

import java.util.Map;

/**
 * A Builder which creates a identity v3 project
 *
 *
 */
public interface ProjectBuilder extends Buildable.Builder<ProjectBuilder, Project> {

    /**
     * @see Project#getId()
     */
    ProjectBuilder id(String id);

    /**
     *
     * @see Project#getDomainId()
     */
    ProjectBuilder domainId(String domainId);

    /**
     * Accepts an existing domain and uses its id
     *
     * @see Project#getDomainId()
     */
    ProjectBuilder domain(Domain domain);

    /**
     *
     * @see Project#getDescription
     */
    ProjectBuilder description(String description);

    /**
     * @see Project#isEnabled()
     */
    ProjectBuilder enabled(Boolean enabled);

    /**
     * @see Project#getName()
     */
    ProjectBuilder name(String name);

    /**
     *
     * @see Project#getLinks()
     */
    ProjectBuilder links(Map<String, String> links);

    /**
     *
     * @see Project#getParentId()
     */
    ProjectBuilder parentId(String parentId);

    /**
     *
     * @see Project#getExtra(String)
     */
    ProjectBuilder setExtra(String name, String value);

}
