package com.heredata.swift.handler;

import com.heredata.ResponseMessage;
import com.heredata.exception.ClientException;
import com.heredata.exception.ExceptionFactory;
import com.heredata.exception.ResponseParseException;
import com.heredata.exception.ServiceException;
import com.heredata.handler.ResponseHandler;
import com.heredata.model.ErrorResult;
import com.heredata.parser.JAXBResponseParser;
import com.heredata.swift.SwiftErrorCode;
import org.apache.http.HttpStatus;

import static com.heredata.utils.ResourceUtils.safeCloseResponse;


/**
 * Used to handle error response from SWIFT, when HTTP status code is not 2xx,
 * then throws <code>SwiftException</code> with detailed error information(such as
 * request id, error code).
 */
public class SwiftErrorResponseHandler<T> implements ResponseHandler<T> {
    @Override
    public void handle(ResponseMessage response) throws ServiceException, ClientException {

        if (response.isSuccessful()) {
            return;
        }

        String requestId = response.getRequestId();
        Integer statusCode = response.getStatusCode();
        if (statusCode!=null) {
            if (response.getContent() == null) {
                /**
                 * When HTTP response body is null, handle status code 404 Not
                 * Found, 304 Not Modified, 412 Precondition Failed especially.
                 */
                if (statusCode == HttpStatus.SC_NOT_FOUND) {
                    throw ExceptionFactory.createServiceException(requestId, SwiftErrorCode.NO_SUCH_KEY, "Not Found");
                } else if (statusCode == HttpStatus.SC_NOT_MODIFIED) {
                    throw ExceptionFactory.createServiceException(requestId, SwiftErrorCode.NOT_MODIFIED, "Not Modified");
                } else if (statusCode == HttpStatus.SC_PRECONDITION_FAILED) {
                    throw ExceptionFactory.createServiceException(requestId, SwiftErrorCode.PRECONDITION_FAILED,
                            "Precondition Failed");
                } else if (statusCode == HttpStatus.SC_FORBIDDEN) {
                    throw ExceptionFactory.createServiceException(requestId, SwiftErrorCode.ACCESS_FORBIDDEN, "AccessForbidden");
                } else {
                    throw ExceptionFactory.createUnknownException(requestId, statusCode);
                }
            }
        }

        JAXBResponseParser parser = new JAXBResponseParser(ErrorResult.class);
        try {
            ErrorResult errorResult = (ErrorResult) parser.parse(response);
            throw ExceptionFactory.createServiceException(errorResult, response.getErrorResponseAsString());
        } catch (ResponseParseException e) {
            throw ExceptionFactory.createServiceException(requestId, response.getErrorResponseAsString(), e);
        } finally {
            safeCloseResponse(response);
        }
    }

    @Override
    public void handle(ResponseMessage response, Class<T> responseClass) throws ServiceException, ClientException {

    }
}
