package com.heredata.uaas.openstack.identity.v3.internal;


import com.heredata.uaas.api.identity.v3.ProjectService;
import com.heredata.uaas.model.common.ActionResponse;
import com.heredata.uaas.model.identity.v3.Project;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneProject;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneProject.Projects;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.heredata.uaas.core.transport.ClientConstants.PATH_PROJECTS;
import static com.heredata.uaas.core.transport.ClientConstants.URI_SEP;

public class ProjectServiceImpl extends BaseIdentityServices implements ProjectService {

    @Override
    public Project create(Project project) {
        checkNotNull(project);
        return post(KeystoneProject.class, PATH_PROJECTS).entity(project).execute();
    }

    @Override
    public Project create(String name, String description, Boolean enabled) {
        checkNotNull(name);
        return this.create(null, name, description, enabled);
    }

    @Override
    public Project create(Boolean isDomain, String name, String description, Boolean enabled) {
        return create(KeystoneProject.builder().name(name).description(description).enabled(enabled).build());
    }

    @Override
    public Project get(String projectId) {
        checkNotNull(projectId);
        return get(KeystoneProject.class, PATH_PROJECTS, "/", projectId).execute();
    }

    @Override
    public List<? extends Project> getByName(String projectName) {
        List<KeystoneProject> list = new ArrayList<>();
        list = get(Projects.class, uri(PATH_PROJECTS)).execute().getList();
        List<KeystoneProject> result = new ArrayList<>();
        list.stream().filter(item -> item.getName().contains(projectName)).forEach(item -> result.add(item));
        return result;
    }

    @Override
    public Project getById(String projectId) {
        checkNotNull(projectId);
        return get(KeystoneProject.class, uri(PATH_PROJECTS + URI_SEP + projectId)).execute();
    }

    @Override
    public Project update(Project project) {
        checkNotNull(project);
        return patch(KeystoneProject.class, PATH_PROJECTS, "/", project.getId()).entity(project).execute();
    }

    @Override
    public ActionResponse delete(String projectId) {
        checkNotNull(projectId);
        return deleteWithResponse(PATH_PROJECTS, "/", projectId).execute();
    }

    @Override
    public List<? extends Project> list() {
        return get(Projects.class, uri(PATH_PROJECTS)).execute().getList();
    }

}
