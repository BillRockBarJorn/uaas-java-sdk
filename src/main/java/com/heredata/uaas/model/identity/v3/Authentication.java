package com.heredata.uaas.model.identity.v3;

import com.heredata.uaas.model.ModelEntity;
import com.heredata.uaas.model.ResourceEntity;

import java.util.List;

public interface Authentication extends ModelEntity {

    Identity getIdentity();

    Scope getScope();

    public interface Identity {

        Password getPassword();

        Token getToken();

        List<String> getMethods();

        public interface Password {

            User getUser();

            public interface User extends ResourceEntity {

                Domain getDomain();

                String getPassword();

                public interface Domain extends ResourceEntity {
                }
            }
        }

        public interface Token {

            String getId();

        }

    }

    public interface Scope {

        Project getProject();

        Domain getDomain();

        public interface Project extends ResourceEntity {

            Domain getDomain();
        }

        public interface Domain extends ResourceEntity {
        }

    }

}
