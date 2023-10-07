package com.heredata.ncdfs.utils;

import com.heredata.utils.ResourceManager;

import java.net.URI;
import java.net.URISyntaxException;

import static com.heredata.ncdfs.common.NCDFSConstants.RESOURCE_NAME_NCDFS;


public class NCDFSUtils {

    public static final ResourceManager NCDFS_RESOURCE_MANAGER = ResourceManager.getInstance(RESOURCE_NAME_NCDFS);

    private static final String ENDPOINT_REGEX = "^[a-zA-Z0-9._-]+$";

    /**
     * Validate endpoint.
     */
    public static boolean validateEndpoint(String endpoint) {
        if (endpoint == null) {
            return false;
        }
        return endpoint.matches(ENDPOINT_REGEX);
    }

    public static void ensureEndpointValid(String endpoint) {
        if (!validateEndpoint(endpoint)) {
            throw new IllegalArgumentException(
                    NCDFS_RESOURCE_MANAGER.getFormattedString("EndpointInvalid", endpoint));
        }
    }

    public static URI toEndpointURI(String endpoint, String defaultProtocol) throws IllegalArgumentException {
        if (endpoint != null && !endpoint.contains("://")) {
            endpoint = defaultProtocol + "://" + endpoint;
        }

        try {
            return new URI(endpoint);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
