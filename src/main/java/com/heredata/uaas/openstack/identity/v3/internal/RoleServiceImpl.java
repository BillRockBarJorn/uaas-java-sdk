package com.heredata.uaas.openstack.identity.v3.internal;


import com.heredata.uaas.api.identity.v3.RoleService;
import com.heredata.uaas.model.common.ActionResponse;
import com.heredata.uaas.model.identity.v3.Role;
import com.heredata.uaas.model.identity.v3.RoleAssignment;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneRole;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneRole.Roles;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneRoleAssignment.RoleAssignments;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.heredata.uaas.core.transport.ClientConstants.PATH_ROLES;

/**
 * Identity Role based Operations Implementation
 *
 */
public class RoleServiceImpl extends BaseIdentityServices implements RoleService {

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionResponse grantProjectUserRole(String projectId, String userId, String roleId) {
        checkNotNull(userId);
        checkNotNull(projectId);
        checkNotNull(roleId);
        return put(ActionResponse.class, uri("projects/%s/users/%s/roles/%s", projectId, userId, roleId)).execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionResponse revokeProjectUserRole(String projectId, String userId, String roleId) {
        checkNotNull(userId);
        checkNotNull(projectId);
        checkNotNull(roleId);
        return deleteWithResponse(uri("projects/%s/users/%s/roles/%s", projectId, userId, roleId)).execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionResponse checkProjectUserRole(String projectId, String userId, String roleId) {
        checkNotNull(projectId);
        checkNotNull(userId);
        checkNotNull(roleId);
        return head(ActionResponse.class, uri("/projects/%s/users/%s/roles/%s", projectId, userId, roleId)).execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionResponse grantDomainUserRole(String domainId, String userId, String roleId) {
        checkNotNull(userId);
        checkNotNull(domainId);
        checkNotNull(roleId);
        return put(ActionResponse.class, uri("domains/%s/users/%s/roles/%s", domainId, userId, roleId)).execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionResponse revokeDomainUserRole(String domainId, String userId, String roleId) {
        checkNotNull(userId);
        checkNotNull(domainId);
        checkNotNull(roleId);
        return deleteWithResponse(uri("domains/%s/users/%s/roles/%s", domainId, userId, roleId)).execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionResponse checkDomainUserRole(String domainId, String userId, String roleId) {
        checkNotNull(domainId);
        checkNotNull(userId);
        checkNotNull(roleId);
        return head(ActionResponse.class, uri("/domains/%s/users/%s/roles/%s", domainId, userId, roleId)).execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends Role> list() {
        return get(Roles.class, uri("/roles")).execute().getList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<? extends Role> getByName(String name) {
        checkNotNull(name);
        return get(Roles.class, PATH_ROLES).param("name", name).execute().getList();
    }

    @Override
    public Role create(Role role) {
        checkNotNull(role);
        return post(KeystoneRole.class, uri(PATH_ROLES)).entity(role).execute();
    }

    @Override
    public Role create(String name) {
        checkNotNull(name);
        return create(KeystoneRole.builder().name(name).build());
    }

    @Override
    public Role update(Role role) {
        checkNotNull(role);
        return patch(KeystoneRole.class, PATH_ROLES, "/", role.getId()).entity(role).execute();
    }

    @Override
    public ActionResponse delete(String roleId) {
        checkNotNull(roleId);
        return deleteWithResponse(PATH_ROLES, "/", roleId).execute();
    }

    @Override
    public Role getRoleById(String roleId) {
        checkNotNull(roleId);
        return get(KeystoneRole.class, PATH_ROLES, "/", roleId).execute();
    }

    @Override
    public List<? extends RoleAssignment> listRoleAssignments(String projectId) {
        checkNotNull(projectId);
        return get(RoleAssignments.class, "role_assignments")
                .param("scope.project.id", projectId)
                .execute().getList();
    }

    @Override
    public ActionResponse grantProjectGroupRole(String projectId, String groupId, String roleId) {
        checkNotNull(projectId);
        checkNotNull(groupId);
        checkNotNull(roleId);
        return put(ActionResponse.class, uri("projects/%s/groups/%s/roles/%s", projectId, groupId, roleId)).execute();
    }

    @Override
    public ActionResponse revokeProjectGroupRole(String projectId, String groupId, String roleId) {
        checkNotNull(projectId);
        checkNotNull(groupId);
        checkNotNull(roleId);
        return deleteWithResponse(uri("projects/%s/groups/%s/roles/%s", projectId, groupId, roleId)).execute();
    }

    @Override
    public ActionResponse checkProjectGroupRole(String projectId, String groupId, String roleId) {
        checkNotNull(projectId);
        checkNotNull(groupId);
        checkNotNull(roleId);
        return head(ActionResponse.class, uri("/projects/%s/groups/%s/roles/%s", projectId, groupId, roleId)).execute();
    }

    @Override
    public ActionResponse grantDomainGroupRole(String domainId, String groupId, String roleId) {
        checkNotNull(domainId);
        checkNotNull(groupId);
        checkNotNull(roleId);
        return put(ActionResponse.class, uri("domains/%s/groups/%s/roles/%s", domainId, groupId, roleId)).execute();
    }

    @Override
    public ActionResponse revokeDomainGroupRole(String domainId, String groupId, String roleId) {
        checkNotNull(domainId);
        checkNotNull(groupId);
        checkNotNull(roleId);
        return deleteWithResponse(uri("domains/%s/groups/%s/roles/%s", domainId, groupId, roleId)).execute();
    }

    @Override
    public ActionResponse checkDomainGroupRole(String domainId, String groupId, String roleId) {
        checkNotNull(domainId);
        checkNotNull(groupId);
        checkNotNull(roleId);
        return head(ActionResponse.class, uri("/domains/%s/groups/%s/roles/%s", domainId, groupId, roleId)).execute();
    }

    @Override
    public List<? extends RoleAssignment> listAllUserRoleAssignments() {
        return get(RoleAssignments.class, "/role_assignments").execute().getList();
    }

    @Override
    public List<? extends RoleAssignment> listUserInAllProjectRoleAssignments(String userId) {
        checkNotNull(userId);
        return get(RoleAssignments.class, "role_assignments")
                .param("user.id", userId)
                .execute().getList();
    }
}
