package com.heredata.uaas.openstack.identity.v3.internal;


import com.heredata.uaas.api.types.ServiceType;
import com.heredata.uaas.openstack.common.functions.EnforceVersionToURL;
import com.heredata.uaas.openstack.internal.BaseOpenStackService;

/**
 * Base Identity Operations Implementation is responsible for insuring the proper endpoint is used for all extending operation APIs
 *
 * @author wuzz
 */
public class BaseIdentityServices extends BaseOpenStackService {

        protected BaseIdentityServices() {
                super(ServiceType.IDENTITY, EnforceVersionToURL.instance("/v3"));
        }
}

