package com.heredata.hos.comm;

import com.heredata.HttpHeaders;

/**
 * <p>Title: HOSHeaders</p>
 * <p>Description: hos服务器特有请求头常量或前缀 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 14:17
 */
public interface HOSHeaders extends HttpHeaders {

    static final String HOS_PREFIX = "x-hos-";
    static final String HOS_USER_METADATA_PREFIX = "x-hos-meta-";

    public static final String X_HOS_DATE = "x-hos-date";
    static final String HOS_CANNED_ACL = "x-hos-acl";
    static final String STORAGE_CLASS = "x-hos-storage-class";
    static final String HOS_VERSION_ID = "x-hos-version-id";

    static final String HOS_SERVER_SIDE_ENCRYPTION = "x-hos-server-side-encryption";
    static final String HOS_SERVER_SIDE_ENCRYPTION_KEY_ID = "x-hos-server-side-encryption-here-kms-key-id";
    static final String HOS_CLIENT_SIDE_ENCRYPTION_ALGORITHM = "x-hos-server-side-encryption-customer-algorithm";
    static final String HOS_CLIENT_SIDE_ENCRYPTION_KEY = "x-hos-server-side-encryption-customer-key";
    static final String HOS_CLIENT_SIDE_ENCRYPTION_KEY_MD5 = "x-hos-server-side-encryption-customer-key-MD5";
    static final String HOS_COPY_CLIENT_SIDE_ENCRYPTION__ALGORITHM = "x-hos-copy-source-server-side-encryption-customer-algorithm";
    static final String HOS_COPY_CLIENT_SIDE_ENCRYPTION__KEY = "x-hos-copy-source-server-side-encryption-customer-key";
    static final String HOS_COPY_CLIENT_SIDE_ENCRYPTION__KEY_MD5 = "x-hos-copy-source-server-side-encryption-customer-key-MD5";

    static final String GET_OBJECT_IF_MODIFIED_SINCE = "If-Modified-Since";
    static final String GET_OBJECT_IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
    static final String GET_OBJECT_IF_MATCH = "If-Match";
    static final String GET_OBJECT_IF_NONE_MATCH = "If-None-Match";

    static final String HEAD_OBJECT_IF_MODIFIED_SINCE = "If-Modified-Since";
    static final String HEAD_OBJECT_IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
    static final String HEAD_OBJECT_IF_MATCH = "If-Match";
    static final String HEAD_OBJECT_IF_NONE_MATCH = "If-None-Match";

    static final String COPY_OBJECT_SOURCE = "x-hos-copy-source";
    static final String COPY_SOURCE_RANGE = "x-hos-copy-source-range";
    static final String COPY_OBJECT_METADATA_DIRECTIVE = "x-hos-metadata-directive";
    static final String COPY_OBJECT_TAGGING_DIRECTIVE = "x-hos-tagging-directive";

    static final String HOS_HEADER_REQUEST_ID = "x-hos-request-id";
    static final String HOS_HEADER_VERSION_ID = "x-hos-version-id";

    static final String ORIGIN = "origin";
    static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
    static final String ACCESS_CONTROL_REQUEST_HEADER = "Access-Control-Request-Headers";

    static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";

    static final String HOS_SECURITY_TOKEN = "x-hos-security-token";

    static final String HOS_NEXT_APPEND_POSITION = "x-hos-next-append-position";
    static final String HOS_HASH_CRC64_ECMA = "x-hos-hash-crc64ecma";
    static final String HOS_OBJECT_TYPE = "x-hos-object-type";

    static final String HOS_OBJECT_ACL = "x-hos-object-acl";

    static final String HOS_HEADER_CALLBACK = "x-hos-callback";
    static final String HOS_HEADER_CALLBACK_VAR = "x-hos-callback-var";

    static final String HOS_STORAGE_CLASS = "x-hos-storage-class";
    static final String HOS_RESTORE = "x-hos-restore";
    static final String HOS_ONGOING_RESTORE = "ongoing-request=\"true\"";

    static final String HOS_BUCKET_REGION = "x-hos-bucket-region";

    static final String HOS_SELECT_PREFIX = "x-hos-select";
    static final String HOS_SELECT_CSV_ROWS = HOS_SELECT_PREFIX + "-csv-rows";
    static final String HOS_SELECT_OUTPUT_RAW = HOS_SELECT_PREFIX + "-output-raw";
    static final String HOS_SELECT_CSV_SPLITS = HOS_SELECT_PREFIX + "-csv-splits";
    static final String HOS_SELECT_INPUT_LINE_RANGE = HOS_SELECT_PREFIX + "-line-range";
    static final String HOS_SELECT_INPUT_SPLIT_RANGE = HOS_SELECT_PREFIX + "-split-range";

    static final String HOS_TAGGING = "x-hos-tagging";


    static final String HOS_HEADER_CERT_ID = "x-hos-yundun-certificate-id";

    static final String HOS_HNS_STATUS = "x-hos-hns-status";

    static final String HOS_DELETE_RECURSIVE = "x-hos-delete-recursive";
    static final String HOS_DELETE_TOKEN = "x-hos-delete-token";

    static final String HOS_RENAME_SOURCE = "x-hos-rename-source";

    static final String HOS_RESOURCE_GROUP_ID = "x-hos-resource-group-id";

    static final String HOS_DATE = "Date";

    static final String HOS_CONTENT_SHA256 = "x-hos-content-sha256";
}
