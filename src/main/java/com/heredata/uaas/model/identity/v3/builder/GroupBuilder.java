package com.heredata.uaas.model.identity.v3.builder;


import com.heredata.uaas.common.Buildable.Builder;
import com.heredata.uaas.model.identity.v3.Group;

import java.util.Map;

/**
 * A Builder which creates a identity v3 group
 *
 *
 */
public interface GroupBuilder extends Builder<GroupBuilder, Group> {

    /**
     * @see Group#getId()
     */
    GroupBuilder id(String id);

    /**
     * @see Group#getName()
     */
    GroupBuilder name(String name);

    /**
     * @see Group#getId()
     */
    GroupBuilder description(String description);

    /**
     * @see Group#getDomainId()
     */
    GroupBuilder domainId(String domainId);

    /**
     * @see Group#getLinks()
     */
    GroupBuilder links(Map<String, String> links);

}
