package com.heredata.s3.signer;

import com.heredata.auth.Credentials;
import lombok.Data;

/**
 * <p>Title: HOSSignerParams</p>
 * <p>Description: 签名参数类 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:03
 */
@Data
public class S3SignerParams {
    /**
     * 用来计算签名的resource
     */
    private String resourcePath;

    /**
     * 证书对象，例如说accessKey或secretKey
     */
    private Credentials credentials;

    /**
     * 记号偏移量。当前系统时间+当前值，用来发送请求
     * 如果有自己的系统时间的可以使用当前树形
     */
    private long tickOffset;

    public S3SignerParams(String resourcePath, Credentials creds) {
        this.resourcePath = resourcePath;
        this.credentials = creds;
        this.tickOffset = 0;
    }
}
