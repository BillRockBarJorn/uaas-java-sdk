package com.heredata.comm;


import com.heredata.ResponseMessage;

public abstract class RetryStrategy {

    private static final int DEFAULT_RETRY_PAUSE_SCALE = 300; // milliseconds.

    public abstract boolean shouldRetry(Exception ex, RequestMessage request, ResponseMessage response, int retries);

    public long getPauseDelay(int retries) {
        // make the pause time increase exponentially
        // based on an assumption that the more times it retries,
        // the less probability it succeeds.
        int scale = DEFAULT_RETRY_PAUSE_SCALE;
        long delay = (long) Math.pow(2, retries) * scale;

        return delay;
    }
}
