package com.heredata.swift.auth;


import com.heredata.auth.Credentials;
import com.heredata.exception.InvalidCredentialsException;

/**
 * Default implementation of {@link Credentials}.
 */
public class DefaultCredentials implements Credentials {

    private final String token;
    private final String account;


    public DefaultCredentials(String token, String account) {
        if (token == null || token.equals("")) {
            throw new InvalidCredentialsException("token should not be null or empty.");
        }

        if (account == null || account.equals("")) {
            throw new InvalidCredentialsException("account should not be null or empty.");
        }
        this.token = token;
        this.account = account;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String getAccessKey() {
        return null;
    }

    @Override
    public String getSecretKey() {
        return null;
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
        return this.account;
    }
}
