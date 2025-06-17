package com.heredata.uaas.identity.v3;

import com.heredata.uaas.AbstractTest;
import com.heredata.uaas.api.OSClient;
import com.heredata.uaas.api.exceptions.OS4JException;
import com.heredata.uaas.api.exceptions.ResponseException;
import com.heredata.uaas.api.identity.v3.IdentityService;
import com.heredata.uaas.model.common.ActionResponse;
import com.heredata.uaas.model.identity.v3.Role;
import com.heredata.uaas.model.identity.v3.RoleAssignment;
import com.heredata.uaas.model.identity.v3.Token;
import com.heredata.uaas.model.identity.v3.User;
import com.heredata.uaas.openstack.OSFactory;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneUser;
import org.junit.Test;

import java.util.List;


/**
 * TODO
 * @author wuzz
 * @since 2022/9/5
 */
public class UserTest extends AbstractTest {
    @Test
    public void roleManager() {
        Token token = getToken();
        OSClient.OSClientV3 osClientV3 = OSFactory.clientFromToken(token, userName, password, accountName);
        IdentityService identity = osClientV3.identity();

        // 角色列表
        List<? extends Role> list = identity.roles().list();
        list.forEach(System.out::println);
        System.out.println("==================================");

        // 根据角色ID查询角色信息
        Role role = identity.roles().getRoleById("003");
        System.out.println(role);
        System.out.println("==================================");
        // 根据角色名称查询角色信息
        List<? extends Role> endpoint = identity.roles().getByName("endpoint");
        endpoint.forEach(System.out::println);
        System.out.println("==================================");

        // 获取所有用户角色列表
        List<? extends RoleAssignment> keystoneRoleAssignments = identity.roles().listAllUserRoleAssignments();
        keystoneRoleAssignments.forEach(System.out::println);
        System.out.println("===============获取指定租户下所有用户角色列表===================");

        // 获取指定租户下所有用户角色列表
        List<? extends RoleAssignment> roleAssignments = identity.roles().listRoleAssignments("099fdfb62e7611edbc330391d2a979b2");
        roleAssignments.forEach(System.out::println);
        System.out.println("==================================");

        // 获取指定用户在所有关联租户下的角色列表
        List<? extends RoleAssignment> roleAssignments1 = identity.roles().listUserInAllProjectRoleAssignments("d99c203428f511edbc330391d2a979b2");
        roleAssignments1.forEach(System.out::println);
        System.out.println("==================================");
    }

    @Test
    public void listUserInAllProjectRoleAssignments() {
        try {
            IdentityService identity = getOSClientV3().identity();
            List<? extends RoleAssignment> roleAssignments = identity.roles()
                    .listUserInAllProjectRoleAssignments("528ec66a290d11edbc330391d2a979b2");
            roleAssignments.forEach(System.out::println);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void listRoleAssignments() {
        try {
            IdentityService identity = getOSClientV3().identity();
            List<? extends RoleAssignment> roleAssignments = identity.roles()
                    .listRoleAssignments("96331e6e454c11eea8b689966c9575e8");
            roleAssignments.forEach(System.out::println);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void listAllUserRoleAssignments() {
        try {
            OSClient.OSClientV3 osClientV3 = getOSClientV3();
            IdentityService identity = osClientV3.identity();
            List<? extends RoleAssignment> roleAssignments = identity.roles().listAllUserRoleAssignments();
            roleAssignments.forEach(System.out::println);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    /**
     * 创建用户
     */
    @Test
    public void createUser() {
        try {
            IdentityService identity = getOSClientV3().identity();
            User user = identity.users().create(null, "user02", "TEST#ps@857"
                    , "aaaaa@heredata.com", true);
            System.out.println(user);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void getUser() {
        try {
            IdentityService identity = getOSClientV3().identity();
            // 根据用户id查询用户
            User byUserId = identity.users().getByUserId("9ef3db5428f311edbc330391d2a979b2");
            System.out.println(byUserId);
            // 根据用户名称查询用户
            User byName = identity.users().getByName("test_user1");
            System.out.println(byName);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @Test
    public void updateUser() {
        try {
            IdentityService identity = getOSClientV3().identity();
            // 将用户名为test_user1的用户改名为test_user2
            User user = new KeystoneUser.UserConcreteBuilder().name("test_user2").password("wqdaczxv")
                    .enabled(true).id("9ef3db5428f311edbc330391d2a979b2").build();
            User update = identity.users().update(user);
            System.out.println(update);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    /**
     * 更新用户密码
     */
    @Test
    public void updateUserPassword() {
        try {
            IdentityService identity = getOSClientV3().identity();
            ActionResponse actionResponse = identity.users()
                    .changePassword("0889c12633fa11edbc330391d2a979b2", "wqdaczxv", "newPassword");
            System.out.println(actionResponse);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    /**
     * 通过userId删除指定用户
     */
    @Test
    public void deleteUserById() {
        try {
            IdentityService identity = getOSClientV3().identity();
            ActionResponse delete = identity.users().delete("9ef3db5428f311edbc330391d2a979b2");
            System.out.println(delete);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void listUsers() {
        try {
            OSClient.OSClientV3 clientV3 = getOSClientV3();
            List<? extends User> list = clientV3.identity().users().list();
            list.forEach(System.out::println);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void listProjectUsers() {
        OSClient.OSClientV3 osClientV3 = getOSClientV3();
        Token token = osClientV3.getToken();
        IdentityService identity = osClientV3.identity();

        List<? extends User> list = identity.users().listProjectUsers(token.getProject().getId());

        list.forEach(System.out::println);

        System.out.println("======================================");

        // 为当前账户创建用户
//        User pointProject = identity.users().createPointProject(token.getProject().getId());
    }
}
