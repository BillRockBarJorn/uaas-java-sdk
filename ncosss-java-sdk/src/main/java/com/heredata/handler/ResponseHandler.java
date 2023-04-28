package com.heredata.handler;

import com.heredata.ResponseMessage;
import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import org.apache.poi.ss.formula.functions.T;

public interface ResponseHandler {

    void handle(ResponseMessage response) throws ServiceException, ClientException;


    void handle(ResponseMessage response, Class<T> responseClass) throws ServiceException, ClientException;

}
