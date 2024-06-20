package com.heredata.handler;

import com.heredata.ResponseMessage;
import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;

public interface ResponseHandler<T> {

    void handle(ResponseMessage response) throws ServiceException, ClientException;


    void handle(ResponseMessage response, Class<T> responseClass) throws ServiceException, ClientException;

}
