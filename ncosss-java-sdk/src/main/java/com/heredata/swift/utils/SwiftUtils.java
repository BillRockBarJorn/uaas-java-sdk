package com.heredata.swift.utils;

import com.heredata.ClientConfiguration;
import com.heredata.exception.InconsistentException;
import com.heredata.model.Callback;
import com.heredata.swift.model.ObjectMetadata;
import com.heredata.utils.DateUtil;
import com.heredata.utils.ResourceManager;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.heredata.comm.HttpConstants.DEFAULT_CHARSET_NAME;
import static com.heredata.swift.comm.SwiftConstants.OBJECT_NAME_MAX_LENGTH;
import static com.heredata.swift.comm.SwiftConstants.RESOURCE_NAME_SWIFT;
import static com.heredata.utils.ResourceUtils.urlEncodeKey;

/**
 * <p>Title: HOSUtils</p>
 * <p>Description: 依赖工具类 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 14:51
 */
public class SwiftUtils {

    public static final ResourceManager SWIFT_RESOURCE_MANAGER = ResourceManager.getInstance(RESOURCE_NAME_SWIFT);

    private static final String BUCKET_NAMING_REGEX = "^[a-z0-9][a-z0-9-_]{1,61}[a-z0-9]$";
    private static final String ENDPOINT_REGEX = "^[a-zA-Z0-9._-]+$";

    /**
     * Validate endpoint.
     */
    public static boolean validateEndpoint(String endpoint) {
        if (endpoint == null) {
            return false;
        }
        return endpoint.matches(ENDPOINT_REGEX);
    }

    public static void ensureEndpointValid(String endpoint) {
        if (!validateEndpoint(endpoint)) {
            throw new IllegalArgumentException(
                    SWIFT_RESOURCE_MANAGER.getFormattedString("EndpointInvalid", endpoint));
        }
    }

    /**
     * Validate bucket name.
     */
    public static boolean validateBucketName(String bucketName) {

        if (bucketName == null) {
            return false;
        }

        return bucketName.matches(BUCKET_NAMING_REGEX);
    }

    public static void ensureBucketNameValid(String bucketName) {
        if (!validateBucketName(bucketName)) {
            throw new IllegalArgumentException(
                    SWIFT_RESOURCE_MANAGER.getFormattedString("BucketNameInvalid", bucketName));
        }
    }

    /**
     * Validate bucket creation name.
     */
    public static boolean validateBucketNameCreation(String bucketName) {
        if (bucketName == null || bucketName.length() > 255 || bucketName.isEmpty() || bucketName.contains("/")) {
            return false;
        }
        return true;
    }

    public static void ensureBucketNameCreationValid(String bucketName) {
        if (!validateBucketNameCreation(bucketName)) {
            throw new IllegalArgumentException(
                    SWIFT_RESOURCE_MANAGER.getFormattedString("BucketNameInvalid", bucketName));
        }
    }

    /**
     * Validate object name.
     */
    public static boolean validateObjectKey(String key) {

        if (key == null || key.length() == 0) {
            return false;
        }

        byte[] bytes = null;
        try {
            bytes = key.getBytes(DEFAULT_CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            return false;
        }

        // Validate exculde xml unsupported chars
        char keyChars[] = key.toCharArray();
        char firstChar = keyChars[0];
        if (firstChar == '\\') {
            return false;
        }

        return (bytes.length > 0 && bytes.length < OBJECT_NAME_MAX_LENGTH);
    }

    public static void ensureObjectKeyValid(String key) {
        if (!validateObjectKey(key)) {
            throw new IllegalArgumentException(SWIFT_RESOURCE_MANAGER.getFormattedString("ObjectKeyInvalid", key));
        }
    }


    public static String determineResourcePath(String bucket, String key, boolean sldEnabled) {
        return sldEnabled ? makeResourcePath(bucket, key) : makeResourcePath(key);
    }

    public static String determineResourcePath(String account, String bucket, String key, boolean sldEnabled) {
        return sldEnabled ? makeResourcePath(account, bucket, key) : makeResourcePath(key);
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
     * Populate metadata to headers.
     */
    public static void populateRequestMetadata(Map<String, String> headers, ObjectMetadata metadata) {
        Map<String, Object> rawMetadata = metadata.getRawMetadata();
        if (rawMetadata != null) {
            for (Entry<String, Object> entry : rawMetadata.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    String key = entry.getKey();
                    String value = entry.getValue().toString();
                    if (key != null) {
                        key = key.trim();
                    }
                    if (value != null) {
                        value = value.trim();
                    }
                    headers.put(key, value);
                }
            }
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

    public static long determineInputStreamLength(InputStream instream, long hintLength) {
        if (hintLength <= 0 || !instream.markSupported()) {
            return 0;
        }
        return hintLength;
    }

    public static long determineInputStreamLength(InputStream instream, long hintLength, boolean useChunkEncoding) {

        if (useChunkEncoding) {
            return -1;
        }

        if (hintLength <= 0 || !instream.markSupported()) {
            return -1;
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
        for (Entry<String, String> entry : callback.getCallbackVar().entrySet()) {
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

    /**
     * Make a third-level domain by appending bucket name to front of original
     * endpoint if no binding to CNAME, otherwise use original endpoint as
     * second-level domain directly.
     */
    public static URI determineFinalEndpoint(URI endpoint, String bucket, ClientConfiguration clientConfig) {
        try {
            StringBuilder conbinedEndpoint = new StringBuilder();
            conbinedEndpoint.append(String.format("%s://", endpoint.getScheme()));
            conbinedEndpoint.append(buildCanonicalHost(endpoint, bucket, clientConfig));
            conbinedEndpoint.append(endpoint.getPort() != -1 ? String.format(":%s", endpoint.getPort()) : "");
            conbinedEndpoint.append(endpoint.getPath());
            return new URI(conbinedEndpoint.toString());
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    private static String buildCanonicalHost(URI endpoint, String bucket, ClientConfiguration clientConfig) {
        String host = endpoint.getHost();

        boolean isCname = false;
        if (clientConfig.isSupportCname()) {
            isCname = cnameExcludeFilter(host, clientConfig.getCnameExcludeList());
        }

        StringBuffer cannonicalHost = new StringBuffer();
//        if (bucket != null && !isCname && !clientConfig.isSLDEnabled()) {
//            cannonicalHost.append(bucket).append(".").append(host);
//        } else {
        cannonicalHost.append(host);
//        }

        return cannonicalHost.toString();
    }

    private static boolean cnameExcludeFilter(String hostToFilter, List<String> excludeList) {
        if (hostToFilter != null && !hostToFilter.trim().isEmpty()) {
            String canonicalHost = hostToFilter.toLowerCase();
            for (String excl : excludeList) {
                if (canonicalHost.endsWith(excl)) {
                    return false;
                }
            }
            return true;
        }
        throw new IllegalArgumentException("Host name can not be null.");
    }
}
