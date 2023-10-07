package com.heredata.handler;

import com.heredata.comm.RequestMessage;
import com.heredata.exception.ClientException;

/**
* <p>Title: RequestHandler</p>
* <p>Description: TODO </p>
* <p>Copyright: Copyright (c) 2022</p>
* <p>Company: Here-Data </p>
* @author wuzz
* @version 1.0.0
* @createtime 2022/10/27 14:02
*/
public interface RequestHandler {

    public void handle(RequestMessage request) throws ClientException;

}
