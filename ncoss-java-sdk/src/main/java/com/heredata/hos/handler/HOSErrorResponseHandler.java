package com.heredata.hos.handler;

import com.heredata.ResponseMessage;
import com.heredata.exception.ClientException;
import com.heredata.exception.ExceptionFactory;
import com.heredata.exception.ResponseParseException;
import com.heredata.exception.ServiceException;
import com.heredata.handler.ResponseHandler;
import com.heredata.hos.HOSErrorCode;
import com.heredata.model.ErrorResult;
import com.heredata.parser.JAXBResponseParser;
import com.heredata.parser.ResponseParser;
import org.apache.http.HttpStatus;

import static com.heredata.hos.comm.HOSHeaders.HOS_HEADER_REQUEST_ID;
import static com.heredata.utils.ResourceUtils.safeCloseResponse;


/**
 * Used to handle error response from HOS, when HTTP status code is not 2xx,
 * then throws <code>ServiceException</code> with detailed error information(such as
 * request id, error code).
 */
public class HOSErrorResponseHandler implements ResponseHandler {

    @Override
    public void handle(ResponseMessage response, Class c) throws ServiceException, ClientException {

    }

    @Override
    public void handle(ResponseMessage response) throws ServiceException, ClientException {

        if (response.isSuccessful()) {
            return;
        }

        String requestId = response.getHeaders().get(HOS_HEADER_REQUEST_ID);
        Integer statusCode = response.getStatusCode();
        if (response.getContent() == null && statusCode != null) {
            /**
             * When HTTP response body is null, handle status code 404 Not
             * Found, 304 Not Modified, 412 Precondition Failed especially.
             */
            if (statusCode == HttpStatus.SC_NOT_FOUND) {
                throw ExceptionFactory.createServiceException(requestId, statusCode + "", HOSErrorCode.NO_SUCH_KEY);
            } else if (statusCode == HttpStatus.SC_NOT_MODIFIED) {
                throw ExceptionFactory.createServiceException(requestId, statusCode + "", HOSErrorCode.NOT_MODIFIED);
            } else if (statusCode == HttpStatus.SC_PRECONDITION_FAILED) {
                throw ExceptionFactory.createServiceException(requestId, statusCode + "", HOSErrorCode.PRECONDITION_FAILED);
            } else if (statusCode == HttpStatus.SC_FORBIDDEN) {
                throw ExceptionFactory.createServiceException(requestId, statusCode + "", HOSErrorCode.ACCESS_FORBIDDEN);
            } else {
                throw ExceptionFactory.createUnknownException(requestId, statusCode);
            }
        }
        ResponseParser parser = null;
        parser = new JAXBResponseParser(ErrorResult.class);

        try {
            ErrorResult errorResult = (ErrorResult) parser.parse(response);
            throw ExceptionFactory.createServiceException(errorResult, response.getErrorResponseAsString());
        } catch (ResponseParseException e) {
            throw ExceptionFactory.createServiceException(requestId, response.getErrorResponseAsString(), e);
        } finally {
            safeCloseResponse(response);
        }
    }

}
