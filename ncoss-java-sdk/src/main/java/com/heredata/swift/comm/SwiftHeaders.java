package com.heredata.swift.comm;

import com.heredata.HttpHeaders;

/**
 * <p>Title: HOSHeaders</p>
 * <p>Description: SWIFT服务器特有请求头常量或前缀 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 14:17
 */
public interface SwiftHeaders extends HttpHeaders {

    String SWIFT_PREFIX = "x-swift-";
    String SWIFT_USER_METADATA_PREFIX = "X-Object-Meta-";

    String HEAD_OBJECT_IF_MODIFIED_SINCE = "If-Modified-Since";
    String HEAD_OBJECT_IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
    String HEAD_OBJECT_IF_MATCH = "If-Match";
    String HEAD_OBJECT_IF_NONE_MATCH = "If-None-Match";

    String X_COPY_FROM = "X-Copy-From";

    String X_OPENSTACK_REQUEST_ID = "X-Openstack-Request-Id";

    String SWIFT_HASH_CRC64_ECMA = "x-swift-hash-crc64ecma";
    String SWIFT_OBJECT_TYPE = "x-swift-object-type";

    String SWIFT_OBJECT_ACL = "x-swift-object-acl";

    String SWIFT_HEADER_CALLBACK = "x-swift-callback";
    String SWIFT_HEADER_CALLBACK_VAR = "x-swift-callback-var";

    String SWIFT_STORAGE_CLASS = "x-swift-storage-class";
    String SWIFT_RESTORE = "x-swift-restore";
    String SWIFT_ONGOING_RESTORE = "ongoing-request=\"true\"";

    String SWIFT_BUCKET_REGION = "x-swift-bucket-region";

    String SWIFT_SELECT_PREFIX = "x-swift-select";
}
