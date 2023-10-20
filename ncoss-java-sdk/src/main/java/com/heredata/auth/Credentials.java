package com.heredata.auth;

/**
 * Provides access to credentials used for accessing HOS, these credentials are
 * used to securely sign requests to HOS.
 */
public interface Credentials {
    /**
     * Returns the access key ID for this credentials.
     */
    public String getAccessKey();

    /**
     * Returns the secret access key for this credentials.
     */
    public String getSecretKey();

    /**
     * Returns the account for this credentials.
     */
    public String getAccount();

    /**
     * Determines whether to use account for http requests.
     */
    public boolean useAccount();

    public String getAccountId();

    /**
     * returns the account's token
     * @return
     */
    public String getToken();
}
