package com.heredata.hos.handler;

import com.heredata.ResponseMessage;
import com.heredata.exception.ClientException;
import com.heredata.exception.ExceptionFactory;
import com.heredata.exception.ResponseParseException;
import com.heredata.exception.ServiceException;
import com.heredata.handler.ResponseHandler;
import com.heredata.model.ErrorResult;
import com.heredata.parser.JAXBResponseParser;
import org.apache.http.HttpStatus;

import static com.heredata.utils.ResourceUtils.safeCloseResponse;

/**
 * <p>Title: HOSCallbackErrorResponseHandler</p>
 * <p>Description: TODO </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:12
 */
public class HOSCallbackErrorResponseHandler<T> implements ResponseHandler<T> {

    @Override
    public void handle(ResponseMessage response, Class<T> responseClass) throws ServiceException, ClientException {

    }

    @Override
    public void handle(ResponseMessage response) throws ServiceException, ClientException {
        Integer statusCode = response.getStatusCode();
        if (statusCode!=null) {
            if (statusCode == HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION) {
                JAXBResponseParser parser = new JAXBResponseParser(ErrorResult.class);
                try {
                    ErrorResult errorResult = (ErrorResult) parser.parse(response);
                throw ExceptionFactory.createServiceException(errorResult, response.getErrorResponseAsString());
                } catch (ResponseParseException e) {
                    throw ExceptionFactory.createServiceException(response.getRequestId(), response.getErrorResponseAsString(), e);
                } finally {
                    safeCloseResponse(response);
                }
            }
        }
    }

}
