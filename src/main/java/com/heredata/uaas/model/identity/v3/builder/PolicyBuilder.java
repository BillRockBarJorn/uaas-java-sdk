package com.heredata.uaas.model.identity.v3.builder;


import com.heredata.uaas.common.Buildable.Builder;
import com.heredata.uaas.model.identity.v3.Policy;

import java.util.Map;

/**
 *
 * the policy builder
 *
 */
public interface PolicyBuilder extends Builder<PolicyBuilder, Policy> {

    /**
     * @see Policy#getId()
     */
    PolicyBuilder id(String id);

    /**
     * @see Policy#getType()
     */
    PolicyBuilder type(String type);

    /**
     * @see Policy#getBlob()
     */
    PolicyBuilder blob(String blob);

    /**
     * @see Policy#getLinks()
     */
    PolicyBuilder links(Map<String, String> links);

    /**
     * @see Policy#getProjectId()
     */
    PolicyBuilder projectId(String projectId);

    /**
     * @see Policy#getUserId()
     */
    PolicyBuilder userId(String userId);

}
