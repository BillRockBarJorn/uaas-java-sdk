package com.heredata.comm;

/**
 * <p>Title: HttpConstants</p>
 * <p>Description: http请求常量信息 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 13:50
 */
public final class HttpConstants extends Constants {

    public static final String DEFAULT_CHARSET_NAME = "utf-8";
    public static final String DEFAULT_XML_ENCODING = "utf-8";

    public static final String DEFAULT_OBJECT_CONTENT_TYPE = "application/octet-stream";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_XML = "application/XML";


    public static final int KB = 1024;
    public static final int DEFAULT_BUFFER_SIZE = 8 * KB;
    public static final int DEFAULT_STREAM_BUFFER_SIZE = 512 * KB;

    public static final long DEFAULT_FILE_SIZE_LIMIT = 5 * 1024 * 1024 * 1024L;

    public static final String RESOURCE_NAME_COMMON = "common";

    public static final int OBJECT_NAME_MAX_LENGTH = 1024;

    public static final String PROTOCOL_HTTP = "http://";
    public static final String PROTOCOL_HTTPS = "https://";
    public static final String PROTOCOL_RTMP = "rtmp://";

    /** Represents a null HOS version ID */
    public static final String NULL_VERSION_ID = "null";


    /** URL encoding for HOS object keys */
    public static final String URL_ENCODING = "url";

    /**
     * 日志包的名称
     */
    public static final String LOGGER_PACKAGE_NAME = "com.heredata.sdk";
}
