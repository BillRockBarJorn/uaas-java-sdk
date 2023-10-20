package com.heredata.hos.signer;


import com.heredata.signer.RequestSigner;
import com.heredata.comm.RequestMessage;
import com.heredata.exception.ClientException;
import com.heredata.auth.Credentials;
import com.heredata.hos.comm.HOSHeaders;
import com.heredata.utils.DateUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

import static com.heredata.HttpHeaders.CONTENT_MD5;
import static com.heredata.HttpHeaders.CONTENT_TYPE;
import static com.heredata.hos.comm.HOSHeaders.HOS_PREFIX;
import static com.heredata.hos.comm.HOSHeaders.X_HOS_DATE;
import static com.heredata.hos.signer.SignParameters.NEW_LINE;
import static com.heredata.hos.signer.SignParameters.SUB_RESOURCES;
import static com.heredata.utils.StringUtils.DEFAULT_ENCODING;
import static com.heredata.utils.StringUtils.isNullOrEmpty;

/**
 * <p>Title: HOSUaasSigner</p>
 * <p>Description: UAAS服务认证签名计算 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:02
 */
public class HOSUaasSigner implements RequestSigner {

    private String date;
    protected final HOSSignerParams signerParams;

    public HOSUaasSigner(HOSSignerParams signerParams) {
        this.signerParams = signerParams;
    }

    protected void addSecurityTokenHeaderIfNeeded(RequestMessage request) {
        Credentials cred = signerParams.getCredentials();
        if (cred.useAccount() && !request.isUseUrlSignature()) {
            request.addHeader(HOSHeaders.HOS_SECURITY_TOKEN, cred.getAccount());
        }
    }

    protected boolean isAnonymous() {
        Credentials cred = signerParams.getCredentials();
        if (cred.getAccessKey().length() > 0 && cred.getSecretKey().length() > 0) {
            return false;
        }
        return true;
    }

    public String hamcSha1(String input) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        SecretKeySpec signingKey = new SecretKeySpec(signerParams.getCredentials().getSecretKey().getBytes(DEFAULT_ENCODING), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);

        return Base64.getEncoder().encodeToString(mac.doFinal(input.getBytes(DEFAULT_ENCODING)));
    }

    private String stringToSign(RequestMessage requestMessage) {
        Map<String, String> headMap = requestMessage.getHeaders();
        String contentMd5 = headMap.containsKey(CONTENT_MD5) ? headMap.get(CONTENT_MD5) : "";
        String contentType = headMap.containsKey(CONTENT_TYPE) ? headMap.get(CONTENT_TYPE) : "";

        /** 利用treeMap来完成key的字典排序 */
        TreeMap<String, String> canonicalizedHeaders = new TreeMap<>();

        headMap.forEach((k, v) -> {
            if (k.startsWith(HOS_PREFIX)) {
                canonicalizedHeaders.put(k, v);
            }
        });

        // 如果有自定义时间头，则需要将自带的时间头置空
        if (canonicalizedHeaders.containsKey(X_HOS_DATE)) {
            date = "";
        }

        // handler method/content-md5/content-type/date
        StringBuilder stringToSign = new StringBuilder();
        stringToSign.append(requestMessage.getMethod()).append(NEW_LINE)
                .append(contentMd5).append(NEW_LINE)
                .append(contentType).append(NEW_LINE)
                .append(date).append(NEW_LINE);

        // handler canonicalizedHeaders
        for (Map.Entry<String, String> entry : canonicalizedHeaders.entrySet()) {
            stringToSign.append(entry.getKey()).append(":").append(entry.getValue()).append(NEW_LINE);
        }

        // 拼接资源路径
        String resourcePath = signerParams.getResourcePath();
        if (resourcePath.length() != 0 && resourcePath.charAt(resourcePath.length() - 1) == '/') {
            resourcePath = resourcePath.substring(0, resourcePath.length() - 1);
            if (resourcePath.length() == 0) {
                resourcePath = "/";
            }
        }
        stringToSign.append(resourcePath);

        Map<String, String> parameters = requestMessage.getParameters();
        TreeMap<String, String> canonicalizedResource = new TreeMap<>();
        parameters.forEach((k, v) -> {
            if (SUB_RESOURCES.contains(k)) {
                canonicalizedResource.put(k, isNullOrEmpty(parameters.get(k)) ? "" : parameters.get(k));
            }
        });

        if (canonicalizedResource.size() > 0) {
            stringToSign.append("?");
            for (String key : canonicalizedResource.keySet()) {
                // 判断是否添加&
                if ('?' != stringToSign.charAt(stringToSign.length() - 1) && '&' != stringToSign.charAt(stringToSign.length() - 1)) {
                    stringToSign.append("&");
                }
                // 添加key
                stringToSign.append(key);
                // 添加value
                if (!isNullOrEmpty(canonicalizedResource.get(key))) {
                    stringToSign.append("=").append(canonicalizedResource.get(key));
                }
            }
        }
        return stringToSign.toString();
    }

    private void addDateHeaderIfNeeded(RequestMessage request) {
        date = DateUtil.getGMT();
        request.getHeaders().put(HOSHeaders.HOS_DATE, date);
    }


    protected void addAuthorizationHeader(RequestMessage request) {
        String stringToSign = stringToSign(request);
        String signature = null;
        try {
            signature = this.hamcSha1(stringToSign);
        } catch (Exception e) {
            throw new ClientException("hamcSha1 error " + e.getMessage());
        }
        String authorization = String.format("HOS %s:%s", signerParams.getCredentials().getAccessKey(), signature);
        request.addHeader(HOSHeaders.AUTHORIZATION, authorization);
        request.addHeader("api-style", "HOS");
    }

    @Override
    public void sign(RequestMessage request) throws ClientException {
        addDateHeaderIfNeeded(request);
        if (isAnonymous()) {
            return;
        }
        addAuthorizationHeader(request);
    }
}
