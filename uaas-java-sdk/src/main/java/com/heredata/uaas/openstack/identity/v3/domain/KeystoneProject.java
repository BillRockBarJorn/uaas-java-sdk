package com.heredata.uaas.openstack.identity.v3.domain;

import com.fasterxml.jackson.annotation.*;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.heredata.uaas.model.identity.v3.Domain;
import com.heredata.uaas.model.identity.v3.Project;
import com.heredata.uaas.model.identity.v3.builder.ProjectBuilder;
import com.heredata.uaas.openstack.common.ListResult;

import java.util.List;
import java.util.Map;

/**
 * Project model class for identity/v3
 *
 * @see <a href="http://developer.openstack.org/api-ref-identity-v3.html#projects-v3">API reference</a>
 */
@JsonRootName("project")
/** If we don't explicitly set extra as an ignore property, it will methods with @JsonAnyGetter/Setter will not work **/
@JsonIgnoreProperties(value = "extra", ignoreUnknown = true)
public class KeystoneProject implements Project {

    private static final long serialVersionUID = 1L;
    @JsonProperty
    private String id;
    @JsonProperty
    private String name;
    private KeystoneDomain domain;
    @JsonProperty("is_domain")
    private Boolean isDomain;
    @JsonProperty("domain_id")
    private String domainId;
    @JsonProperty
    private String description;
    @JsonIgnore
    private Map<String, String> links;
    @JsonProperty("parent_id")
    private String parentId;
    @JsonProperty
    private Boolean enabled = true;
    private Map<String, String> extra = Maps.newHashMap();

    /**
     * @return the Project builder
     */
    public static ProjectBuilder builder() {
        return new ProjectConcreteBuilder();
    }

    @Override
    public ProjectBuilder toBuilder() {
        return new ProjectConcreteBuilder(this);
    }


    @Override
    public KeystoneDomain getDomain() {
        return domain;
    }

    public void setDomain(KeystoneDomain domain) {
        this.domain = domain;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDomainId() {
        return this.domainId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @JsonIgnore
    @Override
    public Map<String, String> getLinks() {
        return links;
    }

    /**
     * {@inheritDoc}
     */
    @JsonProperty("links")
    public void setLinks(Map<String, String> links) {
        this.links = links;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParentId() {
        return parentId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return (enabled != null && enabled);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtra(String key) {
        return extra.get(key);
    }

    @JsonAnyGetter
    public Map<String, String> getExtra() {
        return extra;
    }

    @JsonAnySetter
    public void setExtra(String key, String value) {
        // is_domain is not necessary
        // if we don't ignore this, this will be set into extra field.
        if (Objects.equal(key, "is_domain")) {
            return;
        }
        extra.put(key, value);
    }

    /**
     * set project enabled
     *
     * @param enabled
     *            the new enabled status
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String dId = null;
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("domainId", dId)
                .add("description", description)
                .add("name", name)
                .add("links", links)
                .add("parentId", parentId)
                .add("enabled", enabled)
                .omitNullValues()
                .toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id, domainId, description, name, links, parentId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        KeystoneProject that = KeystoneProject.class.cast(obj);
        return Objects.equal(this.id, that.id)
                && Objects.equal(this.description, that.description)
                && Objects.equal(this.name, that.name)
                && Objects.equal(this.links, that.links)
                && Objects.equal(this.parentId, that.parentId)
                && Objects.equal(this.enabled, that.enabled);
    }

    public static class ProjectConcreteBuilder implements ProjectBuilder {

        KeystoneProject model;

        ProjectConcreteBuilder() {
            this(new KeystoneProject());
        }

        ProjectConcreteBuilder(KeystoneProject model) {
            this.model = model;
        }

        /**
         * @see KeystoneProject#getId()
         */
        @Override
        public ProjectBuilder id(String id) {
            model.id = id;
            return this;
        }

        /**
         * @see KeystoneProject#getDomainId()
         */
        @Override
        public ProjectBuilder domain(Domain domain) {
            if (domain != null && domain.getId() != null) {
                model.domainId = domain.getId();
            }
            return this;
        }

        /**
         * @see KeystoneProject#getDescription()
         */
        @Override
        public ProjectBuilder description(String description) {
            model.description = description;
            return this;
        }

        /**
         * @see KeystoneProject#getName()
         */
        @Override
        public ProjectBuilder name(String name) {
            model.name = name;
            return this;
        }

        /**
         * @see KeystoneProject#getLinks()
         */
        @Override
        public ProjectBuilder links(Map<String, String> links) {
            model.links = links;
            return this;
        }

        /**
         * @see KeystoneProject#getParentId()
         */
        @Override
        public ProjectBuilder parentId(String parentId) {
            model.parentId = parentId;
            return this;
        }

        /**
         * @see KeystoneProject#setExtra(String, String)
         */
        @Override
        public ProjectBuilder setExtra(String key, String value) {
            model.extra.put(key, value);
            return this;
        }

        /**
         * @see KeystoneProject#isEnabled()
         */
        @Override
        public ProjectBuilder enabled(Boolean enabled) {
            model.enabled = enabled;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Project build() {
            return model;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ProjectBuilder from(Project in) {
            if (in != null) {
                this.model = (KeystoneProject) in;
            }
            return this;
        }

        @Override
        public ProjectBuilder domainId(String domainId) {
            model.domainId = domainId;
            return this;
        }
    }

    public static class Projects extends ListResult<KeystoneProject> {

        private static final long serialVersionUID = 1L;
        @JsonProperty("projects")
        protected List<KeystoneProject> list;

        @Override
        public List<KeystoneProject> value() {
            return list;
        }
    }

}
