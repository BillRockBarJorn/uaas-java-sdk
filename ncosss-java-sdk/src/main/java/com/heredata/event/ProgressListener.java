package com.heredata.event;

/**
 * 进度监听器接口
 */
public interface ProgressListener {
    public static final ProgressListener NOOP = new ProgressListener() {
        @Override
        public void progressChanged(ProgressEvent progressEvent) {
        }
    };

    public void progressChanged(ProgressEvent progressEvent);
}
