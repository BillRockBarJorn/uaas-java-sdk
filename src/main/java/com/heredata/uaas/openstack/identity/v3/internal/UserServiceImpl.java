package com.heredata.uaas.openstack.identity.v3.internal;

import com.heredata.uaas.api.identity.v3.UserService;
import com.heredata.uaas.model.common.ActionResponse;
import com.heredata.uaas.model.identity.v3.Group;
import com.heredata.uaas.model.identity.v3.Project;
import com.heredata.uaas.model.identity.v3.Role;
import com.heredata.uaas.model.identity.v3.User;
import com.heredata.uaas.openstack.common.MapEntity;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneGroup.Groups;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneProject.Projects;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneRole.Roles;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneUser;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneUser.Users;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.heredata.uaas.common.StringUtils.checkNullOrEmpty;
import static com.heredata.uaas.core.transport.ClientConstants.PATH_USERS;
import static com.heredata.uaas.core.transport.ClientConstants.URI_SEP;

/**
 * implementation of v3 user service
 *
 */
public class UserServiceImpl extends BaseIdentityServices implements UserService {

    /**
     * {@inheritDoc}
     */
    @Override
    public User get(String userId) {
        checkNotNull(userId);
        return get(KeystoneUser.class, PATH_USERS, "/", userId).execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getByUserId(String userId) {
        checkNotNull(userId);
        KeystoneUser first = get(KeystoneUser.class, uri(PATH_USERS + URI_SEP + userId)).execute();
        return first;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getByName(String userName) {
        checkNotNull(userName);
        return get(Users.class, uri(PATH_USERS)).param("name", userName).execute().first();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionResponse delete(String userId) {
        checkNotNull(userId);
        return deleteWithResponse(PATH_USERS, "/", userId).execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User update(User user) {
        checkNotNull(user);
        checkNotNull(user.getId());
        return patch(KeystoneUser.class, PATH_USERS, "/", user.getId()).entity(user).execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User create(User user) {
        checkNotNull(user);
        return post(KeystoneUser.class, uri(PATH_USERS)).entity(user).execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User create(String domainId, String name, String password, String email, boolean enabled) {
//        checkNotNull(domainId);
        checkNotNull(name);
        checkNotNull(password);
        checkNotNull(email);
        checkNotNull(enabled);
        return create(KeystoneUser.builder().domainId(domainId).name(name)
                .password(password).email(email).enabled(enabled).build());
    }

    /**
     * creates a new user
     *
     * @param projectId the domain id
     * @return the newly created user
     */
    @Override
    public User createPointProject(String projectId) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends Group> listUserGroups(String userId) {
        checkNotNull(userId);
        return get(Groups.class, uri("/users/%s/groups", userId)).execute().getList();
    }

    /**
     * 获取与指定user相关的租户列表
     */
    @Override
    public List<? extends Project> listUserProjects(String userId) {
        checkNotNull(userId);
        return get(Projects.class, uri("/users/%s/projects", userId)).execute().getList();
    }

    /**
     * 查询某账户下的所有用户列表
     * @param projectId：租户id
     * @return
     */
    @Override
    public List<? extends User> listProjectUsers(String projectId) {
        checkNullOrEmpty(projectId);
        return get(Users.class, uri("projects/%s/users", projectId)).execute().getList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends User> list() {
        return get(Users.class, uri(PATH_USERS)).execute().getList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends Role> listProjectUserRoles(String userId, String projectId) {
        checkNotNull(userId);
        checkNotNull(projectId);
        return get(Roles.class, uri("projects/%s/users/%s/roles", projectId, userId)).execute().getList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends Role> listDomainUserRoles(String userId, String domainId) {
        checkNotNull(userId);
        checkNotNull(domainId);
        return get(Roles.class, uri("domains/%s/users/%s/roles", domainId, userId)).execute().getList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionResponse changePassword(String userId, String originalPassword, String password) {
        checkNotNull(userId);
        checkNotNull(originalPassword);
        checkNotNull(password);
        Map<String, Object> passwordMap = new HashMap<String, Object>();
        passwordMap.put("original_password", originalPassword);
        passwordMap.put("password", password);
        MapEntity mapEntity = MapEntity.create("user", passwordMap);
        return post(ActionResponse.class, uri("/users/%s/password", userId)).entity(mapEntity).execute();
    }

}
