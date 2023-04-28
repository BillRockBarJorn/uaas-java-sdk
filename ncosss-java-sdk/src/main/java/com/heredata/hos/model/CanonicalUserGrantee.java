package com.heredata.hos.model;

/**
 * The group of grantees that could be granted permission as a whole.
 */
public class CanonicalUserGrantee implements Grantee {

    private String userId;

    public CanonicalUserGrantee(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * The group's Id.
     */
    @Override
    public String getIdentifier() {
        return this.userId;
    }
}
