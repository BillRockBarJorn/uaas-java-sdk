package com.heredata.uaas.api.identity.v3;


import com.heredata.uaas.common.RestService;
import com.heredata.uaas.model.common.ActionResponse;
import com.heredata.uaas.model.identity.v3.Policy;

import java.util.List;

public interface PolicyService extends RestService {

    /**
     * Create a policy
     *
     * @param policy the policy
     * @return the newly created policy
     */
    Policy create(Policy policy);

    /**
     * Create a policy
     *
     * @param blob the policy rule itself as a serialized blob
     * @param type the MIME media type of the serialized policy blob
     * @param projectId the uuid for the associated project
     * @param userId the id of the user who owns the policy
     * @return the newly created policy
     */
    Policy create(String blob, String type, String projectId, String userId);

    /**
     * Get detailed information on a policy by id
     *
     * @param policyId the policy id
     * @return the policy
     */
    Policy get(String policyId);

    /**
     * Update a policy
     *
     * @param policy the policy set to update
     * @return the updated policy
     */
    Policy update(Policy policy);

    /**
     * Delete a policy
     *
     * @param policyId the policy id
     * @return  the ActionResponse
     */
    ActionResponse delete(String policyId);

    /**
     * @return list of policies
     */
    List<? extends Policy> list();

}
