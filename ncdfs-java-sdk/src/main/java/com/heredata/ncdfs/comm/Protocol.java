package com.heredata.ncdfs.comm;

/**
 * Represents the communication protocol to use when sending requests to HOS, we
 * use HTTPS by default.
 */
public enum Protocol {

    HTTP("http"),

    HTTPS("https");

    private final String protocol;

    private Protocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String toString() {
        return protocol;
    }
}
