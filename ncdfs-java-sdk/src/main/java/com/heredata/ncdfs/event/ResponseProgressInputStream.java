package com.heredata.ncdfs.event;

import java.io.InputStream;

import static com.heredata.ncdfs.event.ProgressPublisher.publishResponseBytesTransferred;


class ResponseProgressInputStream extends ProgressInputStream {

    public ResponseProgressInputStream(InputStream is, ProgressListener listener) {
        super(is, listener);
    }

    @Override
    protected void onEOF() {
        onNotifyBytesRead();
    }

    @Override
    protected void onNotifyBytesRead() {
        publishResponseBytesTransferred(getListener(), getUnnotifiedByteCount());
    }
}
