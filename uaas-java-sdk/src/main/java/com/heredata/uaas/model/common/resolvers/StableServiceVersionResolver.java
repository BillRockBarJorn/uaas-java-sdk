package com.heredata.uaas.model.common.resolvers;


import com.heredata.uaas.api.types.ServiceType;
import com.heredata.uaas.model.identity.v3.Service;

import java.util.SortedSet;

/**
 * Resolves each service to the lowest version which we consider most stable and
 * tested
 *
 * @author Jeremy Unruh
 */
public final class StableServiceVersionResolver implements ServiceVersionResolver {

    public static final StableServiceVersionResolver INSTANCE = new StableServiceVersionResolver();

    private StableServiceVersionResolver() {
    }

    @Override
    public Service resolveV3(ServiceType type, SortedSet<? extends Service> services) {
        return services.first();
    }
}
