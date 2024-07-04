package com.heredata.ncdfs.auth;


import com.heredata.ncdfs.comm.RequestMessage;
import com.heredata.ncdfs.exception.NCDFSClientException;

public interface RequestSigner {

    public void sign(RequestMessage request) throws NCDFSClientException;

}
