package com.heredata.uaas.identity.v3;

import com.heredata.uaas.AbstractTest;
import com.heredata.uaas.api.OSClient;
import com.heredata.uaas.api.exceptions.OS4JException;
import com.heredata.uaas.api.exceptions.ResponseException;
import com.heredata.uaas.api.identity.v3.IdentityService;
import com.heredata.uaas.model.common.ActionResponse;
import com.heredata.uaas.model.identity.v3.Token;
import com.heredata.uaas.openstack.OSFactory;
import com.heredata.uaas.openstack.common.AccessKey;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneAuth;
import com.heredata.uaas.openstack.identity.v3.domain.KeystoneToken;
import org.junit.Test;


/**
 * TODO
 * @author wuzz
 * @since 2022/9/5
 */
public class AuthTokenTest extends AbstractTest {

    /**
     * 创建令牌
     */
    @Test
    public void createToken() {
        try {
            // 构造token对象，设置ip+端口+请求路径统一前缀
            KeystoneToken keystoneToken = new KeystoneToken(endpoint);
            // 根据token创建V3连接  KeystoneToken对象实例+用户名+用户名密码+账户名
            OSClient.OSClientV3 osClientV3 = OSFactory.clientFromToken(keystoneToken, userName, password, accountName);
            // 打印令牌
            System.out.println(osClientV3.getToken());
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    /**
     * 检查令牌是否有效
     */
    @Test
    public void checkToken() {
        try {
            // 创建OSClientV3连接
            OSClient.OSClientV3 osClientV3 = getOSClientV3();
            // 获取uaas服务返回的token对象
            Token token = osClientV3.getToken();
            // 获取功能接口
            IdentityService identity = osClientV3.identity();
            // 检查令牌是否有效
            ActionResponse check = identity.tokens().check(token.getId());
            System.out.println(check);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void createS3Keys() {
        try {
            // 创建OSClientV3连接
            OSClient.OSClientV3 osClientV3 = getOSClientV3();
            // 获取uaas服务端返回的token对象
            Token token = osClientV3.getToken();
            // 获取功能接口
            IdentityService identity = osClientV3.identity();
            // 创建s3 Access Key
            KeystoneToken.TokenS3 s3Keys = identity.tokens().createS3Keys((KeystoneAuth) token.getCredentials());
            System.out.println(s3Keys);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void deleteS3Key() {
        try {
            // 创建OSClientV3连接
            OSClient.OSClientV3 osClientV3 = getOSClientV3();
            // 获取uaas服务端返回的token对象
            Token token = osClientV3.getToken();
            // 获取功能接口
            IdentityService identity = osClientV3.identity();
            // 创建s3 Access Key
            KeystoneToken.TokenS3 s3Keys = identity.tokens().createS3Keys((KeystoneAuth) token.getCredentials());

            // 删除s3 accessKey secretKey
            ActionResponse actionResponse = identity.tokens().deleteS3Key(s3Keys.getKeystoneToken().getId(), s3Keys.getS3Key().getAccessKey());
            System.out.println(actionResponse);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    @Test
    public void getS3SecretKey() {
        try {
            // 创建OSClientV3连接
            OSClient.OSClientV3 osClientV3 = getOSClientV3();
            // 获取uaas服务端返回的token对象
            Token token = osClientV3.getToken();
            // 获取功能接口
            IdentityService identity = osClientV3.identity();
            // 创建s3 Access Key
            KeystoneToken.TokenS3 s3Keys = identity.tokens().createS3Keys((KeystoneAuth) token.getCredentials());
            // 根据accessKey获取SecretKey
            KeystoneToken.TokenS3 result = identity.tokens().getS3SecretKey(s3Keys.getKeystoneToken().getId(), s3Keys.getS3Key().getAccessKey());
            System.out.println(result);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }

    /**
     * TODO
     * 申请永久AccessKey和SecrectKey,没调通，暂时找不到原因
     */
    @Test
    public void getPerminerKey() {
        try {
            // 创建OSClientV3连接
            OSClient.OSClientV3 osClientV3 = getOSClientV3();
            // 获取uaas服务端返回的token对象
            Token token = osClientV3.getToken();
            // 获取功能接口
            IdentityService identity = osClientV3.identity();
            // 创建s3 Access Key
            AccessKey accessKey = identity.tokens().createAccessKey("epdoc_test_usr1"
                    , "38664062131b11eea8b689966c9575e8", "description");
            System.out.println(accessKey);
        } catch (ResponseException re) {
            System.out.println("Error Message:" + re.getMessage());
            System.out.println("Error Code:" + re.getStatus());
        } catch (OS4JException oe) {
            System.out.println(oe.getMessage());
        }
    }
}
