package com.heredata.swift.model;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

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
     * SWIFT服务的请求路由前缀
     */
    private String endPoint;

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
     * 设置关键信息
     * @throws URISyntaxException
     * @throws IOException
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
        accountId = parse.getJSONObject("token").getJSONObject("project").getString("id");
        userId = parse.getJSONObject("token").getJSONObject("user").getString("id");
        xSubjectToken = execute.getHeaders("x-subject-token")[0].getValue();
        account = "AUTH_" + accountId;
    }
}
