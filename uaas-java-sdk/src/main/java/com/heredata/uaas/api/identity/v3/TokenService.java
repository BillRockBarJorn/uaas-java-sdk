package com.heredata.uaas.api.identity.v3;

import com.heredata.uaas.common.RestService;
import com.heredata.uaas.model.common.ActionResponse;
import com.heredata.uaas.model.identity.v3.Domain;
import com.heredata.uaas.model.identity.v3.Project;
import com.heredata.uaas.model.identity.v3.Service;
import com.heredata.uaas.model.identity.v3.Token;
import com.heredata.uaas.openstack.common.AccessKey;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneAuth;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneToken;

import java.util.List;

/**
 * Identity V3 Token operations
 *
 */
public interface TokenService extends RestService {
    /***
     * Validates and shows information for a token.
     *
     * @return the token if valid
     */
    Token get(KeystoneAuth keystoneAuth);

    /***
     * Validates and shows information for a token.
     *
     * @param tokenId the identifier of the token that is subject to be checked
     * @return the token if valid
     */
    Token get(String tokenId);

    /**
     * Validates a token.
     *
     * @param tokenId the identifier of the token that is subject to be checked
     * @return the ActionResponse
     */
    ActionResponse check(String tokenId);

    /**
     * Revokes a token.
     *
     * @param tokenId the identifier of the token that is going to be deleted
     * @return the ActionResponse
     */
    ActionResponse delete(String tokenId);

    /**
     * Get service catalog for specified token
     *
     * @param tokenId the identifier of the token, of which the catalog of services is requested
     * @return the service catalog for the token provided in the request
     */
    List<? extends Service> getServiceCatalog(String tokenId);

    /**
     * Get available project scopes for specified token
     *
     * @param tokenId the identifier of the token in question
     * @return list of projects that are available to be scoped to
     */
    List<? extends Project> getProjectScopes(String tokenId);

    /**
     * Get available domain scopes for specified token
     *  @param tokenId the identifier of the token in question
     *  @return list of domains that are available to be scoped to
     */
    List<? extends Domain> getDomainScopes(String tokenId);

    /**
     * 创建S3令牌，临时AccessKey和SecretKey
     * @param keystoneAuth
     * @return
     */
    KeystoneToken.TokenS3 createS3Keys(KeystoneAuth keystoneAuth);

    /**
     * 删除S3令牌
     * @param tokenId
     * @param accessKey
     * @return
     */
    ActionResponse deleteS3Key(String tokenId, String accessKey);

    /**
     * 获取申请的S3令牌
     * @param tokenId
     * @param accessKey
     * @return
     */
    KeystoneToken.TokenS3 getS3SecretKey(String tokenId, String accessKey);

    ActionResponse createPublicKey(String tokenId);

    AccessKey createAccessKey(String userName, String projectId, String description);
}
