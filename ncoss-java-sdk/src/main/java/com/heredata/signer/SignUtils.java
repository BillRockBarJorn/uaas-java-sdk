package com.heredata.signer;


import com.heredata.HttpHeaders;
import com.heredata.comm.RequestMessage;
import com.heredata.hos.comm.HOSHeaders;
import com.heredata.hos.signer.SignParameters;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import static com.heredata.hos.signer.SignParameters.AUTHORIZATION_PREFIX;
import static com.heredata.utils.CodingUtils.assertTrue;

/**
 * <p>Title: SignUtils</p>
 * <p>Description: 签名工具类 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:04
 */
public class SignUtils {

    public static String composeRequestAuthorization(String accessKeyId, String signature) {
        return AUTHORIZATION_PREFIX + accessKeyId + ":" + signature;
    }

    public static String buildCanonicalString(String method, String resourcePath, RequestMessage request,
                                              String expires) {

        StringBuilder canonicalString = new StringBuilder();
        canonicalString.append(method).append(SignParameters.NEW_LINE);

        Map<String, String> headers = request.getHeaders();
        TreeMap<String, String> headersToSign = new TreeMap<String, String>();

        if (headers != null) {
            for (Entry<String, String> header : headers.entrySet()) {
                if (header.getKey() == null) {
                    continue;
                }

                String lowerKey = header.getKey().toLowerCase();
                if (lowerKey.equals(HttpHeaders.CONTENT_TYPE.toLowerCase())
                        || lowerKey.equals(HttpHeaders.CONTENT_MD5.toLowerCase())
                        || lowerKey.equals(HttpHeaders.DATE.toLowerCase())
                        || lowerKey.startsWith(HOSHeaders.HOS_PREFIX)) {
                    headersToSign.put(lowerKey, header.getValue().trim());
                }
            }
        }

        if (!headersToSign.containsKey(HttpHeaders.CONTENT_TYPE.toLowerCase())) {
            headersToSign.put(HttpHeaders.CONTENT_TYPE.toLowerCase(), "");
        }
        if (!headersToSign.containsKey(HttpHeaders.CONTENT_MD5.toLowerCase())) {
            headersToSign.put(HttpHeaders.CONTENT_MD5.toLowerCase(), "");
        }

        // Append all headers to sign to canonical string
        for (Entry<String, String> entry : headersToSign.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (key.startsWith(HOSHeaders.HOS_PREFIX)) {
                canonicalString.append(key).append(':').append(value);
            } else {
                canonicalString.append(value);
            }

            canonicalString.append(SignParameters.NEW_LINE);
        }

        // Append canonical resource to canonical string
        canonicalString.append(buildCanonicalizedResource(resourcePath, request.getParameters()));

        return canonicalString.toString();
    }

    public static String buildRtmpCanonicalString(String canonicalizedResource, RequestMessage request,
                                                  String expires) {

        StringBuilder canonicalString = new StringBuilder();

        // Append expires
        canonicalString.append(expires + SignParameters.NEW_LINE);

        // Append canonicalized parameters
        for (Entry<String, String> entry : request.getParameters().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            canonicalString.append(key).append(':').append(value);
            canonicalString.append(SignParameters.NEW_LINE);
        }

        // Append canonicalized resource
        canonicalString.append(canonicalizedResource);

        return canonicalString.toString();
    }

    public static String buildCanonicalizedResource(String resourcePath, Map<String, String> parameters) {
        assertTrue(resourcePath.startsWith("/"), "Resource path should start with slash character");

        StringBuilder builder = new StringBuilder();
        builder.append(resourcePath);

        if (parameters != null) {
            String[] parameterNames = parameters.keySet().toArray(new String[parameters.size()]);
            Arrays.sort(parameterNames);

            char separator = '?';
            for (String paramName : parameterNames) {
                if (!SignParameters.SUB_RESOURCES.contains(paramName)) {
                    continue;
                }

                builder.append(separator);
                builder.append(paramName);
                String paramValue = parameters.get(paramName);
                if (paramValue != null) {
                    builder.append("=").append(paramValue);
                }

                separator = '&';
            }
        }

        return builder.toString();
    }
}
