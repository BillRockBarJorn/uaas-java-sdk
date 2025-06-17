package com.heredata.uaas.api;

import com.heredata.uaas.api.exceptions.RegionEndpointNotFoundException;
import com.heredata.uaas.api.identity.v3.IdentityService;
import com.heredata.uaas.api.types.Facing;
import com.heredata.uaas.api.types.ServiceType;
import com.heredata.uaas.model.identity.v3.Token;

import java.util.Map;
import java.util.Set;

/**
 * A client which has been identified. Any calls spawned from this session will
 * automatically utilize the original authentication that was successfully
 * validated and authorized
 *
 * @author wuzz
 */
public interface OSClient< T extends OSClient<T>> {

    /**
     * Specifies the region that should be used for further invocations with
     * this client. If the region is invalid or doesn't exists execution errors
     * will occur when invoking API calls and a
     * {@link RegionEndpointNotFoundException} will be thrown
     *
     * @param region the region to use
     * @return OSClient for method chaining
     */
    T useRegion(String region);

    /**
     * Removes the current region making all calls no longer resolving to region
     * (if originally set otherwise no-op)
     *
     * @return OSClient for method chaining
     */
    T removeRegion();

    /**
     * Changes the Perspective for the current Session (Client)
     *
     * @param perspective the new perspective
     * @return OSClient for method chaining
     */
    T perspective(Facing perspective);

    /**
     * Passes the Headers for the current Session(Client)
     *
     * @param headers the headers to use for keystone tokenless
     * @return OSClient for method chaining
     */
    T headers(Map<String, ? extends Object> headers);

    /**
     * Gets the supported services. A set of ServiceTypes will be returned
     * identifying the OpenStack services installed and supported
     *
     * @return the supported services
     */
    Set<ServiceType> getSupportedServices();

    /**
     * Determines if the Compute (Nova) service is supported
     *
     * @return true, if supports compute
     */
    boolean supportsCompute();

    /**
     * Determines if the Identity (Keystone) service is supported
     *
     * @return true, if supports identity
     */
    boolean supportsIdentity();

    /**
     * Determines if the Network (Neutron) service is supported
     *
     * @return true, if supports network
     */
    boolean supportsNetwork();

    /**
     * Determines if the Image (Glance) service is supported
     *
     * @return true, if supports image
     */
    boolean supportsImage();

    /**
     * Determines if the Orchestration (Heat) service is supported
     *
     * @return true if supports Heat
     */
    boolean supportsHeat();

    /**
     * Determines if the App Catalog (Murano) service is supported
     *
     * @return true if supports Murano
     */
    boolean supportsMurano();

    /**
     * Determines if the Block Storage (Cinder) service is supported
     *
     * @return true if supports Block Storage
     */
    boolean supportsBlockStorage();

    /**
     * Determines if the Object Storage (Swift) service is supported
     *
     * @return true if supports Object Storage
     */
    boolean supportsObjectStorage();

    /**
     * Determines if the Telemetry (Ceilometer) service is supported
     *
     * @return true if supports Telemetry
     */
    boolean supportsTelemetry();

    /**
     * Determines if the Shared File Systems (Manila) service is supported
     *
     * @return true if supports Shared File Systems
     */
    boolean supportsShare();

    /**
     * Gets the current endpoint of the Identity service
     *
     * @return the endpoint
     */
    String getEndpoint();

    /**
     * OpenStack4j Client which authenticates against version V3
     */
    public interface OSClientV3 extends OSClient<OSClientV3> {

        /**
         * Gets the token that was assigned during authorization
         *
         * @return the authentication token
         */
        Token getToken();

        /**
         * Returns the Identity Service API V3
         *
         * @return the identity service version 3
         */
        IdentityService identity();
    }
}
