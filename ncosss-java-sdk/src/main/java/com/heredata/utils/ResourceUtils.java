package com.heredata.utils;

import com.heredata.ResponseMessage;
import com.heredata.exception.InconsistentException;
import com.heredata.model.Callback;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.heredata.comm.Constants.RESOURCE_NAME_COMMON;
import static com.heredata.comm.HttpConstants.DEFAULT_CHARSET_NAME;

/**
 * <p>Title: ResourceUtils</p>
 * <p>Description: 静态资源工具类 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 15:39
 */
public class ResourceUtils {

    public static final ResourceManager COMMON_RESOURCE_MANAGER = ResourceManager.getInstance(RESOURCE_NAME_COMMON);

    private static final String BUCKET_NAMING_CREATION_REGEX = "^[a-z0-9][a-z0-9-]{1,61}[a-z0-9]$";
    private static final String BUCKET_NAMING_REGEX = "^[a-z0-9][a-z0-9-_]{1,61}[a-z0-9]$";
    private static final String ENDPOINT_REGEX = "^[a-zA-Z0-9._-]+$";


    public static ResourceManager createInstance(String ss) {
        return ResourceManager.getInstance(ss);
    }

    /**
     * Validate endpoint.
     */
    public static boolean validateEndpoint(String endpoint) {
        if (endpoint == null) {
            return false;
        }
        return endpoint.matches(ENDPOINT_REGEX);
    }

    /**
     * Make a resource path from the object key, used when the bucket name
     * pearing in the endpoint.
     */
    public static String makeResourcePath(String key) {
        return key != null ? urlEncodeKey(key) : null;
    }

    /**
     * Make a resource path from the bucket name and the object key.
     */
    public static String makeResourcePath(String bucket, String key) {
        if (bucket != null) {
            return bucket + "/" + (key != null ? urlEncodeKey(key) : "");
        } else {
            return null;
        }
    }

    /**
     * Make a resource path from the bucket name and the object key.
     */
    public static String makeResourcePath(String account, String bucket, String key) {
        if (bucket != null) {
            return account + "/" + (bucket != null ? urlEncodeKey(bucket) : "") + (key != null ? ("/" + urlEncodeKey(key)) : "");
        } else {
            return account;
        }
    }

    /**
     * Encode a URL segment with special chars replaced.
     */
    public static String urlEncode(String value, String encoding) {
        if (value == null) {
            return "";
        }

        try {
            String encoded = URLEncoder.encode(value, encoding);
            return encoded.replace("+", "%20").replace("*", "%2A").replace("~", "%7E").replace("/", "%2F");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(COMMON_RESOURCE_MANAGER.getFormattedString("FailedToEncodeUri", e.toString()));
        }
    }

    /**
     * Encode object URI.
     */
    public static String urlEncodeKey(String key) {
        if (key.startsWith("/")) {
            return urlEncode(key, DEFAULT_CHARSET_NAME);
        }

        StringBuffer resultUri = new StringBuffer();

        String[] keys = key.split("/");
        resultUri.append(urlEncode(keys[0], DEFAULT_CHARSET_NAME));
        for (int i = 1; i < keys.length; i++) {
            resultUri.append("/").append(urlEncode(keys[i], DEFAULT_CHARSET_NAME));
        }

        if (key.endsWith("/")) {
            // String#split ignores trailing empty strings,
            // e.g., "a/b/" will be split as a 2-entries array,
            // so we have to append all the trailing slash to the uri.
            for (int i = key.length() - 1; i >= 0; i--) {
                if (key.charAt(i) == '/') {
                    resultUri.append("/");
                } else {
                    break;
                }
            }
        }

        return resultUri.toString();
    }


    public static void addHeader(Map<String, String> headers, String header, String value) {
        if (value != null) {
            headers.put(header, value);
        }
    }

    public static void addDateHeader(Map<String, String> headers, String header, Date value) {
        if (value != null) {
            headers.put(header, DateUtil.formatRfc822Date(value));
        }
    }

    public static void addStringListHeader(Map<String, String> headers, String header, List<String> values) {
        if (values != null && !values.isEmpty()) {
            headers.put(header, join(values));
        }
    }

    public static void removeHeader(Map<String, String> headers, String header) {
        if (header != null && headers.containsKey(header)) {
            headers.remove(header);
        }
    }

