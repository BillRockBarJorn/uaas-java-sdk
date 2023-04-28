package com.heredata.swift.auth;


import com.heredata.comm.RequestMessage;
import com.heredata.exception.ClientException;

public interface RequestSigner {

    public void sign(RequestMessage request) throws ClientException;

}
