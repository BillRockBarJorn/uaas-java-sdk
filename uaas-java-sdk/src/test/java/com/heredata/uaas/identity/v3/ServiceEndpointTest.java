package com.heredata.uaas.identity.v3;

import com.heredata.uaas.AbstractTest;
import com.heredata.uaas.api.exceptions.OS4JException;
import com.heredata.uaas.api.exceptions.ResponseException;
import com.heredata.uaas.api.identity.v3.IdentityService;
import com.heredata.uaas.api.types.Facing;
import com.heredata.uaas.api.types.ServiceType;
import com.heredata.uaas.model.common.ActionResponse;
import com.heredata.uaas.model.identity.v3.Endpoint;
import com.heredata.uaas.model.identity.v3.Service;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneEndpoint;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneService;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * TODO
 * @author wuzz
 * @since 2022/9/7
 */
public class ServiceEndpointTest extends AbstractTest {

    @Test
    public void create() {
        try {
            IdentityService identity = getOSClientV3().identity();
            Service service = identity.serviceEndpoints().create(
                    KeystoneService.builder().type(ServiceType.IDENTITY.name())
                            .name("identity").description("description").build());
            System.out.println(service);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void getServiceDetail() {
        try {
            IdentityService identity = getOSClientV3().identity();
            Service service = identity.serviceEndpoints().get("4b27ff463a2811edbc330391d2a979b2");
            System.out.println(service);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void listService() {
        try {
            IdentityService identity = getOSClientV3().identity();
            List<? extends Service> services = identity.serviceEndpoints().list();
            services.forEach(System.out::println);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void updateService() {
        try {
            IdentityService identity = getOSClientV3().identity();
            Service aaa = identity.serviceEndpoints().update(KeystoneService.builder().id("4b27ff463a2811edbc330391d2a979b2")
                    .type("identity").description("description").name("identity").enabled(true).build());
            System.out.println(aaa);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void deleteService() {
        try {
            IdentityService identity = getOSClientV3().identity();
            ActionResponse delete = identity.serviceEndpoints().delete("4b27ff463a2811edbc330391d2a979b2");
            System.out.println(delete);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void createEndpoint() throws MalformedURLException {
        try {
            IdentityService identity = getOSClientV3().identity();
            Endpoint endpoint = identity.serviceEndpoints().createEndpoint(KeystoneEndpoint.builder().iface(Facing.PUBLIC).region("regionId")
                    .serviceId("69410ecc3a2911edbc330391d2a979b2").enabled(true).url(new URL("http://172.18.232.37:7079/v3")).build());
            System.out.println(endpoint);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void getEndpoint() {
        try {
            IdentityService identity = getOSClientV3().identity();
            Endpoint endpoint = identity.serviceEndpoints().getEndpoint("805a96783a2911edbc330391d2a979b2");
            System.out.println(endpoint);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void listEndpoints() {
        try {
            IdentityService identity = getOSClientV3().identity();
            List<? extends Endpoint> endpoints = identity.serviceEndpoints().listEndpoints();
            endpoints.forEach(System.out::println);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void updateEndpoints() throws MalformedURLException {
        try {
            IdentityService identity = getOSClientV3().identity();
            Endpoint endpoint = identity.serviceEndpoints().updateEndpoint(KeystoneEndpoint.builder().id("805a96783a2911edbc330391d2a979b2").
                    serviceId("69410ecc3a2911edbc330391d2a979b2").enabled(false).url(new URL("http://172.18.232.37:7079/v3")).build());
            System.out.println(endpoint);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void deleteEndpoints() {
        try {
            IdentityService identity = getOSClientV3().identity();
            ActionResponse result = identity.serviceEndpoints().deleteEndpoint("805a96783a2911edbc330391d2a979b2");
            System.out.println(result);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }
}
