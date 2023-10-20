package com.heredata.hos.auth;

import com.heredata.auth.Credentials;
import com.heredata.exception.InvalidCredentialsException;

/**
 * Default implementation of {@link Credentials}.
 */
public class DefaultCredentials implements Credentials {

    private final String accessKey;
    private final String secretKey;
    private final String account;
    private final String accountId;

    public DefaultCredentials(String accessKeyId, String secretAccessKey) {
        this(accessKeyId, secretAccessKey, null);
    }

    public DefaultCredentials(String accessKey, String secretKey, String accountId) {
        if (accessKey == null || accessKey.equals("")) {
            throw new InvalidCredentialsException("Access key should not be null or empty.");
        }
        if (secretKey == null || secretKey.equals("")) {
            throw new InvalidCredentialsException("Secret key should not be null or empty.");
        }

        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.accountId = accountId;
        this.account = "HOS_" + accountId;
    }

    @Override
    public String getAccessKey() {
        return accessKey;
    }

    @Override
    public String getSecretKey() {
        return secretKey;
    }

    @Override
    public String getAccount() {
        return account;
    }

    @Override
    public boolean useAccount() {
        return this.account != null;
    }

    @Override
    public String getAccountId() {
        return accountId;
    }

    @Override
    public String getToken() {
        throw new RuntimeException("DefaultCredentials hasn't token.");
    }
}
