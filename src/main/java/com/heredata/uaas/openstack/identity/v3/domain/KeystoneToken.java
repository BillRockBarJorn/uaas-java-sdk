package com.heredata.uaas.openstack.identity.v3.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.base.MoreObjects;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import com.heredata.uaas.model.identity.AuthStore;
import com.heredata.uaas.model.identity.AuthVersion;
import com.heredata.uaas.model.identity.v3.Service;
import com.heredata.uaas.model.identity.v3.Token;
import com.heredata.uaas.openstack.common.HeadResult;
import com.heredata.uaas.openstack.common.S3Key;

import java.util.Date;
import java.util.List;

import static com.heredata.uaas.core.transport.ClientConstants.CONTENT_HEAD_JSON_ROOT_NAME;
import static com.heredata.uaas.openstack.identity.functions.ServiceFunctions.TYPE_WITHOUT_VERSION;


@JsonRootName("token")
@JsonIgnoreProperties(ignoreUnknown = true)
public class KeystoneToken implements Token {

    private static final long serialVersionUID = 1L;

    public final String jsonRootName = "token";

    @JsonIgnore
    private String id;
    @JsonProperty("expires_at")
    private Date expires;
    @JsonProperty("issued_at")
    private Date issued;
    @JsonProperty("audit_ids")
    private List<String> auditIds;
    @JsonProperty
    private List<String> methods;
    @JsonProperty
    private List<KeystoneRole> roles;
    @JsonProperty(required = false)
    private KeystoneProject project;
    @JsonProperty(required = false)
    private KeystoneDomain domain;
    @JsonProperty
    private KeystoneUser user;
    @JsonProperty
    private List<KeystoneService> catalog;
    @JsonProperty(value = "auth")
    private KeystoneAuth credentials;
    private String endpoint;
    @JsonIgnore
    private String extras;
    @JsonIgnore
    private volatile SortedSetMultimap<String, Service> aggregatedCatalog;
    @JsonProperty(required = false, value = CONTENT_HEAD_JSON_ROOT_NAME)
    private HeadResult head;

    public KeystoneToken() {
    }

    public KeystoneToken(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).omitNullValues()
                .add("id", id)
                .add("expires", expires)
                .add("issued", issued)
                .add("auditIds", auditIds)
                .add("methods", methods)
                .add("user", user)
                .add("roles", roles)
                .add("project", project)
                .add("domain", domain)
                .addValue("\n")
                .add("catalog", catalog)
                .toString();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Date getExpires() {
        return expires;
    }

    @Override
    public AuthVersion getVersion() {
        return AuthVersion.V3;
    }

    @Override
    public Date getIssuedAt() {
        return issued;
    }

    @Override
    public List<String> getAuditIds() {
        return auditIds;
    }

    @Override
    public List<String> getMethods() {
        return methods;
    }

    @Override
    public List<? extends KeystoneService> getCatalog() {
        return catalog;
    }

    @Override
    public KeystoneProject getProject() {
        return project;
    }

    @Override
    public KeystoneDomain getDomain() {
        return domain;
    }

    @Override
    public KeystoneUser getUser() {
        return user;
    }

    @Override
    public List<? extends KeystoneRole> getRoles() {
        return roles;
    }

    @Override
    public KeystoneAuth getCredentials() {
        return credentials;
    }

    @Override
    public String getEndpoint() {
        return endpoint;
    }

    @Override
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public HeadResult getHead() {
        return head;
    }

    public void setHead(HeadResult head) {
        this.head = head;
    }

    public KeystoneToken applyContext(String endpoint, KeystoneAuth credentials) {
        this.credentials = credentials;
        this.endpoint = endpoint;
        return this;
    }

    public KeystoneToken applyContext(KeystoneAuth credentials) {
        this.credentials = credentials;
        return this;
    }

    public KeystoneToken applyContext(String endpoint, TokenAuth token) {
        this.endpoint = endpoint;
        return this;
    }

    public KeystoneToken applyContext(String endpoint, AuthStore credentials) {
        this.endpoint = endpoint;
        this.credentials = new KeystoneAuth(endpoint, credentials.getPassword());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public SortedSetMultimap<String, Service> getAggregatedCatalog() {
        if (aggregatedCatalog == null) {
            synchronized (this) {
                if (aggregatedCatalog == null) {
                    aggregatedCatalog = TreeMultimap.create();
                    for (Service sc : catalog) {
                        String nameKey = TYPE_WITHOUT_VERSION.apply(sc.getName());
                        String typeKey = TYPE_WITHOUT_VERSION.apply(sc.getType());
                        aggregatedCatalog.put(nameKey, sc);
                        aggregatedCatalog.put(typeKey, sc);
                    }
                }
            }
        }
        return aggregatedCatalog;
    }

    @JsonIgnore
    @Override
    public String getCacheIdentifier() {
        String uniq = null;
        if (getProject() != null) {
            uniq = getProject().getId();
        } else if (getDomain() != null) {
            uniq = getDomain().getId();
        } else if (user != null) {
            uniq = user.getId();
        } else {
            uniq = "";
        }
        return String.format("%s:%s", endpoint, uniq);
    }

    public static class TokenS3 {

        private static final long serialVersionUID = 1L;
        //        public static final List<String> jsonRootNames = Collections.addAll(new ArrayList<>(2), "token", "s3");
        public final String jsonRootName = "token";
        @JsonProperty("token")
        private KeystoneToken keystoneToken;

        @JsonProperty("s3")
        private S3Key s3Key;

        public KeystoneToken getKeystoneToken() {
            return keystoneToken;
        }

        public void setKeystoneToken(KeystoneToken keystoneToken) {
            this.keystoneToken = keystoneToken;
        }

        public S3Key getS3Key() {
            return s3Key;
        }

        public void setS3Key(S3Key s3Key) {
            this.s3Key = s3Key;
        }

        @Override
        public String toString() {
            return "TokenS3{" +
                    "keystoneToken=" + keystoneToken +
                    ", s3Key=" + s3Key +
                    '}';
        }
    }
}
