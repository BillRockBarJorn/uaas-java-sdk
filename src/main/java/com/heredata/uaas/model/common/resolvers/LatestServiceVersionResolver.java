package com.heredata.uaas.model.common.resolvers;


import com.heredata.uaas.api.types.ServiceType;
import com.heredata.uaas.model.identity.v3.Service;

import java.util.SortedSet;

/**
 * Resolves the service version to the latest version found within the Service Catalog
 *
 * @author wuzz
 */
public final class LatestServiceVersionResolver implements ServiceVersionResolver {

    public static final LatestServiceVersionResolver INSTANCE = new LatestServiceVersionResolver();

    private LatestServiceVersionResolver() {

    }

    @Override
    public Service resolveV3(ServiceType type, SortedSet<? extends Service> services) {
        return services.last();
    }

}
