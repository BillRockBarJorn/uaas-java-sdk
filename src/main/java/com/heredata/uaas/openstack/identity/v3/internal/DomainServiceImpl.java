package com.heredata.uaas.openstack.identity.v3.internal;


import com.heredata.uaas.api.identity.v3.DomainService;
import com.heredata.uaas.model.common.ActionResponse;
import com.heredata.uaas.model.identity.v3.Domain;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneDomain;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneDomain.Domains;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.heredata.uaas.core.transport.ClientConstants.PATH_DOMAINS;

public class DomainServiceImpl extends BaseIdentityServices implements DomainService {

    @Override
    public Domain create(Domain domain) {
        checkNotNull(domain);
        return post(KeystoneDomain.class, PATH_DOMAINS).entity(domain).execute();
    }

    @Override
    public Domain create(String name, String description, boolean enabled) {
        checkNotNull(name);
        checkNotNull(description);
        checkNotNull(enabled);
        return create(KeystoneDomain.builder().name(name).description(description).enabled(enabled).build());
    }

    @Override
    public Domain update(Domain domain) {
        checkNotNull(domain);
        return patch(KeystoneDomain.class, PATH_DOMAINS, "/", domain.getId()).entity(domain).execute();
    }

    @Override
    public Domain get(String domainId) {
        checkNotNull(domainId);
        return get(KeystoneDomain.class, PATH_DOMAINS, "/", domainId).execute();
    }

    @Override
    public List<? extends Domain> getByName(String domainName) {
        checkNotNull(domainName);
        return get(Domains.class, uri(PATH_DOMAINS)).param("name", domainName).execute().getList();
    }

    @Override
    public ActionResponse delete(String domainId) {
        checkNotNull(domainId);
        return deleteWithResponse(PATH_DOMAINS, "/", domainId).execute();
    }

    @Override
    public List<? extends Domain> list() {
        return get(Domains.class, uri(PATH_DOMAINS)).execute().getList();
    }

}
