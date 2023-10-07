package com.heredata.parser;

/**
 * <p>Title: Marshaller</p>
 * <p>Description: 请求体body中处理接口，将数据处理成字节数组 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 15:11
 */
public interface Marshaller<T, R> {

    public T marshall(R input);

}
