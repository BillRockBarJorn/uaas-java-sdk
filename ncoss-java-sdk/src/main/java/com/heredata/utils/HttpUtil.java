package com.heredata.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import static com.heredata.comm.HttpConstants.DEFAULT_CHARSET_NAME;
import static com.heredata.utils.ResourceUtils.COMMON_RESOURCE_MANAGER;
import static com.heredata.utils.StringUtils.isNullOrEmpty;

/**
 * <p>Title: HttpUtil</p>
 * <p>Description: http工具类 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 15:36
 */
public class HttpUtil {


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
            throw new IllegalArgumentException(COMMON_RESOURCE_MANAGER.getString("FailedToEncodeUri"), e);
        }
    }

    public static String urlDecode(String value, String encoding) {
        if (isNullOrEmpty(value)) {
            return value;
        }

        try {
            return URLDecoder.decode(value, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(COMMON_RESOURCE_MANAGER.getString("FailedToDecodeUrl"), e);
        }
    }

    public static String urlEncode(String value, boolean ignoreSlashes) {
        if (value == null) {
            return "";
        }
        try {
            String encoded = URLEncoder.encode(value, DEFAULT_CHARSET_NAME);
            if (!ignoreSlashes) {
                return StringUtils.replaceEach(encoded, ENCODED_CHARACTERS_WITHOUT_SLASHES, ENCODED_CHARACTERS_WITHOUT_SLASHES_REPLACEMENTS);
            }
            return StringUtils.replaceEach(encoded, ENCODED_CHARACTERS_WITH_SLASHES, ENCODED_CHARACTERS_WITH_SLASHES_REPLACEMENTS);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(COMMON_RESOURCE_MANAGER.getString("FailedToEncodeUri"), e);
        }
    }

    /**
     * Encode request parameters to URL segment.
     */
    public static String paramToQueryString(Map<String, String> params, String charset) {

        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuilder paramString = new StringBuilder();
        boolean first = true;
        for (Entry<String, String> p : params.entrySet()) {
            String key = p.getKey();
            String value = p.getValue();

            if (!first) {
                paramString.append("&");
            }

            // Urlencode each request parameter
            paramString.append(urlEncode(key, charset));
            if (value != null) {
//                paramString.append("=").append(urlEncode(value, charset));
                paramString.append("=").append(value);
            }

            first = false;
        }

        return paramString.toString();
    }


    private static final String[] ENCODED_CHARACTERS_WITH_SLASHES = new String[]{"+", "*", "%7E", "%2F"};
    private static final String[] ENCODED_CHARACTERS_WITH_SLASHES_REPLACEMENTS = new String[]{"%20", "%2A", "~", "/"};

    private static final String[] ENCODED_CHARACTERS_WITHOUT_SLASHES = new String[]{"+", "*", "%7E"};
    private static final String[] ENCODED_CHARACTERS_WITHOUT_SLASHES_REPLACEMENTS = new String[]{"%20", "%2A", "~"};

    private static final String ISO_8859_1_CHARSET = "iso-8859-1";
    private static final String UTF8_CHARSET = "utf-8";

    // To fix the bug that the header value could not be unicode chars.
    // Because HTTP headers are encoded in iso-8859-1,
    // we need to convert the utf-8(java encoding) strings to iso-8859-1 ones.
    public static void convertHeaderCharsetFromIso88591(Map<String, String> headers) {
        convertHeaderCharset(headers, ISO_8859_1_CHARSET, UTF8_CHARSET);
    }

    // For response, convert from iso-8859-1 to utf-8.
    public static void convertHeaderCharsetToIso88591(Map<String, String> headers) {
        convertHeaderCharset(headers, UTF8_CHARSET, ISO_8859_1_CHARSET);
    }

    private static void convertHeaderCharset(Map<String, String> headers, String fromCharset, String toCharset) {

        for (Entry<String, String> header : headers.entrySet()) {
            if (header.getValue() == null) {
                continue;
            }

            try {
                header.setValue(new String(header.getValue().getBytes(fromCharset), toCharset));
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException("Invalid charset name: " + e.getMessage(), e);
            }
        }
    }
}
