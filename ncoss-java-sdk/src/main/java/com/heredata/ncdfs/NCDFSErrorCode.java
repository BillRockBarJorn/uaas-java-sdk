package com.heredata.ncdfs;

/**
 * NCDFS Server side error code.
 */
public interface NCDFSErrorCode {

    /**
     * Access Denied (401)
     */
    String BAD_REQUEST = "BadRequest";

    /**
     * Access Denied (401)
     */
    String ACCESS_DENIED = "AccessDenied";

    /**
     * Access Forbidden (403)
     */
    String ACCESS_FORBIDDEN = "AccessForbidden";

    /**
     * Invalid argument.
     */
    String INVALID_ARGUMENT = "InvalidArgument";

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
     * Malformed xml.
     */
    String MALFORMED_XML = "MalformedXML";

    /**
     * Invalid encryption algorithm error.
     */
    String INVALID_ENCRYPTION_ALGORITHM_ERROR = "InvalidEncryptionAlgorithmError";

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
