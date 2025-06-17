package com.heredata.uaas.model.identity.v3;

import com.heredata.uaas.common.Buildable;
import com.heredata.uaas.model.ModelEntity;
import com.heredata.uaas.model.identity.v3.builder.UserBuilder;

import java.util.Map;

/**
 * identity v3 user model class
 *
 * @see <a href="http://developer.openstack.org/api-ref-identity-v3.html#users-v3">API reference</a>
 */
public interface User extends ModelEntity, Buildable<UserBuilder> {

    /**
     * Globally unique within the owning domain.
     *
     * @return the Id of the user
     */
    String getId();

    /**
     * @return the name of the user
     */
    String getName();

    /**
     * @return the email of the user
     */
    String getEmail();

    /**
     * @return the password of the user
     */
    String getPassword();

    /**
     * @return the domainId of the user
     */
    String getDomainId();

    /**
     * @return the domain of the user
     */
    Domain getDomain();

    /**
     * @return the links of the user
     */
    Map<String, String> getLinks();

    /**
     * @return the enabled status of the user
     */
    boolean isEnabled();

    /**
     * sets the enabled status of the user
     *
     * @param enabled the enabled
     */
    void setEnabled(Boolean enabled);

}
