package com.heredata.uaas.identity.v3;

import com.heredata.uaas.AbstractTest;
import com.heredata.uaas.api.OSClient.OSClientV3;
import com.heredata.uaas.api.exceptions.OS4JException;
import com.heredata.uaas.api.exceptions.ResponseException;
import com.heredata.uaas.api.identity.v3.IdentityService;
import com.heredata.uaas.model.common.ActionResponse;
import com.heredata.uaas.model.identity.v3.Group;
import com.heredata.uaas.model.identity.v3.Token;
import com.heredata.uaas.model.identity.v3.User;
import com.heredata.uaas.openstack.OSFactory;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneGroup;
import org.junit.Test;

import java.util.List;

/**
 * TODO
 * @author wuzz
 * @since 2022/9/7
 */
public class GroupTest extends AbstractTest {


    @Test
    public void createGroup() {
        try {
            IdentityService identity = getOSClientV3().identity();
            Group group1 = identity.groups().create("domainId", "groupName", "description");
            System.out.println(group1);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void listGroup() {
        try {
            IdentityService identity = getOSClientV3().identity();
            // 查询组列表
            List<? extends Group> list = identity.groups().list();
            list.forEach(System.out::println);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void getGroupDetail() {
        try {
            IdentityService identity = getOSClientV3().identity();
            Group group = identity.groups().get("groupId");
            System.out.println(group);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void updateGroup() {
        try {
            IdentityService identity = getOSClientV3().identity();
            Group group = identity.groups().update(KeystoneGroup.builder().id("groupId").name("groupName").build());
            System.out.println(group);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void deleteGroup() {
        try {
            IdentityService identity = getOSClientV3().identity();
            ActionResponse result = identity.groups().delete("groupId");
            System.out.println(result);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void pointUserAddGroup() {
        try {
            IdentityService identity = getOSClientV3().identity();
            ActionResponse result = identity.groups().addUserToGroup("groupId", "userId");
            System.out.println(result);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void listGroupUsers() {
        try {
            IdentityService identity = getOSClientV3().identity();
            List<? extends User> result = identity.groups().listGroupUsers("groupId");
            result.forEach(System.out::println);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void checkGroupUser() {
        try {
            IdentityService identity = getOSClientV3().identity();
            ActionResponse result = identity.groups().checkGroupUser("groupId", "userId");
            System.out.println(result);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void removeUserFromGroup() {
        Token token = getToken();
        OSClientV3 osClientV3 = OSFactory.clientFromToken(token, userName, password, accountName);
        IdentityService identity = osClientV3.identity();

        ActionResponse result = identity.groups().removeUserFromGroup("groupId", "userId");
        System.out.println(result);
    }

    @Test
    public void setProjectGroupRoles() {
        try {
            IdentityService identity = getOSClientV3().identity();
            ActionResponse result = identity.groups().setProjectGroupRoles("groupId"
                    , "projectId", "roleId");
            System.out.println(result);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void checkProjectGroupRoles() {
        try {
            IdentityService identity = getOSClientV3().identity();
            ActionResponse result = identity.groups().checkProjectGroupRoles("groupId", "projectId", "roleId");
            System.out.println(result);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void deleteProjectGroupRoles() {
        try {
            IdentityService identity = getOSClientV3().identity();
            ActionResponse result = identity.groups().deleteProjectGroupRoles("groupId", "projectId", "roleId");
            System.out.println(result);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }
}
