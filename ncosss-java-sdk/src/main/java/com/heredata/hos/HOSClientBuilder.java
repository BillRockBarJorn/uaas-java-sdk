package com.heredata.hos;

import com.heredata.ClientBuilderConfiguration;
import com.heredata.hos.auth.DefaultCredentialProvider;


/**
 * <p>Title: HOSClientBuilder</p>
 * <p>Description: HOS构造器 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/9/2 10:12
 *
 */
public class HOSClientBuilder implements HOSBuilder {

    @Override
    public HOS build(String endpoint, String account, String accessKey, String secretKey) {
        return new HOSClient(endpoint, getDefaultCredentialProvider(accessKey, secretKey, account),
                getClientConfiguration());
    }

    private static ClientBuilderConfiguration getClientConfiguration() {
        return new ClientBuilderConfiguration();
    }

    /**
     * @Title: 获取默认凭证提供器
     * @Description: new 出默认的凭证提供器并返回
     * @params [accessKey, secretKey, account]
     * @return com.heredata.hos.auth.DefaultCredentialProvider
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:21
     */
    private static DefaultCredentialProvider getDefaultCredentialProvider(String accessKey, String secretKey,
                                                                          String account) {
        return new DefaultCredentialProvider(accessKey, secretKey, account);
    }
}
