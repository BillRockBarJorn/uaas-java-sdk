package com.heredata.exception;

import com.heredata.ClientErrorCode;
import com.heredata.model.ErrorResult;
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

import static com.heredata.utils.ResourceUtils.COMMON_RESOURCE_MANAGER;

/**
 * <p>Title: ExceptionFactory</p>
 * <p>Description: 异常工厂类，用来创建服务端异常类 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 15:34
 */
public class ExceptionFactory {

    public static ClientException createNetworkException(IOException ex) {
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
                return new ClientException(cause.getMessage(), errorCode, requestId, cause);
            }
        }

        return new ClientException(ex.getMessage(), errorCode, requestId, ex);
    }

    public static ServiceException createServiceException(String requestId, String errorCode, String message) {
        return new ServiceException(message, errorCode, requestId);
    }

    public static ServiceException createServiceException(String requestId, String rawResponseError,
                                                          Throwable cause) {
        String errorMessage = COMMON_RESOURCE_MANAGER.getFormattedString("FailedToParseResponse", cause.getMessage());

        ServiceException serviceException = new ServiceException();
        serviceException.setRequestId(requestId);
        serviceException.setRawResponseError(rawResponseError);
        serviceException.setErrorMessage(errorMessage);
        return serviceException;
    }

    public static ServiceException createUnknownException(String requestId, int statusCode) {
        String message = "No body in response, http status code " + statusCode;
        return new ServiceException(message, ClientErrorCode.UNKNOWN, requestId);
    }

    public static ServiceException createServiceException(String errorCode, String errorMessage) {
        ServiceException serviceException = new ServiceException();
        serviceException.setErrorCode(errorCode);
        serviceException.setErrorMessage(errorMessage);
        return serviceException;
    }

    public static ServiceException createServiceException(ErrorResult errorResult, String rawResponseError) {
        return new ServiceException(errorResult.Message, errorResult.Code, errorResult.RequestId, errorResult.HostId,
                errorResult.Header, errorResult.ResourceType, errorResult.Method, rawResponseError, null);
    }

}
