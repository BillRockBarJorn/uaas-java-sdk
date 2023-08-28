package com.heredata.uaas.identity.v3;

import com.heredata.uaas.AbstractTest;
import com.heredata.uaas.api.OSClient;
import com.heredata.uaas.model.identity.v3.Token;
import com.heredata.uaas.openstack.OSFactory;
import com.heredata.uaas.openstack.internal.OSClientSession;
import org.junit.Test;

import java.util.Set;

/**
 * TODO
 * @author wuzz
 * @since 2022/9/13
 */
public class OSClientTest extends AbstractTest {


    @Test
    public void demo1() {
        Token token = getToken();
        OSClient.OSClientV3 osClientV3 = OSFactory.clientFromToken(token, userName, password, accountName);
        OSClientSession current = OSClientSession.getCurrent();
        Set supportedServices = current.getSupportedServices();
        System.out.println(supportedServices);
    }

}
