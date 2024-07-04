package com.heredata.ncdfs.utils;

import com.heredata.ncdfs.ClientErrorCode;
import com.heredata.ncdfs.NCDFSErrorCode;
import com.heredata.ncdfs.exception.NCDFSClientException;
import com.heredata.ncdfs.exception.NCDFSException;
import com.heredata.ncdfs.internal.NCDFSErrorResult;
import com.heredata.ncdfs.internal.NCDFSUtils;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.NonRepeatableRequestException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;


/**
 * A simple factory used for creating instances of <code>NCDFSClientException</code>
 * and <code>NCDFSException</code>.
 */
public class ExceptionFactory {

    public static NCDFSClientException createNetworkException(IOException ex) {
        String requestId = "Unknown";
        String errorCode = ClientErrorCode.UNKNOWN;

        if (ex instanceof SocketTimeoutException) {
            errorCode = ClientErrorCode.SOCKET_TIMEOUT;
        } else if (ex instanceof SocketException) {
            errorCode = ClientErrorCode.SOCKET_EXCEPTION;
        } else if (ex instanceof ConnectTimeoutException) {
            errorCode = ClientErrorCode.CONNECTION_TIMEOUT;
        } else if (ex instanceof UnknownHostException) {
            errorCode = ClientErrorCode.UNKNOWN_HOST;
        } else if (ex instanceof HttpHostConnectException) {
            errorCode = ClientErrorCode.CONNECTION_REFUSED;
        } else if (ex instanceof NoHttpResponseException) {
            errorCode = ClientErrorCode.CONNECTION_TIMEOUT;
        } else if (ex instanceof SSLException) {
            errorCode = ClientErrorCode.SSL_EXCEPTION;
        } else if (ex instanceof ClientProtocolException) {
            Throwable cause = ex.getCause();
            if (cause instanceof NonRepeatableRequestException) {
                errorCode = ClientErrorCode.NONREPEATABLE_REQUEST;
                return new NCDFSClientException(cause.getMessage(), errorCode, requestId, cause);
            }
        }

        return new NCDFSClientException(ex.getMessage(), errorCode, requestId, ex);
    }

    public static NCDFSException createInvalidResponseException(Throwable cause) {
        return createInvalidResponseException(
                NCDFSUtils.COMMON_RESOURCE_MANAGER.getFormattedString("FailedToParseResponse", cause.getMessage()));
    }

    public static NCDFSException createInvalidResponseException(String rawResponseError,
                                                                Throwable cause) {
        return createInvalidResponseException(
                NCDFSUtils.COMMON_RESOURCE_MANAGER.getFormattedString("FailedToParseResponse", cause.getMessage()),
                rawResponseError);
    }

    public static NCDFSException createInvalidResponseException(String message) {
        return createNCDFSException(NCDFSErrorCode.INVALID_RESPONSE, message);
    }

    public static NCDFSException createInvalidResponseException(String message,
                                                                String rawResponseError) {
        return createNCDFSException(NCDFSErrorCode.INVALID_RESPONSE, message, rawResponseError);
    }

    public static NCDFSException createNCDFSException(NCDFSErrorResult errorResult) {
        return createNCDFSException(errorResult, null);
    }

    public static NCDFSException createNCDFSException(NCDFSErrorResult errorResult, String rawResponseError) {
        return new NCDFSException(errorResult.Message, errorResult.Code,
                errorResult.Header, errorResult.ResourceType, errorResult.Method, rawResponseError);
    }

    public static NCDFSException createNCDFSException(String errorCode, String message) {
        return new NCDFSException(message, errorCode, null, null, null);
    }

    public static NCDFSException createNCDFSException(String errorCode, String message,
                                                      String rawResponseError) {
        return new NCDFSException(message, errorCode, null, null, null, rawResponseError);
    }

    public static NCDFSException createUnknownNCDFSException(int statusCode) {
        String message = "No body in response, http status code " + Integer.toString(statusCode);
        return new NCDFSException(message, ClientErrorCode.UNKNOWN, null, null, null);
    }
}
