package com.heredata.uaas.api.identity.v3;


import com.heredata.uaas.common.RestService;
import com.heredata.uaas.model.common.ActionResponse;
import com.heredata.uaas.model.identity.v3.Project;

import java.util.List;

/**
 * Identity Project Service
 *
 */
public interface ProjectService extends RestService {

    /**
     * Creates a new Project
     *
     * @param project the project to create
     * @return the new Project including it's id
     */
    Project create(Project project);

    /**
     * Creates a new Project
     *
     * @param name the name of the new project
     * @param description the description of the new project
     * @param enabled the enabled status of the new project
     * @return the newly created project
     */
    Project create(String name, String description, Boolean enabled);

    /**
     * Creates a new Project
     *
     * @param isDomain the name of the new project
     * @param name the name of the new project
     * @param description the description of the new project
     * @param enabled the enabled status of the new project
     * @return the newly created project
     */
    Project create(Boolean isDomain, String name, String description, Boolean enabled);

    /**
     * get detailed information on a project
     *
     * @param projectId the project id
     * @return the project
     */
    Project get(String projectId);

    /**
     * 根据租户的名称查询租户详情
     * @param projectName 租户名称
     * @return List<? extends Project>集合
     */
    List<? extends Project> getByName(String projectName);

    /**
     * get detailed information about a project specified by project name and domain id
     *
     * @param projectId the project name
     * @param projectId the domain id
     * @return the project or null if not found
     */
    Project getById(String projectId);

    /**
     * updates an existing project
     *
     * @param project the project set to update
     * @return the updated project
     */
    Project update(Project project);

    /**
     * delete a project by id
     *
     * @param projectId the project id
     * @return the ActionResponse
     */
    ActionResponse delete(String projectId);

    /**
     * list all projects the current token has access to
     *
     * @return list of projects
     */
    List<? extends Project> list();

}
