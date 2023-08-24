package com.heredata.hos;

import com.heredata.ClientBuilderConfiguration;
import com.heredata.ClientConfiguration;
import com.heredata.hos.auth.DefaultCredentialProvider;
import com.heredata.utils.LogUtils;


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
        return build(endpoint, account, accessKey, secretKey, getClientConfiguration());
    }

    @Override
    public HOS build(String endpoint, String account, String accessKey, String secretKey, ClientConfiguration clientConfiguration) {
        return build(endpoint, account,accessKey, secretKey, clientConfiguration, null);
    }

    /**
     * 该接口适用对象存储NCOSS-3.*版本，4.*版本请使用
     * @param endpoint 对象存储的endpoint
     * @param account 账户（租户）id
     * @param accessKey
     * @param secretKey
     * @param clientConfiguration 客户端配置类
     * @param bucket 桶名，如果不想传桶名，而且用的到的桶是固定，可以采取该方式
     * @return
     */
    @Override
    public HOS build(String endpoint, String account, String accessKey, String secretKey, ClientConfiguration clientConfiguration, String bucket) {
        return new HOSClient(endpoint, getDefaultCredentialProvider(accessKey, secretKey, account),
                clientConfiguration, bucket);
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

    private static DefaultCredentialProvider getDefaultCredentialProvider(String accessKey, String secretKey) {
        return new DefaultCredentialProvider(accessKey, secretKey);
    }

    @Override
    public HOS build(String endpoint, String accessKey, String secretKey) {
        return build(endpoint, accessKey, secretKey, getClientConfiguration(), null);
    }

    @Override
    public HOS build(String endpoint, String accessKey, String secretKey, ClientConfiguration clientConfiguration) {
        return build(endpoint, accessKey, secretKey, clientConfiguration, null);
    }

    @Override
    public HOS build(String endpoint, String accessKey, String secretKey, ClientConfiguration clientConfiguration, String bucket) {
        return new HOSClient(endpoint, getDefaultCredentialProvider(accessKey, secretKey), clientConfiguration, bucket);
    }
}
