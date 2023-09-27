package com.heredata.hos.utils;

import com.heredata.ClientConfiguration;
import com.heredata.ResponseMessage;
import com.heredata.exception.InconsistentException;
import com.heredata.hos.comm.HOSConstants;
import com.heredata.hos.comm.HOSHeaders;
import com.heredata.hos.model.ObjectMetadata;
import com.heredata.model.Callback;
import com.heredata.model.Callback.CalbackBodyType;
import com.heredata.utils.BinaryUtil;
import com.heredata.utils.CodingUtils;
import com.heredata.utils.DateUtil;
import com.heredata.utils.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.heredata.comm.HttpConstants.DEFAULT_CHARSET_NAME;
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
public class HOSUtils {


    public static final ResourceManager HOS_RESOURCE_MANAGER = ResourceManager.getInstance(HOSConstants.RESOURCE_NAME_HOS);

    private static final String BUCKET_NAMING_CREATION_REGEX = "^[a-zA-Z0-9._-][a-zA-Z0-9-._-]{1,61}[a-zA-Z0-9._-]$";
    private static final String BUCKET_NAMING_REGEX = "^[a-zA-Z0-9._-][a-zA-Z0-9-._-]{1,61}[a-zA-Z0-9._-]$";
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
                    HOS_RESOURCE_MANAGER.getFormattedString("EndpointInvalid", endpoint));
        }
    }

    /**
     * Validate bucket name.
     */
    public static boolean validateBucketName(String bucket) {

        if (bucket == null) {
            return false;
        }


        // bucket length should be less than and no more than 63
        // characters long.
        if (bucket.length() < 3 || bucket.length() > 255) {
            return false;
        }
        // bucket with successive periods is invalid.
        if (bucket.indexOf("..") > -1 || bucket.indexOf(".-")>-1 || bucket.indexOf("-.")>-1) {
            return false;
        }
        // bucket cannot have ip address style.
        if (bucket.matches("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+")) {
            return false;
        }

        // bucket should begin with alphabet/number and end with alphabet/number,
        // with alphabet/number/.- in the middle.
        if (bucket.matches("^[a-z0-9][a-z0-9.-]+[a-z0-9]$")) {
            return true;
        }
        return false;
    }

    public static void ensureBucketNameValid(String bucketName) {
        if (!validateBucketName(bucketName)) {
            throw new IllegalArgumentException(
                    HOS_RESOURCE_MANAGER.getFormattedString("BucketNameInvalid", bucketName));
        }
    }

    /**
     * Validate bucket creation name.
     */
    public static boolean validateBucketNameCreation(String bucketName) {

        if (bucketName == null) {
            return false;
        }

        return validateBucketName(bucketName);
    }

    public static void ensureBucketNameCreationValid(String bucketName) {
        if (!validateBucketNameCreation(bucketName)) {
            throw new IllegalArgumentException(
                    HOS_RESOURCE_MANAGER.getFormattedString("BucketNameInvalid", bucketName));
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

        return (bytes.length > 0 && bytes.length < HOSConstants.OBJECT_NAME_MAX_LENGTH);
    }

    public static void ensureObjectKeyValid(String key) {
        if (!validateObjectKey(key)) {
            throw new IllegalArgumentException(HOS_RESOURCE_MANAGER.getFormattedString("ObjectKeyInvalid", key));
        }
    }

    public static void ensureLiveChannelNameValid(String liveChannelName) {
        if (!validateObjectKey(liveChannelName)) {
            throw new IllegalArgumentException(
                    HOS_RESOURCE_MANAGER.getFormattedString("LiveChannelNameInvalid", liveChannelName));
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

    public static String determineResourcePath(String bucket, String key, boolean sldEnabled) {
        return sldEnabled ? makeResourcePath(bucket, key) : makeResourcePath(bucket, key);
    }

    public static String determineResourcePath(String account, String bucket, String key, boolean sldEnabled) {
        return sldEnabled ? makeResourcePath(account, bucket, key) : makeResourcePath(account, bucket, key);
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
            return bucket + (key != null ? "/" + urlEncodeKey(key) : "");
        } else {
            return null;
        }
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

    public static void mandatoryCloseResponse(ResponseMessage response) {
        try {
            response.abort();
        } catch (IOException e) {
        }
    }

    public static long determineInputStreamLength(InputStream instream, long hintLength) {

//        if (hintLength <= 0 || !instream.markSupported()) {
//            return -1;
//        }

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

    public static String joinETags(List<String> eTags) {

        StringBuilder sb = new StringBuilder();
        boolean first = true;

        for (String eTag : eTags) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(eTag);

            first = false;
        }

        return sb.toString();
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
        if (callback.getCalbackBodyType() == CalbackBodyType.JSON) {
            jsonBody.append(",\"callbackBodyType\":\"application/json\"");
        } else if (callback.getCalbackBodyType() == CalbackBodyType.URL) {
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
                if (!"{".equals(jsonBody.toString())) {
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
    public static void populateRequestCallback(Map<String, String> headers, Callback callback) {
        if (callback != null) {
            String jsonCb = jsonizeCallback(callback);
            String base64Cb = BinaryUtil.toBase64String(jsonCb.getBytes());

            headers.put(HOSHeaders.HOS_HEADER_CALLBACK, base64Cb);

            if (callback.hasCallbackVar()) {
                String jsonCbVar = jsonizeCallbackVar(callback);
                String base64CbVar = BinaryUtil.toBase64String(jsonCbVar.getBytes());
                base64CbVar = base64CbVar.replaceAll("\n", "").replaceAll("\r", "");
                headers.put(HOSHeaders.HOS_HEADER_CALLBACK_VAR, base64CbVar);
            }
        }
    }

    /**
     * Checks if OSS and SDK's checksum is same. If not, throws
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
     * Populate metadata to headers.
     */
    public static void populateRequestMetadata(Map<String, String> headers, ObjectMetadata metadata) {
        Map<String, Object> rawMetadata = metadata.getRawMetadata();
        if (rawMetadata != null) {
            for (Map.Entry<String, Object> entry : rawMetadata.entrySet()) {
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

        Map<String, String> userMetadata = metadata.getUserMetadata();
        if (userMetadata != null) {
            for (Map.Entry<String, String> entry : userMetadata.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (key != null) {
                        key = key.trim();
                    }
                    if (value != null) {
                        value = value.trim();
                    }
                    headers.put(HOSHeaders.HOS_USER_METADATA_PREFIX + key, value);
                }
            }
        }
    }

}
