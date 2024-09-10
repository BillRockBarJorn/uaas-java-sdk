package com.heredata.eics.utils;

import com.alibaba.fastjson.JSON;
import com.heredata.utils.StringUtils;
import com.sitech.cmap.fw.core.common.EmptyValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.nio.charset.StandardCharsets;

@Slf4j
public class HttpClientUtil {
    private static final String CODE_ENCODING = "UTF-8";
    private static PoolingHttpClientConnectionManager poolConnManager = null;

    /**
     * @author dingbr
     * @return 具有连接池的HttpClient
     */
    public static CloseableHttpClient getHttpClient() {

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(1000 * 60 * 60 * 12)
                .setConnectTimeout(1000 * 60 * 60 * 12)
                .setSocketTimeout(1000 * 60 * 60 * 12).build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(poolConnManager)
                .setDefaultRequestConfig(requestConfig).build();
        if (poolConnManager != null && poolConnManager.getTotalStats() != null) {
            log.info("now client pool "
                    + poolConnManager.getTotalStats().toString());
        }
        return httpClient;
    }

    /**
     * @author dingbr
     * @return CloseableHttpResponse
     * @edit  edit by yyh 在用post 请求时， 由于log 里边调用了post.getEntty() 导致报错 entity may not be null
     */
    public static CloseableHttpResponse  post(HttpPost post,Object object) {
        CloseableHttpClient client = getHttpClient();
        CloseableHttpResponse  res = null;
        try {
            if (StringUtils.isNullOrEmpty("Content-Type")){
                post.addHeader("Accept", "application/json");
            }
            if (StringUtils.isNullOrEmpty("Accept")){
                post.setHeader("Accept", "application/json");
            }
            if (EmptyValidator.isNotEmpty(object)){
                String body =  JSON.toJSON(object).toString();
                post.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
            }
            res = client.execute(post);
            int statusCode = res.getStatusLine().getStatusCode();
            if (post.getEntity() != null) {
                log.info("Post " + post.getURI() + " Data "
                        + EntityUtils.toString(post.getEntity()) + " ResultCode : " + statusCode);
            }else{
                log.info("Post " + post.getURI() + " ResultCode : " + statusCode);
            }
        } catch (Exception e) {
            log.info("Post " + post.getURI() + "Error!" + e.getMessage(), e);
        }
        return res;
    }

    /**
     * @author dingrb
     * @return CloseableHttpResponse
     */
    public static CloseableHttpResponse get(HttpGet get) {
        CloseableHttpClient client = getHttpClient();
        CloseableHttpResponse res = null;
        try {
            // 转码,防止中文乱码
            log.debug("Get " + get.getURI());
            res = client.execute(get);
            int statusCode = res.getStatusLine().getStatusCode();
            log.debug("Get " + get.getURI() + " ResultCode : " + statusCode);
        } catch (Exception e) {
            log.error("Get " + get.getURI() + "Error!" + e.getMessage(), e);
        }
        return res;
    }

    /**
     * @author dingrb
     * @return CloseableHttpResponse
     */
    public static CloseableHttpResponse delete(HttpDelete delete) {
        CloseableHttpClient client = getHttpClient();
        CloseableHttpResponse res = null;
        try {
            delete.addHeader("Accept", "application/json");// addHeader
            // 如果Header中没有定义则添加，如果已定义则保持原有value不改变。
            delete.addHeader("Content-Type", "application/json;charset="
                    + CODE_ENCODING); // addHeader
            // 如果Header中没有定义则添加，如果已定义则保持原有value不改变。

            // 转码,防止中文乱码
            log.debug("Delete " + delete.getURI());
            res = client.execute(delete);
            int statusCode = res.getStatusLine().getStatusCode();
            log.debug("Delete " + delete.getURI() + " ResultCode : "
                    + statusCode);
        } catch (Exception e) {
            log.error("Delete " + delete.getURI() + "Error!" + e.getMessage(), e);
        }
        return res;
    }

    /**
     * @author dingrb
     * @return CloseableHttpResponse
     */
    public static CloseableHttpResponse put(HttpPut put) {
        CloseableHttpClient client = getHttpClient();
        CloseableHttpResponse res = null;
        try {
            // 转码,防止中文乱码
            log.debug("Put " + put.getURI());
            res = client.execute(put);
            int statusCode = res.getStatusLine().getStatusCode();
            log.debug("Put " + put.getURI() + " ResultCode : " + statusCode);
        } catch (Exception e) {
            log.error("Put " + put.getURI() + "Error!" + e.getMessage(), e);
        }
        return res;
    }
}
