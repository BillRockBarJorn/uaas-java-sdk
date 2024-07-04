package com.heredata.ncdfs;

import com.heredata.ncdfs.exception.NCDFSClientException;
import com.heredata.ncdfs.exception.NCDFSException;

public interface ResponseHandler {

    public void handle(ResponseMessage response) throws NCDFSException, NCDFSClientException;

}
