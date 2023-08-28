package com.heredata.uaas.openstack.common;


import com.heredata.uaas.model.ModelEntity;

public interface Auth extends ModelEntity {

	public enum Type { CREDENTIALS, TOKEN, RAX_APIKEY, TOKENLESS }

}
