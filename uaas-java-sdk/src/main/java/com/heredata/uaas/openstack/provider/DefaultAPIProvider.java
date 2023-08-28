package com.heredata.uaas.openstack.provider;

import com.google.common.collect.Maps;
import com.heredata.uaas.api.APIProvider;
import com.heredata.uaas.api.exceptions.ApiNotFoundException;
import com.heredata.uaas.api.identity.v3.*;
import com.heredata.uaas.openstack.identity.v3.internal.*;

import java.util.Map;

/**
 * Simple API Provider which keeps internally Maps interface implementations as singletons
 *
 * @author Jeremy Unruh
 */
public class DefaultAPIProvider implements APIProvider {

    private static final Map<Class<?>, Class<?>> bindings = Maps.newHashMap();
    private static final Map<Class<?>, Object> instances = Maps.newConcurrentMap();

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        bind(IdentityService.class, IdentityServiceImpl.class);
        bind(IdentityService.class, IdentityServiceImpl.class);
        bind(ServiceEndpointService.class, ServiceEndpointServiceImpl.class);
        bind(CredentialService.class, CredentialServiceImpl.class);
        bind(UserService.class, UserServiceImpl.class);
        bind(ProjectService.class, ProjectServiceImpl.class);
        bind(RoleService.class, RoleServiceImpl.class);
        bind(DomainService.class, DomainServiceImpl.class);
        bind(GroupService.class, GroupServiceImpl.class);
        bind(PolicyService.class, PolicyServiceImpl.class);
        bind(RegionService.class, RegionServiceImpl.class);
        bind(TokenService.class, TokenServiceImpl.class);
        bind(GroupService.class, GroupServiceImpl.class);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Class<T> api) {
        if (instances.containsKey(api)) {
            return (T) instances.get(api);
        }

        if (bindings.containsKey(api)) {
            try {
                T impl = (T) bindings.get(api).newInstance();
                instances.put(api, impl);
                return impl;
            } catch (Exception e) {
                throw new ApiNotFoundException("API Not found for: " + api.getName(), e);
            }
        }
        throw new ApiNotFoundException("API Not found for: " + api.getName());
    }

    private void bind(Class<?> api, Class<?> impl) {
        bindings.put(api, impl);
    }
}
