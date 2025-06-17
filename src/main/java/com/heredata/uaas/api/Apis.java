package com.heredata.uaas.api;


import java.util.ServiceLoader;

/**
 * Provides access to the Major APIs and Buildables
 *
 * @author wuzz
 */
public class Apis {

    private static final APIProvider provider = initializeProvider();

    /**
     * Gets the API implementation based on Type
     *
     * @param <T>
     *            the API type
     * @param api
     *            the API implementation
     * @return the API implementation
     */
    public static <T> T get(Class<T> api) {
        return provider.get(api);
    }

    /**
     * Gets the identity v3 services API
     *
     * @return the identity v3 services
     */
    public static com.heredata.uaas.api.identity.v3.IdentityService getIdentityV3Services() {
        return get(com.heredata.uaas.api.identity.v3.IdentityService.class);
    }

    private static APIProvider initializeProvider() {
        // No need to check for emptiness as there is default implementation registered
        APIProvider p = ServiceLoader.load(APIProvider.class).iterator().next();
        p.initialize();
        return p;
    }
}
