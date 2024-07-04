package com.heredata.ncdfs.auth;

/**
 * Provides access to credentials used for accessing HOS, these credentials are
 * used to securely sign requests to HOS.
 */
public interface Credentials {
    /**
     * Returns the access key ID for this credentials.
     */
    public String getToken();
}
