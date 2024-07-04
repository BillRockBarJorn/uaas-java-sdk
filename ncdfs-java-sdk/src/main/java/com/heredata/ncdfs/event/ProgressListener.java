package com.heredata.ncdfs.event;


public interface ProgressListener {
    public static final ProgressListener NOOP = new ProgressListener() {
        @Override
        public void progressChanged(ProgressEvent progressEvent) {
        }
    };

    public void progressChanged(ProgressEvent progressEvent);
}
