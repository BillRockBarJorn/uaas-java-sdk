package com.heredata.signer;

import com.heredata.comm.RequestMessage;
import com.heredata.exception.ClientException;

/**
 * <p>Title: RequestSigner</p>
 * <p>Description: 发送请求时签名接口
 *                发送请求时，如果需要实现签名计算，需要实现当前接口。
 *                具体签名逻辑由您来实现</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 13:46
 */
public interface RequestSigner {

    public void sign(RequestMessage request) throws ClientException;

}
