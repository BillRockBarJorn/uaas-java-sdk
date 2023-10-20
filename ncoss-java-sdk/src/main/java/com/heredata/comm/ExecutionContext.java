package com.heredata.comm;

import com.heredata.signer.RequestSigner;
import com.heredata.handler.RequestHandler;
import com.heredata.handler.ResponseHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.LinkedList;
import java.util.List;

/**
* <p>Title: ExecutionContext</p>
* <p>Description: http请求报文类 </p>
* <p>Copyright: Copyright (c) 2022</p>
* <p>Company: Here-Data </p>
* @author wuzz
* @version 1.0.0
* @createtime 2022/10/27 13:49
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ExecutionContext {

    /**
     * 当需要url签名时，调用实现类中的sign方法 。
     * 即 {@see RequestMessage#useUrlSignature}为true起作用
     */
    private RequestSigner signer;

    /**
     * 请求处理器
     * 在发送请求之前会调用实现类中handle方法
     */
    private List<RequestHandler> requestHandlers = new LinkedList<>();

    /**
     * 响应处理器
     * 在发送请求之后 会调用实现类中handle方法
     */
    private List<ResponseHandler> responseHandlers = new LinkedList<>();

    /**
     * 签名处理器
     * 在发送请求之前 会调用实现类中sign方法
     */
    private List<RequestSigner> signerHandlers = new LinkedList<>();

    private String charset = HttpConstants.DEFAULT_CHARSET_NAME;

    /**
     * 重试策略
     * 当发送请求失败后采取如下的重试策略
     */
    private RetryStrategy retryStrategy;

    public void addResponseHandler(ResponseHandler handler) {
        responseHandlers.add(handler);
    }

    public void insertResponseHandler(int position, ResponseHandler handler) {
        responseHandlers.add(position, handler);
    }

    public void removeResponseHandler(ResponseHandler handler) {
        responseHandlers.remove(handler);
    }

    public void addRequestHandler(RequestHandler handler) {
        requestHandlers.add(handler);
    }

    public void insertRequestHandler(int position, RequestHandler handler) {
        requestHandlers.add(position, handler);
    }

    public void removeRequestHandler(RequestHandler handler) {
        requestHandlers.remove(handler);
    }

    public void addSignerHandler(RequestSigner handler) {
        signerHandlers.add(handler);
    }

    public void insertSignerHandler(int position, RequestSigner handler) {
        signerHandlers.add(position, handler);
    }

    public void removeSignerHandler(RequestSigner handler) {
        signerHandlers.remove(handler);
    }
}
