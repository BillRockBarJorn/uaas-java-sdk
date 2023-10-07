package com.heredata.model;

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
 * 账户关键信息类
 * @author wuzz
 * @since 2022/8/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class KeyInformation {
    private String endPoint;
    private String accessKey;
    private String secretKey;
    private String account;
    private String accountId;
    private String userId;
    private String xSubjectToken;
    private String secretId;

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
        accessKey = parse.getJSONObject("s3").getString("access_key");
        secretKey = parse.getJSONObject("s3").getString("secret_key");
        accountId = parse.getJSONObject("token").getJSONObject("project").getString("id");
        userId = parse.getJSONObject("token").getJSONObject("user").getString("id");
        xSubjectToken = execute.getHeaders("x-subject-token")[0].getValue();
        account = "HOS_" + accountId;
    }


    /**
     *
     * @param secretKey
     * @param expirationDate
     * @return
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
