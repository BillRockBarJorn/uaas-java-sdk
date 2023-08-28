package com.heredata.uaas.identity.v3;

import com.heredata.uaas.AbstractTest;
import com.heredata.uaas.api.OSClient;
import com.heredata.uaas.api.exceptions.OS4JException;
import com.heredata.uaas.api.exceptions.ResponseException;
import com.heredata.uaas.api.identity.v3.IdentityService;
import com.heredata.uaas.model.common.ActionResponse;
import com.heredata.uaas.model.identity.v3.Region;
import com.heredata.uaas.model.identity.v3.Token;
import com.heredata.uaas.openstack.OSFactory;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneRegion;
import org.junit.Test;

import java.util.List;

/**
 * TODO
 * @author wuzz
 * @since 2022/9/7
 */
public class RegionTest extends AbstractTest {

    @Test
    public void create() {
        try {
            IdentityService identity = getOSClientV3().identity();
            Region region = identity.regions().create(KeystoneRegion.builder().id("beijing").description("description").build());
            System.out.println(region);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void getDetail() {
        try {
            IdentityService identity = getOSClientV3().identity();
            Region region = identity.regions().get("beijing");
            System.out.println(region);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void addChildRegion() {
        Token token = getToken();
        OSClient.OSClientV3 osClientV3 = OSFactory.clientFromToken(token, userName, password, accountName);
        IdentityService identity = osClientV3.identity();
        Region region = identity.regions().create(KeystoneRegion.builder().id("abcdefgh").description("我是区").parentRegionId("abcdefg").build());
        System.out.println(region);
    }

    @Test
    public void listRegion() {
        try {
            IdentityService identity = getOSClientV3().identity();
            List<? extends Region> region = identity.regions().list();
            region.forEach(System.out::println);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void updateRegion() {
        try {
            IdentityService identity = getOSClientV3().identity();
            Region region = identity.regions().update(KeystoneRegion.builder().id("beijing").parentRegionId("abc")
                    .description("description").build());
            System.out.println(region);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void deleteRegion() {
        try {
            IdentityService identity = getOSClientV3().identity();
            ActionResponse result = identity.regions().delete("abc");
            System.out.println(result);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }
}
