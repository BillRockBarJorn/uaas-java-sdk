package com.heredata.request;

import com.heredata.ResponseMessage;
import com.heredata.comm.RequestMessage;
import com.heredata.comm.RetryStrategy;

/**
 * No retry strategy that does nothing when HTTP request fails.
 */

/**
 * <p>Title: NoRetryStrategy</p>
 * <p>Description: 请求重试策略。此类代表发送失败后不再重试发送请求 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 15:20
 */
public class NoRetryStrategy extends RetryStrategy {

    @Override
    public boolean shouldRetry(Exception ex, RequestMessage request, ResponseMessage response, int retries) {
        return false;
    }

}
