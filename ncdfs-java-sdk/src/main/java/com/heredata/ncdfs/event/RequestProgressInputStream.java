package com.heredata.ncdfs.event;

import java.io.InputStream;

import static com.heredata.ncdfs.event.ProgressPublisher.publishRequestBytesTransferred;


class RequestProgressInputStream extends ProgressInputStream {

    public RequestProgressInputStream(InputStream is, ProgressListener listener) {
        super(is, listener);
    }

    @Override
    protected void onEOF() {
        onNotifyBytesRead();
    }

    @Override
    protected void onNotifyBytesRead() {
        publishRequestBytesTransferred(getListener(), getUnnotifiedByteCount());
    }
}
