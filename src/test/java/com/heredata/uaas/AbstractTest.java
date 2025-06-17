package com.heredata.uaas;

import com.heredata.uaas.api.OSClient.OSClientV3;
import com.heredata.uaas.model.identity.v3.Token;
import com.heredata.uaas.openstack.OSFactory;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneToken;

/**
 * Base Test class which handles Mocking a Webserver to fullfill and test
 * against JSON response objects from an OpenStack deployment
 *
 * @author wuzz
 */
public abstract class AbstractTest {
    // 非admin账户信息
//    public static final String userName = "test_user1";
//    public static final String password = "TEST#ps@857";
//    public static final String accountName = "test_pro1";
    // admin账户信息
    public static final String userName = "epdoc_test_usr1";
    public static final String password = "epdoc@202306";
    public static final String accountName = "epdoc_test";
    // 主机，端口，路径统一前缀信息
    public static final String endpoint = "http://172.18.194.245:8066/v3";


    /**
     * 通过OSFactory创建OSClientV3实例
     * @return
     */
    protected OSClientV3 getOSClientV3() {
        // 构造token对象，设置ip+端口+请求路径统一前缀
        KeystoneToken keystoneToken = new KeystoneToken(endpoint);
        // 根据token创建V3连接
        return OSFactory.clientFromToken(keystoneToken, userName, password, accountName);
    }

    /**
     * 获取实例里面的token
     * @return
     */
    protected Token getToken() {
        return getOSClientV3().getToken();
    }
}
