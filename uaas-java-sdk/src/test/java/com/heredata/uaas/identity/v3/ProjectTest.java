package com.heredata.uaas.identity.v3;

import com.heredata.uaas.AbstractTest;
import com.heredata.uaas.api.OSClient;
import com.heredata.uaas.api.exceptions.OS4JException;
import com.heredata.uaas.api.exceptions.ResponseException;
import com.heredata.uaas.api.identity.v3.IdentityService;
import com.heredata.uaas.model.common.ActionResponse;
import com.heredata.uaas.model.identity.v3.Project;
import com.heredata.uaas.model.identity.v3.Token;
import com.heredata.uaas.model.identity.v3.User;
import com.heredata.uaas.openstack.OSFactory;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneToken;
import org.junit.Test;

import java.util.List;

/**
 * TODO
 * @author wuzz
 * @since 2022/9/14
 */
public class ProjectTest extends AbstractTest {


    @Test
    public void simpleUse() {
        // 用户信息  用户名
        final String userName = "user01";
        // 用户密码
        final String password = "123456";
        // 账户名称
        final String accountName = "project01";
        // 主机，端口，路径统一前缀信息
        final String endpoint = "http://172.18.232.37:7079/v3";


        // 构造token对象，设置ip+端口+请求路径统一前缀
        KeystoneToken keystoneToken = new KeystoneToken(endpoint);
        try {
            // 根据token创建V3连接
            OSClient.OSClientV3 osClientV3 = OSFactory.clientFromToken(keystoneToken, userName, password, accountName);
            // 创建租户
            Project project = osClientV3.identity().projects()
                    .create("project01", "projectDescription", false);
            System.out.println(project);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void createProject() {
        try {
            IdentityService identity = getOSClientV3().identity();
            // 创建租户
            Project project = identity.projects().create("project02", "projectDescription", true);
            System.out.println(project);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void queryProject() {
        try {
            IdentityService identity = getOSClientV3().identity();
            // 根据账户名称查询账户，支持模糊查询
//            List<? extends Project> resultList = identity.projects().getByName("Bill");
//            resultList.forEach(System.out::println);
            System.out.println("==============================================================================");
            // 根据账户id查询账户
            Project byId = identity.projects().getById("8dbb6d12454811eea8b689966c9575e8");
            System.out.println(byId);
            System.out.println("==============================================================================");
            // 查询租户列表
            List<? extends Project> list = identity.projects().list();
            list.forEach(System.out::println);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void deleteProject() {
        try {
            IdentityService identity = getOSClientV3().identity();
            ActionResponse delete = identity.projects().delete("8ab83c3e335711edbc330391d2a979b2");
            System.out.println(delete);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void grantProjectUserRole() {
        try {
            IdentityService identity = getOSClientV3().identity();
            ActionResponse actionResponse = identity.roles()
                    .grantProjectUserRole("96331e6e454c11eea8b689966c9575e8", "5b83acc4454d11eea8b689966c9575e8", "003");
            System.out.println(actionResponse);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void revokeProjectUserRole() {
        try {
            IdentityService identity = getOSClientV3().identity();
            ActionResponse result = identity.roles().revokeProjectUserRole("7c9dfff2139b11edbc330391d2a979b2", "89d5dcfa3b0811edbc330391d2a979b2", "001");
            System.out.println(result);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void listProjectUsers() {
        try {
            OSClient.OSClientV3 clientV3 = getOSClientV3();
            Token token = clientV3.getToken();
            List<? extends User> list = clientV3.identity().users().listProjectUsers(token.getProject().getId());
            list.forEach(System.out::println);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void projectUserTest() {
        Token token = getToken();
        OSClient.OSClientV3 osClientV3 = OSFactory.clientFromToken(token, userName, password, accountName);
        IdentityService identity = osClientV3.identity();

        // 查询租户列表
        List<? extends Project> list = identity.projects().list();
        list.forEach(System.out::println);
        System.out.println("=========================================");


        // 查询用户列表
        List<? extends User> list1 = identity.users().list();
        list1.forEach(System.out::println);
        System.out.println("=========================================");

        // 绑定用户至租户,并设置为  003  ta	租户管理员（Tenant Administrator）
        ActionResponse actionResponse = identity.roles().grantProjectUserRole("099fdfb62e7611edbc330391d2a979b2", "0e3a01f42e7711edbc330391d2a979b2", "003");
        System.out.println(actionResponse);
        System.out.println("=========================================");

        // 取消用户和租户的绑定
//        ActionResponse actionResponse1 = identity.roles().revokeProjectUserRole("099fdfb62e7611edbc330391d2a979b2", "0e3a01f42e7711edbc330391d2a979b2", "003");
//        System.out.println(actionResponse1);
//        System.out.println("=========================================");

        // 获取与指定user相关的租户列表
        List<? extends Project> projects = identity.users().listUserProjects("0e3a01f42e7711edbc330391d2a979b2");
        System.out.println(projects);
    }
}
