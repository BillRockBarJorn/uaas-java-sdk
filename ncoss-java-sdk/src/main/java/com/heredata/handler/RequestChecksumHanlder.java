package com.heredata.handler;

import com.heredata.comm.RequestMessage;
import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import com.heredata.utils.CRC64;

import java.io.InputStream;
import java.util.zip.CheckedInputStream;

/**
 * <p>Title: RequestChecksumHanlder</p>
 * <p>Description: http请求中对content/body里面流信息进行CRC冗余校验处理器 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 14:02
 */
public class RequestChecksumHanlder implements RequestHandler {

    @Override
    public void handle(RequestMessage request) throws ServiceException, ClientException {
        InputStream originalInputStream = request.getContent();
        if (originalInputStream == null) {
            return;
        }

        CRC64 crc = new CRC64();
        CheckedInputStream checkedInputstream = new CheckedInputStream(originalInputStream, crc);
        request.setContent(checkedInputstream);
    }

}
