package com.heredata.swift.auth;


import com.heredata.auth.Credentials;
import com.heredata.auth.CredentialsProvider;
import com.heredata.exception.InvalidCredentialsException;

/**
 * Default implementation of {@link CredentialsProvider}.
 */
public class DefaultCredentialProvider implements CredentialsProvider {

    private volatile Credentials creds;

    public DefaultCredentialProvider(Credentials creds) {
        setCredentials(creds);
    }

    public DefaultCredentialProvider(String token, String account) {
        checkCredentials(token, account);
        setCredentials(new DefaultCredentials(token, account));
    }

    @Override
    public synchronized void setCredentials(Credentials creds) {
        if (creds == null) {
            throw new InvalidCredentialsException("creds should not be null.");
        }

        checkCredentials(creds.getToken(), creds.getAccount());
        this.creds = creds;
    }

    @Override
    public Credentials getCredentials() {
        if (this.creds == null) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        return this.creds;
    }

    private static void checkCredentials(String token, String account) {
        if (token == null || token.equals("")) {
            throw new InvalidCredentialsException("token should not be null or empty.");
        }

        if (account == null || account.equals("")) {
            throw new InvalidCredentialsException("account should not be null or empty.");
        }
    }

}
