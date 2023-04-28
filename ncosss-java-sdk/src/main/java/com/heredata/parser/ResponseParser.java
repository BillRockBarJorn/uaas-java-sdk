package com.heredata.parser;

import com.heredata.ResponseMessage;
import com.heredata.exception.ResponseParseException;

/**
 * <p>Title: ResponseParser</p>
 * <p>Description: 响应解析接口，当响应结果需要由流转换为指定信息需要实现此接口 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 15:14
 */
public interface ResponseParser<T> {
    /**
     * 将响应信息转换为泛型T
     * @param response
     *            The http response message.
     * @return The java Type T object that the result stands for.
     * @throws ResponseParseException
     *             Failed to parse the result.
     */
    public T parse(ResponseMessage response) throws ResponseParseException;
}
