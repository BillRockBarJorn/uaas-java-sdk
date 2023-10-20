package com.heredata.s3.comm;

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
public interface S3Headers extends HttpHeaders {

    static final String HOS_PREFIX = "x-amz-";
    static final String HOS_USER_METADATA_PREFIX = "x-amz-meta-";

    public static final String X_HOS_DATE = "x-amz-date";
    static final String HOS_CANNED_ACL = "x-amz-acl";
    static final String STORAGE_CLASS = "x-amz-storage-class";
    static final String HOS_VERSION_ID = "x-amz-version-id";

    static final String HOS_SERVER_SIDE_ENCRYPTION = "x-amz-server-side-encryption";
    static final String HOS_SERVER_SIDE_ENCRYPTION_KEY_ID = "x-amz-server-side-encryption-here-kms-key-id";
    static final String HOS_CLIENT_SIDE_ENCRYPTION_ALGORITHM = "x-amz-server-side-encryption-customer-algorithm";
    static final String HOS_CLIENT_SIDE_ENCRYPTION_KEY = "x-amz-server-side-encryption-customer-key";
    static final String HOS_CLIENT_SIDE_ENCRYPTION_KEY_MD5 = "x-amz-server-side-encryption-customer-key-MD5";
    static final String HOS_COPY_CLIENT_SIDE_ENCRYPTION__ALGORITHM = "x-amz-copy-source-server-side-encryption-customer-algorithm";
    static final String HOS_COPY_CLIENT_SIDE_ENCRYPTION__KEY = "x-amz-copy-source-server-side-encryption-customer-key";
    static final String HOS_COPY_CLIENT_SIDE_ENCRYPTION__KEY_MD5 = "x-amz-copy-source-server-side-encryption-customer-key-MD5";

    static final String GET_OBJECT_IF_MODIFIED_SINCE = "If-Modified-Since";
    static final String GET_OBJECT_IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
    static final String GET_OBJECT_IF_MATCH = "If-Match";
    static final String GET_OBJECT_IF_NONE_MATCH = "If-None-Match";

    static final String HEAD_OBJECT_IF_MODIFIED_SINCE = "If-Modified-Since";
    static final String HEAD_OBJECT_IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
    static final String HEAD_OBJECT_IF_MATCH = "If-Match";
    static final String HEAD_OBJECT_IF_NONE_MATCH = "If-None-Match";

    static final String COPY_OBJECT_SOURCE = "x-amz-copy-source";
    static final String COPY_SOURCE_RANGE = "x-amz-copy-source-range";
    static final String COPY_OBJECT_METADATA_DIRECTIVE = "x-amz-metadata-directive";
    static final String COPY_OBJECT_TAGGING_DIRECTIVE = "x-amz-tagging-directive";

    static final String HOS_HEADER_REQUEST_ID = "x-amz-request-id";
    static final String HOS_HEADER_VERSION_ID = "x-amz-version-id";

    static final String ORIGIN = "origin";
    static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
    static final String ACCESS_CONTROL_REQUEST_HEADER = "Access-Control-Request-Headers";

    static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";

    static final String HOS_SECURITY_TOKEN = "x-amz-security-token";

    static final String HOS_NEXT_APPEND_POSITION = "x-amz-next-append-position";
    static final String HOS_HASH_CRC64_ECMA = "x-amz-hash-crc64ecma";
    static final String HOS_OBJECT_TYPE = "x-amz-object-type";

    static final String HOS_OBJECT_ACL = "x-amz-object-acl";

    static final String HOS_HEADER_CALLBACK = "x-amz-callback";
    static final String HOS_HEADER_CALLBACK_VAR = "x-amz-callback-var";

    static final String HOS_STORAGE_CLASS = "x-amz-storage-class";
    static final String HOS_RESTORE = "x-amz-restore";
    static final String HOS_ONGOING_RESTORE = "ongoing-request=\"true\"";

    static final String HOS_BUCKET_REGION = "x-amz-bucket-region";

    static final String HOS_SELECT_PREFIX = "x-amz-select";
    static final String HOS_SELECT_CSV_ROWS = HOS_SELECT_PREFIX + "-csv-rows";
    static final String HOS_SELECT_OUTPUT_RAW = HOS_SELECT_PREFIX + "-output-raw";
    static final String HOS_SELECT_CSV_SPLITS = HOS_SELECT_PREFIX + "-csv-splits";
    static final String HOS_SELECT_INPUT_LINE_RANGE = HOS_SELECT_PREFIX + "-line-range";
    static final String HOS_SELECT_INPUT_SPLIT_RANGE = HOS_SELECT_PREFIX + "-split-range";

    static final String HOS_TAGGING = "x-amz-tagging";


    static final String HOS_HEADER_CERT_ID = "x-amz-yundun-certificate-id";

    static final String HOS_HNS_STATUS = "x-amz-hns-status";

    static final String HOS_DELETE_RECURSIVE = "x-amz-delete-recursive";
    static final String HOS_DELETE_TOKEN = "x-amz-delete-token";

    static final String HOS_RENAME_SOURCE = "x-amz-rename-source";

    static final String HOS_RESOURCE_GROUP_ID = "x-amz-resource-group-id";

    static final String HOS_DATE = "Date";

    static final String HOS_CONTENT_SHA256 = "x-amz-content-sha256";
}
