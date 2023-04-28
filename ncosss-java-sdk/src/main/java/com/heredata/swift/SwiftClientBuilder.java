package com.heredata.swift;

import com.heredata.ClientBuilderConfiguration;
import com.heredata.swift.auth.DefaultCredentialProvider;

import java.util.HashMap;
import java.util.Map;


/**
 * <p>Title: SwiftClientBuilder</p>
 * <p>Description: SWIFT构造器 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/9/2 10:12
 *
 */
public class SwiftClientBuilder implements SwiftBuilder {

    @Override
    public Swift build(String endpoint, String account, String token) {
        return new SwiftClient(endpoint, getDefaultCredentialProvider(token, account),
                getClientConfiguration(token));
    }

    private static ClientBuilderConfiguration getClientConfiguration(String token) {
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Auth-Token", token);
        clientBuilderConfiguration.setDefaultHeaders(headers);
        return clientBuilderConfiguration;
    }

    /**
     * @Title: 获取默认凭证提供器
     * @Description: new 出默认的凭证提供器并返回
     * @params [token, account]
     * @return com.heredata.swift.auth.DefaultCredentialProvider
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:21
     */
    private static DefaultCredentialProvider getDefaultCredentialProvider(String token, String account) {
        return new DefaultCredentialProvider(token, account);
    }
}
