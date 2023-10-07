package com.heredata.event;


import static com.heredata.event.ProgressEventType.*;

public class ProgressPublisher {

    public static void publishProgress(final ProgressListener listener, final ProgressEventType eventType) {
        if (listener == ProgressListener.NOOP || listener == null || eventType == null) {
            return;
        }
        listener.progressChanged(new ProgressEvent(eventType));
    }

    public static void publishSelectProgress(final ProgressListener listener, final ProgressEventType eventType,
                                             final long scannedBytes) {
        if (listener == ProgressListener.NOOP || listener == null || eventType == null) {
            return;
        }
        listener.progressChanged(new ProgressEvent(eventType, scannedBytes));
    }

    public static void publishRequestContentLength(final ProgressListener listener, final long bytes) {
        publishByteCountEvent(listener, REQUEST_CONTENT_LENGTH_EVENT, bytes);
    }

    public static void publishRequestBytesTransferred(final ProgressListener listener, final long bytes) {
        publishByteCountEvent(listener, REQUEST_BYTE_TRANSFER_EVENT, bytes);
    }

    public static void publishResponseContentLength(final ProgressListener listener, final long bytes) {
        publishByteCountEvent(listener, RESPONSE_CONTENT_LENGTH_EVENT, bytes);
    }

    public static void publishResponseBytesTransferred(final ProgressListener listener, final long bytes) {
        publishByteCountEvent(listener, RESPONSE_BYTE_TRANSFER_EVENT, bytes);
    }

    private static void publishByteCountEvent(final ProgressListener listener, final ProgressEventType eventType,
            final long bytes) {
        if (listener == ProgressListener.NOOP || listener == null || bytes <= 0) {
            return;
        }
        listener.progressChanged(new ProgressEvent(eventType, bytes));
    }
}
