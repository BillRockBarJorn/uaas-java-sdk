package com.heredata.uaas.openstack.identity.v3.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.heredata.uaas.model.identity.v3.Domain;
import com.heredata.uaas.model.identity.v3.User;
import com.heredata.uaas.model.identity.v3.builder.UserBuilder;
import com.heredata.uaas.openstack.common.HeadResult;
import com.heredata.uaas.openstack.common.ListResult;

import java.util.List;
import java.util.Map;

/**
 * User model class for identity/v3
 *
 * @see <a href= "http://developer.openstack.org/api-ref-identity-v3.html#users-v3">API reference</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName("user")
public class KeystoneUser implements User {

    private static final long serialVersionUID = 1L;
    public final String jsonRootName = "user";
    @JsonProperty
    private String id;
    @JsonProperty
    private String name;
    @JsonProperty
    private KeystoneDomain domain;
    @JsonProperty("domain_id")
    private String domainId;
    private String email;
    private String password;
    @JsonProperty
    private Map<String, String> links;
    private Boolean enabled = true;
    @JsonProperty(value = "head")
    private HeadResult head;

    /**
     * @return the user builder
     */
    public static UserBuilder builder() {
        return new UserConcreteBuilder();
    }

    @Override
    public UserBuilder toBuilder() {
        return new UserConcreteBuilder(this);
    }

    /**
     * @return the id of the user
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * @return the of the user
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return the email of the user
     */
    @Override
    public String getEmail() {
        return email;
    }

    /**
     * @return the password of the user
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * @return the domainId of the user
     */
    @Override
    public String getDomainId() {
        return domainId;
    }

    /**
     * @return the domain of the user
     */
    @Override
    public Domain getDomain() {
        return domain;
    }

    /**
     * @return the links of the user
     */
    @Override
    public Map<String, String> getLinks() {
        return links;
    }

    /**
     * @return the enabled of the user
     */
    @Override
    public boolean isEnabled() {
        return enabled != null && enabled;
    }

    /**
     * set user enabled
     *
     * @param enabled the new enabled status
     */
    @Override
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public HeadResult getHead() {
        return head;
    }

    public void setHead(HeadResult head) {
        this.head = head;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("name", name)
                .add("id", id)
                .add("email", email)
                .add("password", password)
                .add("domainId", domainId)
                .add("links", links)
                .add("enabled", enabled)
                .toString();
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
        KeystoneUser that = KeystoneUser.class.cast(obj);
        return Objects.equal(this.name, that.name)
                && Objects.equal(this.id, that.id)
                && Objects.equal(this.email, that.email)
                && Objects.equal(this.password, that.password)
                && Objects.equal(this.domainId, that.domainId)
                && Objects.equal(this.links, that.domainId)
                && Objects.equal(this.enabled, that.enabled);
    }


    public static class Users extends ListResult<KeystoneUser> {

        private static final long serialVersionUID = 1L;
        @JsonProperty("users")
        private List<KeystoneUser> list;

        @Override
        public List<KeystoneUser> value() {
            return list;
        }
    }

    public static class UserConcreteBuilder implements UserBuilder {

        KeystoneUser model;

        public UserConcreteBuilder() {
            this(new KeystoneUser());
        }

        UserConcreteBuilder(KeystoneUser model) {
            this.model = model;
        }

        /**
         * @see KeystoneUser#getId()
         */
        @Override
        public UserBuilder id(String id) {
            model.id = id;
            return this;
        }

        /**
         * @return the KeystoneUser model
         */
        @Override
        public User build() {
            return model;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public UserBuilder from(User in) {
            if (in != null) {
                this.model = (KeystoneUser) in;
            }
            return this;
        }

        /**
         * @see KeystoneUser#getName()
         */
        @Override
        public UserBuilder name(String name) {
            model.name = name;
            return this;
        }

        /**
         * @see KeystoneUser#getDomainId()
         */
        @Override
        public UserBuilder domainId(String domainId) {
            model.domainId = domainId;
            return this;
        }

        /**
         * @see KeystoneUser#getDomain()
         */
        @Override
        public UserBuilder domain(Domain domain) {
            if (domain != null && domain.getId() != null) {
                model.domainId = domain.getId();
            }
            return this;
        }

        /**
         * @see KeystoneUser#getEmail()
         */
        @Override
        public UserBuilder email(String email) {
            model.email = email;
            return this;
        }

        /**
         * @see KeystoneUser#getPassword()
         */
        @Override
        public UserBuilder password(String password) {
            model.password = password;
            return this;
        }

        /**
         * @see KeystoneUser#getLinks()
         */
        @Override
        public UserBuilder links(Map<String, String> links) {
            model.links = links;
            return this;
        }

        /**
         * @see KeystoneUser#isEnabled()
         */
        @Override
        public UserBuilder enabled(boolean enabled) {
            model.enabled = enabled;
            return this;
        }
    }

}
