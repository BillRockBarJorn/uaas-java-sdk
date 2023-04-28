package com.heredata.hos.auth;

import com.heredata.exception.InvalidCredentialsException;

/**
 * Default implementation of {@link CredentialsProvider}.
 */
public class DefaultCredentialProvider implements CredentialsProvider {

    private volatile Credentials creds;

    public DefaultCredentialProvider(Credentials creds) {
        setCredentials(creds);
    }

    public DefaultCredentialProvider(String accessKeyId, String secretAccessKey) {
        this(accessKeyId, secretAccessKey, null);
    }

    public DefaultCredentialProvider(String accessKey, String secretKey, String account) {
        checkCredentials(accessKey, secretKey, account);
        setCredentials(new DefaultCredentials(accessKey, secretKey, account));
    }

    @Override
    public synchronized void setCredentials(Credentials creds) {
        if (creds == null) {
            throw new InvalidCredentialsException("creds should not be null.");
        }

        checkCredentials(creds.getAccessKey(), creds.getSecretKey(), creds.getAccount());
        this.creds = creds;
    }

    @Override
    public Credentials getCredentials() {
        if (this.creds == null) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        return this.creds;
    }

    private static void checkCredentials(String accessKey, String secretKey, String account) {
        if (accessKey == null || accessKey.equals("")) {
            throw new InvalidCredentialsException("Access key should not be null or empty.");
        }

        if (secretKey == null || secretKey.equals("")) {
            throw new InvalidCredentialsException("Secret key should not be null or empty.");
        }

        if (account == null || account.equals("")) {
            throw new InvalidCredentialsException("account should not be null or empty.");
        }
    }

}
