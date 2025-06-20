package com.heredata.uaas.openstack.identity.v3.internal;


import com.heredata.uaas.api.identity.v3.PolicyService;
import com.heredata.uaas.model.common.ActionResponse;
import com.heredata.uaas.model.identity.v3.Policy;
import com.heredata.uaas.openstack.identity.v3.domain.KeystonePolicy;
import com.heredata.uaas.openstack.identity.v3.domain.KeystonePolicy.Policies;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.heredata.uaas.core.transport.ClientConstants.PATH_POLICIES;

public class PolicyServiceImpl extends BaseIdentityServices implements PolicyService {

    @Override
    public Policy create(Policy policy) {
        checkNotNull(policy);
        return post(KeystonePolicy.class, uri(PATH_POLICIES)).entity(policy).execute();
    }

    @Override
    public Policy create(String blob, String type, String projectId, String userId) {
        checkNotNull(blob);
        checkNotNull(type);
        checkNotNull(projectId);
        checkNotNull(userId);
        return create(KeystonePolicy.builder().blob(blob).type(type).projectId(projectId).userId(userId).build());
    }

    @Override
    public Policy get(String policyId) {
        checkNotNull(policyId);
        return get(KeystonePolicy.class, PATH_POLICIES, "/", policyId).execute();
    }

    @Override
    public Policy update(Policy policy) {
        checkNotNull(policy);
        return patch(KeystonePolicy.class, PATH_POLICIES, "/", policy.getId()).entity(policy).execute();
    }

    @Override
    public ActionResponse delete(String policyId) {
        checkNotNull(policyId);
        return deleteWithResponse(PATH_POLICIES, "/", policyId).execute();
    }

    @Override
    public List<? extends Policy> list() {
        return get(Policies.class, uri(PATH_POLICIES)).execute().getList();
    }

}
