package com.heredata.hos.comm;

/**
 * <p>Title: RequestParameters</p>
 * <p>Description: TODO </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 14:39
 */
public final class HOSRequestParameters {

    /**
     * 定义需要计算在签名字符串中的信息
     */
    public static final String SUBRESOURCE_ACL = "acl";
    public static final String SUBRESOURCE_LIFECYCLE = "lifecycle";
    public static final String SUBRESOURCE_UPLOADS = "uploads";
    public static final String SUBRESOURCE_DELETE = "delete";
    public static final String SUBRESOURCE_CORS = "cors";
    public static final String SUBRESOURCE_APPEND = "append";
    public static final String SUBRESOURCE_TAGGING = "tagging";
    public static final String SUBRESOURCE_BUCKET_INFO = "bucketInfo";
    public static final String SUBRESOURCE_OBJECTMETA = "objectMeta";
    public static final String SUBRESOURCE_QOS = "qos";
    public static final String SUBRESOURCE_CSV_SELECT = "csv/select";
    public static final String SUBRESOURCE_CSV_META = "csv/meta";
    public static final String SUBRESOURCE_JSON_SELECT = "json/select";
    public static final String SUBRESOURCE_JSON_META = "json/meta";
    public static final String SUBRESOURCE_RESTORE = "restore";
    public static final String SUBRESOURCE_ENCRYPTION = "encryption";
    public static final String SUBRESOURCE_VRESIONS = "versions";
    public static final String SUBRESOURCE_VRESIONING = "versioning";
    public static final String SUBRESOURCE_VRESION_ID = "versionId";
    public static final String SUBRESOURCE_POLICY = "policy";
    public static final String SUBRESOURCE_QUOTA = "quota";
    public static final String SUBRESOURCE_CALLBACK = "callback";
    public static final String SUBRESOURCE_CALLBACK_VAR = "callback-var";
    public static final String SUBRESOURCE_RENAME = "x-hos-rename";

    public static final String SUBRESOURCE_UDF = "udf";
    public static final String SUBRESOURCE_UDF_NAME = "udfName";
    public static final String SUBRESOURCE_UDF_IMAGE = "udfImage";
    public static final String SUBRESOURCE_UDF_IMAGE_DESC = "udfImageDesc";
    public static final String SUBRESOURCE_UDF_APPLICATION = "udfApplication";
    public static final String SUBRESOURCE_UDF_LOG = "udfApplicationLog";


    /**
     * 自定义body中xml标签值
     */
    public static final String PREFIX = "prefix";
    public static final String START_AFTER = "start-after";
    public static final String MARKER = "marker";
    public static final String LIMIT = "limit";
    public static final String MAX_KEYS = "max-keys";
    public static final String VERSION_ID_MARKER = "version-id-marker";
    public static final String MAX_UPLOADS = "max-uploads";
    public static final String UPLOAD_ID_MARKER = "upload-id-marker";
    public static final String KEY_MARKER = "key-marker";
    public static final String MAX_PARTS = "max-parts";
    public static final String PART_NUMBER_MARKER = "part-number-marker";
    public static final String SEQUENTIAL = "sequential";
    public static final String TAG_KEY = "tag-key";
    public static final String TAG_VALUE = "tag-value";
    public static final String LIST_TYPE = "list-type";

    /**
     * 自定义子资源路径后缀
     */
    public static final String ACL = "acl";
    public static final String POLICY = "policy";
    public static final String QUOTA = "quota";
    public static final String TAGGING = "tagging";
    public static final String VERSIONING = "versioning";
    public static final String VERSION_ID = "versionId";
    public static final String VERSIONS = "versions";
    public static final String LIFECYCLE = "lifecycle";
    public static final String UPLOADS = "uploads";
    public static final String RESTORE = "restore";
    public static final String UPLOAD_ID = "uploadId";
    public static final String PART_NUMBER = "partNumber";


    public static final String SECURITY_TOKEN = "security-token";
    public static final String X_HOS_AC_SOURCE_IP = "x-hos-ac-source-ip";
    public static final String X_HOS_AC_SUBNET_MASK = "x-hos-ac-subnet-mask";
    public static final String X_HOS_AC_VPC_ID = "x-hos-ac-vpc-id";
    public static final String X_HOS_AC_FORWARD_ALLOW = "x-hos-ac-forward-allow";

    public static final String POSITION = "position";

    public static final String COMP_CREATE = "create";
    public static final String COMP_UPGRADE = "upgrade";
    public static final String COMP_RESIZE = "resize";

    public static final String STAT = "stat";
    public static final String SINCE = "since";
    public static final String TAIL = "tail";
}
