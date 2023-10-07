package com.heredata.hos;

/**
 * <p>Title: HOSErrorCode</p>
 * <p>Description: HOS服务器错误码 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 13:45
 */
public interface HOSErrorCode {

    /**
     * Access Denied (401)
     */
    String ACCESS_DENIED = "AccessDenied";

    /**
     * Access Forbidden (403)
     */
    String ACCESS_FORBIDDEN = "AccessForbidden";

    /**
     * Bucket pre-exists
     */
    String BUCKET_ALREADY_EXISTS = "BucketAlreadyExists";

    /**
     * Bucket not empty.
     */
    String BUCKET_NOT_EMPTY = "BucketNotEmpty";

    /**
     * File groups is too large.
     */
    String FILE_GROUP_TOO_LARGE = "FileGroupTooLarge";

    /**
     * File part is stale.
     */
    String FILE_PART_STALE = "FilePartStale";

    /**
     * Invalid argument.
     */
    String INVALID_ARGUMENT = "InvalidArgument";

    /**
     * Non-existing Access ID
     */
    String INVALID_ACCESS_KEY_ID = "InvalidAccessKeyId";

    /**
     * Invalid bucket name
     */
    String INVALID_BUCKET_NAME = "InvalidBucketName";

    /**
     * Invalid object name
     */
    String INVALID_OBJECT_NAME = "InvalidObjectName";

    /**
     * Invalid part
     */
    String INVALID_PART = "InvalidPart";

    /**
     * Invalid part order
     */
    String INVALID_PART_ORDER = "InvalidPartOrder";

    /**
     * The target bucket does not exist when setting logging.
     */
    String INVALID_TARGET_BUCKET_FOR_LOGGING = "InvalidTargetBucketForLogging";

    /**
     * HOS Internal error.
     */
    String INTERNAL_ERROR = "InternalError";

    /**
     * Missing content length.
     */
    String MISSING_CONTENT_LENGTH = "MissingContentLength";

    /**
     * Missing required argument.
     */
    String MISSING_ARGUMENT = "MissingArgument";

    /**
     * No bucket meets the requirement specified.
     */
    String NO_SUCH_BUCKET = "NoSuchBucket";

    /**
     * File does not exist.
     */
    String NO_SUCH_KEY = "NoSuchKey" + " or " + NO_SUCH_BUCKET;

    /**
     * Version does not exist.
     */
    String NO_SUCH_VERSION = "NoSuchVersion";

    /**
     * Not implemented method.
     */
    String NOT_IMPLEMENTED = "NotImplemented";

    /**
     * Error occurred in precondition.
     */
    String PRECONDITION_FAILED = "PreconditionFailed";

    /**
     * 304 Not Modified。
     */
    String NOT_MODIFIED = "NotModified";

    /**
     * Invalid location.
     */
    String INVALID_LOCATION_CONSTRAINT = "InvalidLocationConstraint";

    /**
     * The specified location does not match with the request.
     */
    String ILLEGAL_LOCATION_CONSTRAINT_EXCEPTION = "IllegalLocationConstraintException";

    /**
     * The time skew between the time in request headers and server is more than
     * 15 min.
     */
    String REQUEST_TIME_TOO_SKEWED = "RequestTimeTooSkewed";

    /**
     * Request times out.
     */
    String REQUEST_TIMEOUT = "RequestTimeout";

    /**
     * Invalid signature.
     */
    String SIGNATURE_DOES_NOT_MATCH = "SignatureDoesNotMatch";

    /**
     * Too many buckets under a user.
     */
    String TOO_MANY_BUCKETS = "TooManyBuckets";

    /**
     * Source buckets is not configured with CORS.
     */
    String NO_SUCH_CORS_CONFIGURATION = "NoSuchCORSConfiguration";

    /**
     * The source bucket is not configured with static website (the index page
     * is null).
     */
    String NO_SUCH_WEBSITE_CONFIGURATION = "NoSuchWebsiteConfiguration";

    /**
     * The source bucket is not configured with lifecycle rule.
     */
    String NO_SUCH_LIFECYCLE = "NoSuchLifecycle";

    /**
     * Malformed xml.
     */
    String MALFORMED_XML = "MalformedXML";

    /**
     * Invalid encryption algorithm error.
     */
    String INVALID_ENCRYPTION_ALGORITHM_ERROR = "InvalidEncryptionAlgorithmError";

    /**
     * The upload Id does not exist.
     */
    String NO_SUCH_UPLOAD = "NoSuchUpload";

    /**
     * The entity is too small. (Part must be more than 100K)
     */
    String ENTITY_TOO_SMALL = "EntityTooSmall";

    /**
     * The entity is too big.
     */
    String ENTITY_TOO_LARGE = "EntityTooLarge";

    /**
     * Invalid MD5 digest.
     */
    String INVALID_DIGEST = "InvalidDigest";

    /**
     * Invalid range of the character.
     */
    String INVALID_RANGE = "InvalidRange";

    /**
     * Security token is not supported.
     */
    String SECURITY_TOKEN_NOT_SUPPORTED = "SecurityTokenNotSupported";

    /**
     * The specified object does not support append operation.
     */
    String OBJECT_NOT_APPENDALBE = "ObjectNotAppendable";

    /**
     * The position of append on the object is not same as the current length.
     */
    String POSITION_NOT_EQUAL_TO_LENGTH = "PositionNotEqualToLength";

    /**
     * Invalid response.
     */
    String INVALID_RESPONSE = "InvalidResponse";

    /**
     * Callback failed. The operation (such as download or upload) succeeded
     * though.
     */
    String CALLBACK_FAILED = "CallbackFailed";

    /**
     * The Live Channel does not exist.
     */
    String NO_SUCH_LIVE_CHANNEL = "NoSuchLiveChannel";

    /**
     * symlink target file does not exist.
     */
    String NO_SUCH_SYM_LINK_TARGET = "SymlinkTargetNotExist";

    /**
     * The archive file is not restored before usage.
     */
    String INVALID_OBJECT_STATE = "InvalidObjectState";

    /**
     * The policy text is illegal.
     */
    String INVALID_POLICY_DOCUMENT = "InvalidPolicyDocument";

    /**
     * The exsiting bucket without policy.
     */
    String NO_SUCH_BUCKET_POLICY = "NoSuchBucketPolicy";

    /**
     * The object has already exists.
     */
    String OBJECT_ALREADY_EXISTS = "ObjectAlreadyExists";

    /**
     * The exsiting bucket without inventory.
     */
    String NO_SUCH_INVENTORY = "NoSuchInventory";

    /**
     * The part is not upload sequentially
     */
    String PART_NOT_SEQUENTIAL = "PartNotSequential";

    /**
     * The file is immutable.
     */
    String FILE_IMMUTABLE = "FileImmutable";

    /**
     * The worm configuration is locked.
     */
    String WORM_CONFIGURATION_LOCKED = "WORMConfigurationLocked";

    /**
     * The worm configuration is invalid.
     */
    String INVALID_WORM_CONFIGURATION = "InvalidWORMConfiguration";

    /**
     * The file already exists.
     */
    String FILE_ALREADY_EXISTS = "FileAlreadyExists";

}
