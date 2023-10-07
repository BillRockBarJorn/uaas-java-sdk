package com.heredata.hos.model;

import com.alibaba.fastjson.JSONObject;
import com.heredata.utils.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

/**
 * <p>Title: KeyInformation</p>
 * <p>Description: 账户类信息实体，创建连接，加密对象时使用 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/25 14:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class KeyInformation {
    /**
     * HOS服务的请求路由前缀
     */
    private String endPoint;
    /**
     * UAAS服务返回的accessKey
     */
    private String accessKey;
    /**
     * UAAS服务返回的secretKey
     */
    private String secretKey;
    /**
     * 账户名称
     */
    private String account;
    /**
     * 账户名称对应的账户ID
     */
    private String accountId;
    /**
     * 租户的ID
     */
    private String userId;
    /**
     * UAAS服务返回的token
     */
    private String xSubjectToken;
    /**
     * HKMS服务返回的秘钥ID
     */
    private String secretId;

    /**
     * @Title: 设置关键信息
     * @Description: 为对象赋值，包括accessKey,secretKey,accountId,userId,xSubjectToken,account属性值
     * @params [userName, userPassword, scopeName, uaasURL]
     * @return void
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/25 14:23
     */
    public void setKeyInformation(String userName, String userPassword, String scopeName, String uaasURL) throws URISyntaxException, IOException {
        CloseableHttpClient build = HttpClients.custom().build();

        String bodyStr = "{\"auth\":{\"identity\":{\"methods\":[\"password\"],\"password\":{\"user\":{\"name\":\"" + userName + "\",\"domain\":{\"name\":\"default\"},\"password\":\"" + userPassword + "\"}}},\"scope\":{\"project\":{\"domain\":{\"name\":\"default\"},\"name\":\"" + scopeName + "\"}}}}";

        HttpPost httpPost = new HttpPost();
        httpPost.setHeader("Auth-S3-Required", "Y");
        httpPost.setURI(new URI(uaasURL));
        ByteArrayEntity byteArrayEntity = new ByteArrayEntity(bodyStr.getBytes(StandardCharsets.UTF_8), ContentType.APPLICATION_JSON);
        httpPost.setEntity(byteArrayEntity);

        CloseableHttpResponse execute = build.execute(httpPost);

        InputStream content = execute.getEntity().getContent();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] b = new byte[10240];
        int n;
        while ((n = content.read(b)) != -1) {
            stream.write(b, 0, n);
        }

        JSONObject parse = JSONObject.parseObject(stream.toString());
        accessKey = parse.getJSONObject("s3").getString("access_key");
        secretKey = parse.getJSONObject("s3").getString("secret_key");
        accountId = parse.getJSONObject("token").getJSONObject("project").getString("id");
        userId = parse.getJSONObject("token").getJSONObject("user").getString("id");
        xSubjectToken = execute.getHeaders("x-subject-token")[0].getValue();
        account = "HOS_" + accountId;
    }


    /**
     * @Title: 设置HKMS服务返回的秘钥Id
     * @Description: 设置HKMS服务返回的秘钥Id
     * @params [secretKey, expirationDate, secretURL]
     * @return void
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/25 14:24
     */
    public void setSecretId(String secretKey, Date expirationDate, String secretURL) throws URISyntaxException, IOException {
        JSONObject body = new JSONObject();
        body.put("name", "AES key");
        body.put("expiration", DateUtil.formatIso8601Date(expirationDate));
        body.put("algorithm", "aes");
        body.put("bit_length", 256);
        body.put("mode", "cbc");
        body.put("payload", Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8)));
        body.put("payload_content_type", "application/octet-stream");
        body.put("payload_content_encoding", "base64");

        CloseableHttpClient build = HttpClients.custom().build();

        HttpPost httpPost = new HttpPost();
        httpPost.setHeader("x-auth-token", xSubjectToken);
        httpPost.setURI(new URI(secretURL));
        StringEntity stringEntity = new StringEntity(body.toString());
        stringEntity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        httpPost.setEntity(stringEntity);


        CloseableHttpResponse execute = build.execute(httpPost);

        InputStream content = execute.getEntity().getContent();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] b = new byte[10240];
        int n;
        while ((n = content.read(b)) != -1) {
            stream.write(b, 0, n);
        }

        if (execute.getStatusLine().getStatusCode() == 201) {
            JSONObject result = JSONObject.parseObject(stream.toString());
            int index = result.getString("secret_ref").lastIndexOf("/");
            secretId = result.getString("secret_ref").substring(index + 1);
        }
    }

}
