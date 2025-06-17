package com.heredata.uaas.model.identity.v3.builder;


import com.heredata.uaas.common.Buildable;
import com.heredata.uaas.model.identity.v3.Domain;
import com.heredata.uaas.model.identity.v3.User;

import java.util.Map;

/**
 * A Builder which creates a identity v3 project
 *
 *
 *
 */
public interface UserBuilder extends Buildable.Builder<UserBuilder, User> {

    /**
     * @see User#getId()
     */
    UserBuilder id(String id);

    /**
     * @see User#getName()
     */
    UserBuilder name(String name);

    /**
     * @see User#getDomainId()
     */
    UserBuilder domainId(String domainId);

    /**
     * Accepts an existing domain and uses its id
     *
     * @see User#getDomain()
     */
    UserBuilder domain(Domain domain);

    /**
     * @see User#getEmail()
     */
    UserBuilder email(String email);

    /**
     * @see User#getLinks()
     */
    UserBuilder links(Map<String, String> links);

    /**
     * @see User#getPassword()
     */
    UserBuilder password(String password);

    /**
     * @see User#isEnabled()
     */
    UserBuilder enabled(boolean enabled);

}
