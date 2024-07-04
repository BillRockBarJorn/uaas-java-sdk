package com.heredata.ncdfs.comm;

import com.heredata.ncdfs.exception.NCDFSClientException;
import com.heredata.ncdfs.exception.NCDFSException;

public interface RequestHandler {

    public void handle(RequestMessage request) throws NCDFSException, NCDFSClientException;

}
