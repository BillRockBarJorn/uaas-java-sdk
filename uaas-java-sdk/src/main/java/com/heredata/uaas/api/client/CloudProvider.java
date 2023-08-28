package com.heredata.uaas.api.client;

/**
 * Cloud Provider helps OpenStack4j handle provider specific changes or workarounds.  Users who know the provider
 * they are authenticating with should set this for more reliability
 *
 */
public enum CloudProvider {
    UNKNOWN,
    RACKSPACE,
    HPCLOUD
}