    public static String join(List<String> strings) {

        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (String s : strings) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(s);

            first = false;
        }

        return sb.toString();
    }

    public static void safeCloseResponse(ResponseMessage response) {
        try {
            response.close();
        } catch (IOException e) {
        }
    }

    public static void mandatoryCloseResponse(ResponseMessage response) {
        try {
            response.abort();
        } catch (IOException e) {
        }
    }

    public static long determineInputStreamLength(InputStream instream, long hintLength) {

        if (hintLength <= 0 || !instream.markSupported()) {
            return 0;
        }

        return hintLength;
    }

    /**
     * Encode the callback with JSON.
     */
    public static String jsonizeCallback(Callback callback) {
        StringBuffer jsonBody = new StringBuffer();

        jsonBody.append("{");
        // url, required
        jsonBody.append("\"callbackUrl\":" + "\"" + callback.getCallbackUrl() + "\"");

        // host, optional
        if (callback.getCallbackHost() != null && !callback.getCallbackHost().isEmpty()) {
            jsonBody.append(",\"callbackHost\":" + "\"" + callback.getCallbackHost() + "\"");
        }

        // body, require
        jsonBody.append(",\"callbackBody\":" + "\"" + callback.getCallbackBody() + "\"");

        // bodyType, optional
        if (callback.getCalbackBodyType() == Callback.CalbackBodyType.JSON) {
            jsonBody.append(",\"callbackBodyType\":\"application/json\"");
        } else if (callback.getCalbackBodyType() == Callback.CalbackBodyType.URL) {
            jsonBody.append(",\"callbackBodyType\":\"application/x-www-form-urlencoded\"");
        }
        jsonBody.append("}");

        return jsonBody.toString();
    }

    /**
     * Encode CallbackVar with Json.
     */
    public static String jsonizeCallbackVar(Callback callback) {
        StringBuffer jsonBody = new StringBuffer();

        jsonBody.append("{");
        for (Map.Entry<String, String> entry : callback.getCallbackVar().entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                if (!jsonBody.toString().equals("{")) {
                    jsonBody.append(",");
                }
                jsonBody.append("\"" + entry.getKey() + "\":\"" + entry.getValue() + "\" ");
            }
        }
        jsonBody.append("}");

        return jsonBody.toString();
    }

    /**
     * Ensure the callback is valid by checking its url and body are not null or
     * empty.
     */
    public static void ensureCallbackValid(Callback callback) {
        if (callback != null) {
            CodingUtils.assertStringNotNullOrEmpty(callback.getCallbackUrl(), "Callback.callbackUrl");
            CodingUtils.assertParameterNotNull(callback.getCallbackBody(), "Callback.callbackBody");
        }
    }

    /**
     * Put the callback parameter into header.
     */
//    public static void populateRequestCallback(Map<String, String> headers, Callback callback) {
//        if (callback != null) {
//            String jsonCb = jsonizeCallback(callback);
//            String base64Cb = BinaryUtil.toBase64String(jsonCb.getBytes());
//
//            headers.put(HOSHeaders.HOS_HEADER_CALLBACK, base64Cb);
//
//            if (callback.hasCallbackVar()) {
//                String jsonCbVar = jsonizeCallbackVar(callback);
//                String base64CbVar = BinaryUtil.toBase64String(jsonCbVar.getBytes());
//                base64CbVar = base64CbVar.replaceAll("\n", "").replaceAll("\r", "");
//                headers.put(HOSHeaders.HOS_HEADER_CALLBACK_VAR, base64CbVar);
//            }
//        }
//    }

    /**
     * Checks if HOS and SDK's checksum is same. If not, throws
     * InconsistentException.
     */
    public static void checkChecksum(Long clientChecksum, Long serverChecksum, String requestId) {
        if (clientChecksum != null && serverChecksum != null && !clientChecksum.equals(serverChecksum)) {
            throw new InconsistentException(clientChecksum, serverChecksum, requestId);
        }
    }

    public static URI toEndpointURI(String endpoint, String defaultProtocol) throws IllegalArgumentException {
        if (endpoint != null && !endpoint.contains("://")) {
            endpoint = defaultProtocol + "://" + endpoint;
        }

        try {
            return new URI(endpoint);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }


}
