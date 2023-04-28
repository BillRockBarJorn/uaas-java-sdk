package com.heredata.swift.auth;

/**
 * Provides access to credentials used for accessing HOS, these credentials are
 * used to securely sign requests to HOS.
 */
public interface Credentials {
    /**
     * returns the account's token
     * @return
     */
    public String getToken();

    /**
     * Returns the account for this credentials.
     */
    public String getAccount();

    /**
     * Determines whether to use account for http requests.
     */
    public boolean useAccount();
}
