package com.heredata.uaas.identity.v3;

import com.heredata.uaas.AbstractTest;
import com.heredata.uaas.api.exceptions.OS4JException;
import com.heredata.uaas.api.exceptions.ResponseException;
import com.heredata.uaas.api.identity.v3.IdentityService;
import com.heredata.uaas.model.identity.v3.Role;
import org.junit.Test;

import java.util.List;

/**
 * TODO
 * @author wuzz
 * @since 2022/9/14
 */
public class RoleTest extends AbstractTest {

    @Test
    public void getRole() {
        try {
            IdentityService identity = getOSClientV3().identity();
            // 根据角色ID查询角色信息
//            Role role = identity.roles().getRoleById("001");
//            System.out.println(role);
//            System.out.println("==================================");
            // 根据角色名称查询角色信息
            List<? extends Role> endpoint = identity.roles().getByName("sa");
            endpoint.forEach(System.out::println);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void getRoleList() {
        try {
            IdentityService identity = getOSClientV3().identity();
            // 角色列表
            List<? extends Role> list = identity.roles().list();
            list.forEach(System.out::println);
        } catch (
                ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (
                OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

}
