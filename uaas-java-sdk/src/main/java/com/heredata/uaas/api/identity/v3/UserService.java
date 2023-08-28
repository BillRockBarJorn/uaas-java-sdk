package com.heredata.uaas.api.identity.v3;


import com.heredata.uaas.common.RestService;
import com.heredata.uaas.model.common.ActionResponse;
import com.heredata.uaas.model.identity.v3.Group;
import com.heredata.uaas.model.identity.v3.Project;
import com.heredata.uaas.model.identity.v3.Role;
import com.heredata.uaas.model.identity.v3.User;

import java.util.List;


/**
 * identity/v3 User operations
 *
 */
public interface UserService extends RestService {

    /**
     * gets detailed information about a specified user by id
     *
     * @param userId the user id
     * @return the user
     */
    User get(String userId);

    /**
     * get detailed information about users matching specified name across all domains
     *
     * @param userId the user's id
     * @return the of list users matching the name across all domains
     */
    User getByUserId(String userId);

    /**
     * get detailed information about a user specified by username and domain id
     *
     * @param userName the user name
     * @return the user or null if not found
     */
    User getByName(String userName);

    /**
     * delete a user by id
     *
     * @param userId the userId
     * @return the action response
     */
    ActionResponse delete(String userId);

    /**
     * updates the password for or enables or disables a specified user.
     *
     * @param user the user set to update
     * @return the updated user
     */
    User update(User user);

    /**
     * create a new user
     *
     * @param user the user
     * @return the newly created user
     */
    User create(User user);

    /**
     * creates a new user
     *
     * @param domainId the domain id
     * @param name the name of the new user
     * @param password the password of the new user
     * @param email the email of the new user
     * @param enabled the enabled of the new user
     * @return the newly created user
     */
    User create(String domainId, String name, String password, String email, boolean enabled);

    /**
     * creates a new user
     *
     * @param projectId the domain id
     * @return the newly created user
     */
    User createPointProject(String projectId);

    /**
     * lists groups for a specified user
     *
     * @param userId the user id
     * @return list of groups for a user
     */
    List<? extends Group> listUserGroups(String userId);

    /**
     * lists projects for a specified user
     *
     * @param userId the user
     * @return list of projects for a user
     */
    List<? extends Project> listUserProjects(String userId);

    /**
     * 获取指定租户下的用户列表
     * @param projectId：租户id
     * @return
     */
    List<? extends User> listProjectUsers(String projectId);

    /**
     * list role assignments for specified user in project context
     *
     * @param userId the user id
     * @param projectId the scope (project,domain)
     * @return list of role assignments for specified user
     */
    List<? extends Role> listProjectUserRoles(String userId, String projectId);

    /**
     * list role assignment for specified user in domain context
     *
     * @param userId the user identifier
     * @param domainId the domain identifier
     * @return list of role assignments for specified user and domain
     */
    List<? extends Role> listDomainUserRoles(String userId, String domainId);

    /**
     * lists users.
     *
     * @return list of users
     */
    List<? extends User> list();

    /**
     * change password for user.
     *
     * @param userId the user identifier
     * @param originalPassword the original password
     * @param password the new password
     * @return the action response
     */
    ActionResponse changePassword(String userId, String originalPassword, String password);


}
