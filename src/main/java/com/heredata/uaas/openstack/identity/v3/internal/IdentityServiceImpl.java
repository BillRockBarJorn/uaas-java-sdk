package com.heredata.uaas.openstack.identity.v3.internal;


import com.heredata.uaas.api.Apis;
import com.heredata.uaas.api.identity.v3.*;
import com.heredata.uaas.model.common.Extension;
import com.heredata.uaas.openstack.common.ExtensionValue.ExtensionList;

import java.util.List;

import static com.heredata.uaas.core.transport.ClientConstants.PATH_EXTENSIONS;


/**
 * Identity V3 service implementation
 *
 */
public class IdentityServiceImpl extends BaseIdentityServices implements IdentityService {

    @Override
    public CredentialService credentials() {
        return Apis.get(CredentialService.class);
    }

    @Override
    public DomainService domains() {
        return Apis.get(DomainService.class);
    }

    @Override
    public ProjectService projects() {
        return Apis.get(ProjectService.class);
    }

    @Override
    public UserService users() {
        return Apis.get(UserService.class);
    }

    @Override
    public RoleService roles() {
        return Apis.get(RoleService.class);
    }

    @Override
    public GroupService groups() {
        return Apis.get(GroupService.class);
    }

    @Override
    public PolicyService policies() {
        return Apis.get(PolicyService.class);
    }

    @Override
    public ServiceEndpointService serviceEndpoints() {
        return Apis.get(ServiceEndpointService.class);
    }

    @Override
    public RegionService regions() {
        return Apis.get(RegionService.class);
    }

    @Override
    public TokenService tokens() {
        return Apis.get(TokenService.class);
    }

    @Override
    public List<? extends Extension> listExtensions() {
        return get(ExtensionList.class, PATH_EXTENSIONS).execute().getList();
    }

}
