package com.heredata.handler;


import com.heredata.ResponseMessage;
import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import com.heredata.utils.CRC64;

import java.io.InputStream;
import java.util.zip.CheckedInputStream;

/**
 * <p>Title: ResponseChecksumHandler</p>
 * <p>Description: http响应中对content/body里面流信息进行CRC冗余校验处理器 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 14:07
 */
public class ResponseChecksumHandler<T> implements ResponseHandler<T> {

    @Override
    public void handle(ResponseMessage response) throws ServiceException, ClientException {
        InputStream originalInputStream = response.getContent();
        if (originalInputStream == null) {
            return;
        }

        CRC64 crc = new CRC64();
        CheckedInputStream checkedInputstream = new CheckedInputStream(originalInputStream, crc);
        response.setContent(checkedInputstream);
    }

    @Override
    public void handle(ResponseMessage response, Class<T> responseClass) throws ServiceException, ClientException {

    }
}
