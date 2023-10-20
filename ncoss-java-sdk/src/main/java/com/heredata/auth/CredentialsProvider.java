package com.heredata.auth;

/**
 * Abstract credentials provider that maintains only one user credentials. Users
 * can switch to other valid credentials with
 * that <b>implementations of this interface must be thread-safe.</b>
 */
public interface CredentialsProvider {
    public void setCredentials(Credentials creds);

    public Credentials getCredentials();
}
