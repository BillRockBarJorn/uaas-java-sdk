package com.heredata.hos.parser;


import com.heredata.ResponseMessage;
import com.heredata.exception.ResponseParseException;
import com.heredata.hos.comm.HOSConstants;
import com.heredata.hos.comm.HOSHeaders;
import com.heredata.hos.model.*;
import com.heredata.hos.model.DeleteVersionsResult.DeletedVersion;
import com.heredata.hos.model.bucket.Bucket;
import com.heredata.hos.model.bucket.BucketList;
import com.heredata.hos.model.bucket.BucketQuotaResult;
import com.heredata.hos.model.bucket.BucketVersioningConfiguration;
import com.heredata.model.GenericResult;
import com.heredata.model.VoidResult;
import com.heredata.parser.ResponseParser;
import com.heredata.utils.DateUtil;
import com.heredata.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.JDOMParseException;
import org.jdom2.input.SAXBuilder;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;
import java.util.zip.CheckedInputStream;

import static com.heredata.HttpHeaders.*;
import static com.heredata.hos.comm.HOSHeaders.HOS_HEADER_REQUEST_ID;
import static com.heredata.hos.comm.HOSHeaders.HOS_HEADER_VERSION_ID;
import static com.heredata.utils.HttpUtil.urlDecode;
import static com.heredata.utils.ResourceUtils.safeCloseResponse;
import static com.heredata.utils.StringUtils.*;


/*
 * A collection of parsers that parse HTTP reponses into corresponding human-readable results.
 */
public final class ResponseParsers {

    public static final ListBucketResponseParser listBucketResponseParser = new ListBucketResponseParser();
    public static final GetAclResponseParser getAclResponseParser = new GetAclResponseParser();
    public static final GetBucketLifecycleResponseParser getBucketLifecycleResponseParser = new GetBucketLifecycleResponseParser();
    public static final GetTaggingResponseParser getTaggingResponseParser = new GetTaggingResponseParser();
    public static final GetBucketInfoResponseParser getBucketInfoResponseParser = new GetBucketInfoResponseParser();
    public static final GetBucketVersioningResponseParser getBucketVersioningResponseParser = new GetBucketVersioningResponseParser();
    public static final GetBucketPolicyResponseParser getBucketPolicyResponseParser = new GetBucketPolicyResponseParser();

    public static final ListVersionsReponseParser listVersionsReponseParser = new ListVersionsReponseParser();
    public static final PutObjectReponseParser putObjectReponseParser = new PutObjectReponseParser();
    public static final PutObjectProcessReponseParser putObjectProcessReponseParser = new PutObjectProcessReponseParser();
    public static final GetObjectMetadataResponseParser getObjectMetadataResponseParser = new GetObjectMetadataResponseParser();
    public static final CopyObjectResponseParser copyObjectResponseParser = new CopyObjectResponseParser();
    public static final DeleteObjectsResponseParser deleteObjectsResponseParser = new DeleteObjectsResponseParser();
    public static final DeleteVersionsResponseParser deleteVersionsResponseParser = new DeleteVersionsResponseParser();
    public static final GetSimplifiedObjectMetaResponseParser getSimplifiedObjectMetaResponseParser = new GetSimplifiedObjectMetaResponseParser();
    public static final RestoreObjectResponseParser restoreObjectResponseParser = new RestoreObjectResponseParser();
    public static final HeadObjectResponseParser headObjectResponseParser = new HeadObjectResponseParser();

    public static final CompleteMultipartUploadResponseParser completeMultipartUploadResponseParser = new CompleteMultipartUploadResponseParser();
    public static final CompleteMultipartUploadProcessResponseParser completeMultipartUploadProcessResponseParser = new CompleteMultipartUploadProcessResponseParser();
    public static final InitiateMultipartUploadResponseParser initiateMultipartUploadResponseParser = new InitiateMultipartUploadResponseParser();
    public static final ListMultipartUploadsResponseParser listMultipartUploadsResponseParser = new ListMultipartUploadsResponseParser();
    public static final ListPartsResponseParser listPartsResponseParser = new ListPartsResponseParser();

    public static final GetAccountInfoResponseParser getAccountInfoResponseParser = new GetAccountInfoResponseParser();

    public static final GetQuotaBucketParser getQuotaBucketParser = new GetQuotaBucketParser();


    public static Long parseLongWithDefault(String defaultValue) {
        if (defaultValue == null || "".equals(defaultValue)) {
            return 0L;
        }
        return Long.parseLong(defaultValue);
    }

    public static final class EmptyResponseParser implements ResponseParser<ResponseMessage> {

        @Override
        public ResponseMessage parse(ResponseMessage response) throws ResponseParseException {
            // Close response and return it directly without parsing.
            safeCloseResponse(response);
            return response;
        }

    }

    public static final class RequestIdResponseParser implements ResponseParser<VoidResult> {
        @Override
        public VoidResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                VoidResult result = new VoidResult();
                result.setResponse(response);
                result.setRequestId(response.getHeaders().get(HOS_HEADER_REQUEST_ID));
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class ListBucketResponseParser implements ResponseParser<BucketList> {

        @Override
        public BucketList parse(ResponseMessage response) throws ResponseParseException {
            try {
                BucketList result = parseListBucket(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetAclResponseParser implements ResponseParser<AccessControlList> {

        @Override
        public AccessControlList parse(ResponseMessage response) throws ResponseParseException {
            try {
                AccessControlList result = parseGetAcl(response);
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetBucketLifecycleResponseParser implements ResponseParser<List<LifecycleRule>> {

        @Override
        public List<LifecycleRule> parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseGetBucketLifecycle(response.getContent());
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetBucketInfoResponseParser implements ResponseParser<Bucket> {

        @Override
        public Bucket parse(ResponseMessage response) throws ResponseParseException {
            try {
                Bucket result = parseGetBucketInfo(response);
                result.setResponse(response);
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetBucketVersioningResponseParser
            implements ResponseParser<BucketVersioningConfiguration> {

        @Override
        public BucketVersioningConfiguration parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseGetBucketVersioning(response.getContent());
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetBucketPolicyResponseParser implements ResponseParser<GetBucketPolicyResult> {

        @Override
        public GetBucketPolicyResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                GetBucketPolicyResult result = parseGetBucketPolicy(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetAccountInfoResponseParser implements ResponseParser<AccountInfo> {
        @Override
        public AccountInfo parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseGetAccountInfo(response);
            } finally {
                safeCloseResponse(response);
            }
        }
    }

    public static final class GetQuotaBucketParser implements ResponseParser<BucketQuotaResult> {

        @Override
        public BucketQuotaResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseGetQuotaBucket(response);
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetTaggingResponseParser implements ResponseParser<TagSet> {

        @Override
        public TagSet parse(ResponseMessage response) throws ResponseParseException {
            try {
                TagSet result = parseGetBucketTagging(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class ListObjectsReponseParser implements ResponseParser<ObjectListing> {

        private String bucketName;

        public ListObjectsReponseParser(String bucketName) {
            this.bucketName = bucketName;
        }

        @Override
        public ObjectListing parse(ResponseMessage response) throws ResponseParseException {
            try {
                ObjectListing result = parseListObjects(response.getContent());
                result.setBucketName(this.bucketName);
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class ListVersionsReponseParser implements ResponseParser<VersionListing> {

        @Override
        public VersionListing parse(ResponseMessage response) throws ResponseParseException {
            try {
                VersionListing result = parseListVersions(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class PutObjectReponseParser implements ResponseParser<PutObjectResult> {

        @Override
        public PutObjectResult parse(ResponseMessage response) throws ResponseParseException {
            PutObjectResult result = new PutObjectResult();
            try {
                result.setETag(trimQuotes(response.getHeaders().get(HOSHeaders.ETAG)));
                result.setVersionId(response.getHeaders().get(HOS_HEADER_VERSION_ID));
                result.setRequestId(response.getHeaders().get(HOS_HEADER_REQUEST_ID));
                result.setResponse(response);
                setCRC(result, response);
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class PutObjectProcessReponseParser implements ResponseParser<PutObjectResult> {

        @Override
        public PutObjectResult parse(ResponseMessage response) throws ResponseParseException {
            PutObjectResult result = new PutObjectResult();
            result.setRequestId(response.getRequestId());
            result.setETag(trimQuotes(response.getHeaders().get(HOSHeaders.ETAG)));
            result.setVersionId(response.getHeaders().get(HOS_HEADER_VERSION_ID));
            result.setResponse(response);
            return result;
        }

    }

    public static final class GetObjectResponseParser implements ResponseParser<HOSObject> {
        private String bucketName;
        private String key;

        public GetObjectResponseParser(final String bucketName, final String key) {
            this.bucketName = bucketName;
            this.key = key;
        }

        @Override
        public HOSObject parse(ResponseMessage response) throws ResponseParseException {
            HOSObject HOSObject = new HOSObject();
            HOSObject.setBucketName(this.bucketName);
            HOSObject.setKey(this.key);
            HOSObject.setObjectContent(response.getContent());
            HOSObject.setRequestId(response.getHeaders().get(HOS_HEADER_REQUEST_ID));
            HOSObject.setResponse(response);
            HOSObject.setMimeType(response.getHeaders().get(CONTENT_TYPE));
            HOSObject.setSize(response.getContentLength());
            try {
                HOSObject.setLastModified(DateUtil.parseGMTDate(response.getHeaders().get(LAST_MODIFIED)));
            } catch (ParseException e) {
                throw new ResponseParseException(e.getMessage(), e);
            }
            try {
                HOSObject.setETag(response.getHeaders().get(ETAG));
                HOSObject.setMetadata(parseObjectMetadata(response.getHeaders()));
                return HOSObject;
            } catch (ResponseParseException e) {
                // Close response only when parsing exception thrown. Otherwise,
                // just hand over to SDK users and remain them close it when no
                // longer in use.
                safeCloseResponse(response);

                // Rethrow
                throw e;
            }
        }

    }

    public static final class GetSimplifiedObjectMetaResponseParser implements ResponseParser<SimplifiedObjectMeta> {

        @Override
        public SimplifiedObjectMeta parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseSimplifiedObjectMeta(response.getHeaders());
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class RestoreObjectResponseParser implements ResponseParser<RestoreObjectResult> {

        @Override
        public RestoreObjectResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                RestoreObjectResult result = new RestoreObjectResult(response.getStatusCode());
                result.setRequestId(response.getRequestId());
                result.setResponse(response);
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class GetObjectMetadataResponseParser implements ResponseParser<ObjectMetadata> {

        @Override
        public ObjectMetadata parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseObjectMetadata(response.getHeaders());
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class HeadObjectResponseParser implements ResponseParser<ObjectMetadata> {

        @Override
        public ObjectMetadata parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseObjectMetadata(response.getHeaders());
            } finally {
                safeCloseResponse(response);
            }
        }
    }

    public static final class CopyObjectResponseParser implements ResponseParser<CopyObjectResult> {

        @Override
        public CopyObjectResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                CopyObjectResult result = parseCopyObjectResult(response.getContent());
                result.setVersionId(response.getHeaders().get(HOS_HEADER_VERSION_ID));
                result.setRequestId(response.getRequestId());
                result.setResponse(response);
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class DeleteObjectsResponseParser implements ResponseParser<DeleteObjectsResult> {

        @Override
        public DeleteObjectsResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                DeleteObjectsResult result;

                // Occurs when deleting multiple objects in quiet mode.
                if (response.getContentLength() == 0) {
                    result = new DeleteObjectsResult(null);
                } else {
                    result = parseDeleteObjectsResult(response.getContent());
                }
                result.setRequestId(response.getRequestId());

                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class DeleteVersionsResponseParser implements ResponseParser<DeleteVersionsResult> {

        @Override
        public DeleteVersionsResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                DeleteVersionsResult result;

                // Occurs when deleting multiple objects in quiet mode.
                if (response.getContentLength() == 0) {
                    result = new DeleteVersionsResult(new ArrayList<>());
                } else {
                    result = parseDeleteVersionsResult(response.getContent());
                }
                result.setRequestId(response.getRequestId());

                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class CompleteMultipartUploadResponseParser
            implements ResponseParser<CompleteMultipartUploadResult> {

        @Override
        public CompleteMultipartUploadResult parse(ResponseMessage response) throws ResponseParseException {
            try {
//                CompleteMultipartUploadResult result = parseCompleteMultipartUpload(response.getContent());
                CompleteMultipartUploadResult result = new CompleteMultipartUploadResult();
                result.setETag(response.getHeaders().get(ETAG));
                result.setVersionId(response.getHeaders().get(HOS_HEADER_VERSION_ID));
                result.setRequestId(response.getRequestId());
                result.setResponse(response);
//                setServerCRC(result, response);
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class CompleteMultipartUploadProcessResponseParser
            implements ResponseParser<CompleteMultipartUploadResult> {

        @Override
        public CompleteMultipartUploadResult parse(ResponseMessage response) throws ResponseParseException {
            CompleteMultipartUploadResult result = new CompleteMultipartUploadResult();
            result.setVersionId(response.getHeaders().get(HOS_HEADER_VERSION_ID));
            result.setRequestId(response.getRequestId());
            result.setResponse(response);
            return result;
        }

    }

    public static final class InitiateMultipartUploadResponseParser
            implements ResponseParser<InitiateMultipartUploadResult> {

        @Override
        public InitiateMultipartUploadResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                InitiateMultipartUploadResult result = parseInitiateMultipartUpload(response.getContent());
                result.setRequestId(response.getRequestId());
                result.setResponse(response);
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class ListMultipartUploadsResponseParser implements ResponseParser<MultipartUploadListing> {

        @Override
        public MultipartUploadListing parse(ResponseMessage response) throws ResponseParseException {
            try {
                MultipartUploadListing result = parseListMultipartUploads(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static final class ListPartsResponseParser implements ResponseParser<PartListing> {

        @Override
        public PartListing parse(ResponseMessage response) throws ResponseParseException {
            try {
                PartListing result = parseListParts(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    public static <ResultType extends GenericResult> void setCRC(ResultType result, ResponseMessage response) {
        InputStream inputStream = response.getRequest().getContent();
        if (inputStream instanceof CheckedInputStream) {
            CheckedInputStream checkedInputStream = (CheckedInputStream) inputStream;
//            result.setClientCRC(checkedInputStream.getChecksum().getValue());
        }

        String strSrvCrc = response.getHeaders().get(HOSHeaders.HOS_HASH_CRC64_ECMA);
        if (strSrvCrc != null) {
            BigInteger bi = new BigInteger(strSrvCrc);
//            result.setServerCRC(bi.longValue());
        }
    }

    public static <ResultType extends GenericResult> void setServerCRC(ResultType result, ResponseMessage response) {
        String strSrvCrc = response.getHeaders().get(HOSHeaders.HOS_HASH_CRC64_ECMA);
        if (strSrvCrc != null) {
            BigInteger bi = new BigInteger(strSrvCrc);
//            result.setServerCRC(bi.longValue());
        }
    }

    private static Element getXmlRootElement(InputStream responseBody) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        builder.setFeature("http://xml.org/sax/features/external-general-entities", false);
        builder.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        builder.setExpandEntities(false);
        Document doc = builder.build(responseBody);
        return doc.getRootElement();
    }

    /**
     * Unmarshall list objects response body to object listing.
     */
    @SuppressWarnings("unchecked")
    public static ObjectListing parseListObjects(InputStream responseBody) throws ResponseParseException {

        try {
            ObjectListing result = new ObjectListing();
            Element root = getXmlRootElement(responseBody);

            String prefix = root.getChild("Prefix").getValue();
            String mrker = root.getChild("StartAfter") != null ? root.getChild("StartAfter").getValue() : (root.getChild("Marker") != null ? root.getChild("Marker").getValue() : null);
            String keyCount = root.getChild("KeyCount").getValue();
            String maxKeys = root.getChild("MaxKeys").getValue();
            String isTruncated = root.getChild("IsTruncated").getValue();
            String delimiter = root.getChild("NextStartAfter") != null ? root.getChild("NextStartAfter").getValue() : (root.getChild("NextMarker") != null ? root.getChild("NextMarker").getValue() : null);
            result.setPrefix(prefix);
            result.setDelimiter(delimiter);
            result.setMarker(mrker);
            result.setKeyCounts(keyCount == null ? 0 : Integer.valueOf(keyCount));
            result.setMaxKeys(maxKeys == null ? 0 : Integer.valueOf(maxKeys));
            result.setTruncated(isTruncated == null ? false : Boolean.valueOf(isTruncated));

            List<Element> contents = root.getChildren("Contents");
            for (Element item : contents) {
                HOSObjectSummary HOSObjectSummary = new HOSObjectSummary();
                String key = getElementValue(item, "Key");
                String lastModified = getElementValue(item, "LastModified");
                String eTag = getElementValue(item, "ETag");
                String size = getElementValue(item, "Size");
                String storageClass = getElementValue(item, "StorageClass");
                Element owner = item.getChild("Owner");
                if (owner != null) {
                    String id = getElementValue(owner, "ID");
                    HOSObjectSummary.setOwner(new Owner(id));
                }
                HOSObjectSummary.setKey(urlDecode(key, DEFAULT_ENCODING));
//                HOSObjectSummary.setKey(key);
                try {
                    HOSObjectSummary.setLastModified(DateUtil.parseIso8601Date(lastModified));
                } catch (ParseException e) {
                    throw new ResponseParseException(e.getMessage(), e);
                }
                HOSObjectSummary.setETag(eTag.substring(1, eTag.length() - 1));
                HOSObjectSummary.setSize(isNullOrEmpty(size) ? null : Long.valueOf(size));
                HOSObjectSummary.setStorageClass(storageClass);
                result.getObjectSummaries().add(HOSObjectSummary);
            }
            return result;
        } catch (JDOMParseException e) {
            e.printStackTrace();
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("aaaa");
            throw new ResponseParseException(e.getMessage(), e);
        }

    }

    public static String getElementValue(Element root, String tar) {
        if (root == null || root.getChild(tar) == null) {
            return null;
        }
        return root.getChild(tar).getValue();
    }

    /**
     * Unmarshall list objects response body to object listing.
     */
    @SuppressWarnings("unchecked")
    public static VersionListing parseListVersions(InputStream responseBody) throws ResponseParseException {
        try {
            Element root = getXmlRootElement(responseBody);

            boolean shouldSDKDecode = true;
            VersionListing versionListing = new VersionListing();
            versionListing.setBucketName(root.getChildText("Name"));
            versionListing.setMaxKeys(Integer.valueOf(root.getChildText("MaxKeys")));
            versionListing.setTruncated(Boolean.valueOf(root.getChildText("IsTruncated")));

            if (root.getChild("Prefix") != null) {
                String prefix = root.getChildText("Prefix");
                versionListing.setPrefix(isNullOrEmpty(prefix) ? null : decodeIfSpecified(prefix, shouldSDKDecode));
            }

            if (root.getChild("KeyMarker") != null) {
                String marker = root.getChildText("KeyMarker");
                versionListing.setStartAfter(isNullOrEmpty(marker) ? null : decodeIfSpecified(marker, shouldSDKDecode));
            }

            if (root.getChild("VersionIdMarker") != null) {
                String marker = root.getChildText("VersionIdMarker");
                versionListing.setVersionIdMarker(isNullOrEmpty(marker) ? null : marker);
            }

            if (root.getChild("NextKeyMarker") != null) {
                String nextMarker = root.getChildText("NextKeyMarker");
                versionListing.setNextStartAfter(
                        isNullOrEmpty(nextMarker) ? null : decodeIfSpecified(nextMarker, shouldSDKDecode));
            }

            if (root.getChild("NextVersionIdMarker") != null) {
                String nextMarker = root.getChildText("NextVersionIdMarker");
                versionListing.setNextVersionIdMarker(isNullOrEmpty(nextMarker) ? null : nextMarker);
            }

            List<Element> objectSummaryElems = root.getChildren("Version");
            for (Element elem : objectSummaryElems) {
                HOSVersionSummary HOSVersionSummary = new HOSVersionSummary();

                HOSVersionSummary.setKey(decodeIfSpecified(elem.getChildText("Key"), shouldSDKDecode));
                HOSVersionSummary.setVersionId(elem.getChildText("VersionId"));
                HOSVersionSummary.setLatest("true".equals(elem.getChildText("IsLatest")));
                HOSVersionSummary.setETag(trimQuotes(elem.getChildText("ETag")));
                HOSVersionSummary.setLastModified(DateUtil.parseIso8601Date(elem.getChildText("LastModified")));
                HOSVersionSummary.setSize(Long.valueOf(elem.getChildText("Size")));
                HOSVersionSummary.setStorageClass(elem.getChildText("StorageClass"));
                HOSVersionSummary.setBucketName(versionListing.getBucketName());
                HOSVersionSummary.setDeleteMarker(false);

                String id = elem.getChild("Owner").getChildText("ID");
                HOSVersionSummary.setOwner(new Owner(id));

                versionListing.getVersionSummaries().add(HOSVersionSummary);
            }

            List<Element> delSummaryElems = root.getChildren("DeleteMarker");
            for (Element elem : delSummaryElems) {
                HOSVersionSummary HOSVersionSummary = new HOSVersionSummary();

                HOSVersionSummary.setKey(decodeIfSpecified(elem.getChildText("Key"), shouldSDKDecode));
                HOSVersionSummary.setVersionId(elem.getChildText("VersionId"));
                HOSVersionSummary.setLatest("true".equals(elem.getChildText("IsLatest")));
                HOSVersionSummary.setLastModified(DateUtil.parseIso8601Date(elem.getChildText("LastModified")));
                HOSVersionSummary.setBucketName(versionListing.getBucketName());
                HOSVersionSummary.setDeleteMarker(true);

                String id = elem.getChild("Owner").getChildText("ID");
                HOSVersionSummary.setOwner(new Owner(id));

                versionListing.getVersionSummaries().add(HOSVersionSummary);
            }
            return versionListing;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Perform an url decode on the given value if specified.
     */
    private static String decodeIfSpecified(String value, boolean decode) {
        return decode ? urlDecode(value, StringUtils.DEFAULT_ENCODING) : value;
    }

    /**
     * Unmarshall get bucket acl response body to ACL.
     */
    public static AccessControlList parseGetAcl(ResponseMessage response) throws ResponseParseException {
        try {
            Element root = getXmlRootElement(response.getContent());

            AccessControlList result = new AccessControlList();

            Element owner = root.getChild("Owner");
            Owner owner1 = new Owner(owner.getChild("ID").getValue());

            Element accessControlList = root.getChild("AccessControlList");
            List<Element> grants = accessControlList.getChildren("Grant");
            for (Element grant : grants) {
                Element grantee = grant.getChild("Grantee");
                Attribute attribute = grantee.getAttributes().stream().filter(item -> "type".equals(item.getName())).findFirst().get();
                String type = attribute.getValue();
                Grantee grantee1 = null;
                if ("CanonicalUser".equals(type)) {
                    grantee1 = new CanonicalUserGrantee(grantee.getChild("ID").getValue());
                } else if ("Group".equals(type)) {
                    grantee1 = GroupGrantee.AllUsers;
                }
                Permission[] values = Permission.values();
                String permission = grant.getChild("Permission").getValue();
                for (Permission permission1 : values) {
                    if (permission1.getCannedAcl().equals(permission)) {
                        result.getGrants().add(new Grant(grantee1, permission1));
                        continue;
                    }
                }
            }
            result.setOwner(owner1);
            return result;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall upload part copy response body to uploaded part's ETag.
     */
    public static String parseUploadPartCopy(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);
            return root.getChildText("ETag");
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall list bucket response body to bucket list.
     */
    @SuppressWarnings("unchecked")
    public static BucketList parseListBucket(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            BucketList bucketList = new BucketList();
            Element owners = root.getChild("Owner");

            Owner owner = new Owner(owners.getChild("ID").getValue());


            bucketList.setPrefix(getElementValue(root, "Prefix"));
            bucketList.setStartAfter(getElementValue(root, "startAfter"));
            bucketList.setNextStartAfter(getElementValue(root, "NextStartAfter"));
            if (root.getChild("MaxKeys") != null) {
                String value = root.getChildText("MaxKeys");
                bucketList.setMaxKeys(isNullOrEmpty(value) ? null : Integer.valueOf(value));
            }
            if (root.getChild("IsTruncated") != null) {
                String value = root.getChildText("IsTruncated");
                bucketList.setTruncated(isNullOrEmpty(value) ? false : Boolean.valueOf(value));
            }

            List<Bucket> buckets = new ArrayList<Bucket>();
            if (root.getChild("Buckets") != null) {
                List<Element> bucketElems = root.getChild("Buckets").getChildren("Bucket");
                for (Element e : bucketElems) {
                    Bucket bucket = new Bucket();
                    bucket.setBucketName(e.getChildText("Name"));
                    bucket.setCreationDate(DateUtil.parseIso8601Date(e.getChildText("CreationDate")));
                    bucket.setOwner(owner);
                    buckets.add(bucket);
                }
            }
            bucketList.setBucketList(buckets);
            return bucketList;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }

    }

    /**
     * Unmarshall simplified object meta from response headers.
     */
    public static SimplifiedObjectMeta parseSimplifiedObjectMeta(Map<String, String> headers)
            throws ResponseParseException {

        try {
            SimplifiedObjectMeta objectMeta = new SimplifiedObjectMeta();

            for (Iterator<String> it = headers.keySet().iterator(); it.hasNext(); ) {
                String key = it.next();

                if (key.equalsIgnoreCase(LAST_MODIFIED)) {
                    try {
                        objectMeta.setLastModified(DateUtil.parseRfc822Date(headers.get(key)));
                    } catch (ParseException pe) {
                        throw new ResponseParseException(pe.getMessage(), pe);
                    }
                } else if (key.equalsIgnoreCase(HOSHeaders.CONTENT_LENGTH)) {
                    Long value = Long.valueOf(headers.get(key));
                    objectMeta.setSize(value);
                } else if (key.equalsIgnoreCase(HOSHeaders.ETAG)) {
                    objectMeta.setETag(trimQuotes(headers.get(key)));
                } else if (key.equalsIgnoreCase(HOS_HEADER_REQUEST_ID)) {
                    objectMeta.setRequestId(headers.get(key));
                } else if (key.equalsIgnoreCase(HOS_HEADER_VERSION_ID)) {
                    objectMeta.setVersionId(headers.get(key));
                }
            }

            return objectMeta;
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall object metadata from response headers.
     */
    public static ObjectMetadata parseObjectMetadata(Map<String, String> headers) throws ResponseParseException {

        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();

            for (Iterator<String> it = headers.keySet().iterator(); it.hasNext(); ) {
                String key = it.next();

                if (key.indexOf(HOSHeaders.HOS_USER_METADATA_PREFIX) >= 0) {
                    key = key.substring(HOSHeaders.HOS_USER_METADATA_PREFIX.length());
                    objectMetadata.addUserMetadata(key, headers.get(HOSHeaders.HOS_USER_METADATA_PREFIX + key));
                } else if (key.equalsIgnoreCase(LAST_MODIFIED) || key.equalsIgnoreCase(DATE)) {
                    try {
                        objectMetadata.setHeader(key, DateUtil.parseRfc822Date(headers.get(key)));
                    } catch (ParseException pe) {
                        throw new ResponseParseException(pe.getMessage(), pe);
                    }
                } else if (key.equalsIgnoreCase(HOSHeaders.CONTENT_LENGTH)) {
                    Long value = Long.valueOf(headers.get(key));
                    objectMetadata.setHeader(key, value);
                } else if (key.equalsIgnoreCase(HOSHeaders.ETAG)) {
                    objectMetadata.setHeader(key, trimQuotes(headers.get(key)));
                } else {
                    objectMetadata.setHeader(key, headers.get(key));
                }
            }

            return objectMetadata;
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall initiate multipart upload response body to corresponding
     * result.
     */
    public static InitiateMultipartUploadResult parseInitiateMultipartUpload(InputStream responseBody)
            throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            InitiateMultipartUploadResult result = new InitiateMultipartUploadResult();
            if (root.getChild("Bucket") != null) {
                result.setBucketName(root.getChildText("Bucket"));
            }

            if (root.getChild("Key") != null) {
                result.setKey(root.getChildText("Key"));
            }

            if (root.getChild("UploadId") != null) {
                result.setUploadId(root.getChildText("UploadId"));
            }

            return result;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall list multipart uploads response body to multipart upload
     * listing.
     */
    @SuppressWarnings("unchecked")
    public static MultipartUploadListing parseListMultipartUploads(InputStream responseBody)
            throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            MultipartUploadListing multipartUploadListing = new MultipartUploadListing();
            multipartUploadListing.setBucketName(root.getChildText("Bucket"));
            multipartUploadListing.setMaxKeys(Integer.valueOf(root.getChildText("MaxUploads")));
            multipartUploadListing.setTruncated(Boolean.valueOf(root.getChildText("IsTruncated")));

            if (root.getChild("Prefix") != null) {
                String prefix = root.getChildText("Prefix");
                if (!isNullOrEmpty(prefix)) {
                    multipartUploadListing.setPrefix(prefix);
                }
            }

            if (root.getChild("KeyMarker") != null) {
                String keyMarker = root.getChildText("KeyMarker");
                if (!isNullOrEmpty(keyMarker)) {
                    multipartUploadListing.setStartAfter(keyMarker);
                }
            }

            if (root.getChild("NextKeyMarker") != null) {
                String nextKeyMarker = root.getChildText("NextKeyMarker");
                if (!isNullOrEmpty(nextKeyMarker)) {
                    multipartUploadListing.setNextStartAfter(nextKeyMarker);
                }
            }

            List<Element> uploadElems = root.getChildren("Upload");
            for (Element elem : uploadElems) {
                if (elem.getChild("Initiated") == null) {
                    continue;
                }

                MultipartUpload mu = new MultipartUpload();
                mu.setKey(decodeIfSpecified(elem.getChildText("Key"), true));
                mu.setUploadId(elem.getChildText("UploadId"));
                mu.setStorageClass(elem.getChildText("StorageClass"));
                mu.setInitiated(DateUtil.parseIso8601Date(elem.getChildText("Initiated")));
                mu.setOwner(new Owner(elem.getChild("Owner").getChildText("ID")));
                multipartUploadListing.addMultipartUpload(mu);
            }
            return multipartUploadListing;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall list parts response body to part listing.
     */
    @SuppressWarnings("unchecked")
    public static PartListing parseListParts(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            PartListing partListing = new PartListing();
            partListing.setBucketName(root.getChildText("Bucket"));
            partListing.setKey(root.getChildText("Key"));
            partListing.setUploadId(root.getChildText("UploadId"));
            partListing.setOwner(new Owner(root.getChild("Owner").getChildText("ID")));
            partListing.setStorageClass(root.getChildText("StorageClass"));
            partListing.setMaxKeys(Integer.valueOf(root.getChildText("MaxParts")));
            partListing.setTruncated(Boolean.valueOf(root.getChildText("IsTruncated")));

            if (root.getChild("PartNumberMarker") != null) {
                String partNumberMarker = root.getChildText("PartNumberMarker");
                if (!isNullOrEmpty(partNumberMarker)) {
                    partListing.setPartNumberMarker(Integer.valueOf(partNumberMarker));
                }
            }

            if (root.getChild("NextPartNumberMarker") != null) {
                String nextPartNumberMarker = root.getChildText("NextPartNumberMarker");
                if (!isNullOrEmpty(nextPartNumberMarker)) {
                    partListing.setNextPartNumberMarker(Integer.valueOf(nextPartNumberMarker));
                }
            }

            List<Element> partElems = root.getChildren("Part");
            for (Element elem : partElems) {
                PartSummary ps = new PartSummary();

                ps.setPartNumber(Integer.valueOf(elem.getChildText("PartNumber")));
                ps.setLastModified(DateUtil.parseIso8601Date(elem.getChildText("LastModified")));
                ps.setETag(trimQuotes(elem.getChildText("ETag")));
                ps.setSize(Integer.valueOf(elem.getChildText("Size")));

                partListing.addPart(ps);
            }
            return partListing;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }

    }

    /**
     * Unmarshall complete multipart upload response body to corresponding
     * result.
     */
    public static CompleteMultipartUploadResult parseCompleteMultipartUpload(InputStream responseBody)
            throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            CompleteMultipartUploadResult result = new CompleteMultipartUploadResult();
            result.setBucketName(root.getChildText("Bucket"));
            result.setETag(trimQuotes(root.getChildText("ETag")));
            result.setKey(root.getChildText("Key"));
//            result.setLocation(root.getChildText("Location"));

            return result;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * 转换ElementList to StringList
     *
     * @param elementList
     * @return
     */
    private static List<String> parseStringListFromElemet(List<Element> elementList) {
        if (elementList != null && elementList.size() > 0) {
            List<String> list = new ArrayList<String>();
            for (Element element : elementList) {
                list.add(element.getText());
            }
            return list;
        }
        return null;
    }

    /**
     * Unmarshall copy object response body to corresponding result.
     */
    public static CopyObjectResult parseCopyObjectResult(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            CopyObjectResult result = new CopyObjectResult();
            result.setLastModified(DateUtil.parseIso8601Date(root.getChildText("LastModified")));
            result.setEtag(trimQuotes(root.getChildText("ETag")));

            return result;
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall delete objects response body to corresponding result.
     */
    @SuppressWarnings("unchecked")
    public static DeleteObjectsResult parseDeleteObjectsResult(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);

            DeleteObjectsResult deleteObjectsResult = new DeleteObjectsResult();
            if (root.getChild("EncodingType") != null) {
                String encodingType = root.getChildText("EncodingType");
            }

            List<String> deletedObjects = new ArrayList<String>();
            List<Element> deletedElements = root.getChildren("Deleted");
            for (Element elem : deletedElements) {
                deletedObjects.add(elem.getChildText("Key"));
            }
            deleteObjectsResult.setDeletedObjects(deletedObjects);

            return deleteObjectsResult;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall delete versions response body to corresponding result.
     */
    @SuppressWarnings("unchecked")
    public static DeleteVersionsResult parseDeleteVersionsResult(InputStream responseBody)
            throws ResponseParseException {
        boolean shouldSDKDecodeResponse = false;

        try {
            Element root = getXmlRootElement(responseBody);

            if (root.getChild("EncodingType") != null) {
                String encodingType = root.getChildText("EncodingType");
                shouldSDKDecodeResponse = HOSConstants.URL_ENCODING.equals(encodingType);
            }

            List<DeletedVersion> deletedVersions = new ArrayList<DeletedVersion>();
            List<Element> deletedElements = root.getChildren("Deleted");
            for (Element elem : deletedElements) {
                DeletedVersion key = new DeletedVersion();

                if (shouldSDKDecodeResponse) {
                    key.setKey(urlDecode(elem.getChildText("Key"), StringUtils.DEFAULT_ENCODING));
                } else {
                    key.setKey(elem.getChildText("Key"));
                }

                if (elem.getChild("VersionId") != null) {
                    key.setVersionId(elem.getChildText("VersionId"));
                }

                if (elem.getChild("DeleteMarker") != null) {
                    key.setDeleteMarker(Boolean.parseBoolean(elem.getChildText("DeleteMarker")));
                }

                if (elem.getChild("DeleteMarkerVersionId") != null) {
                    key.setDeleteMarkerVersionId(elem.getChildText("DeleteMarkerVersionId"));
                }

                deletedVersions.add(key);
            }

            return new DeleteVersionsResult(deletedVersions);
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket tagging response body to cors rules.
     */
    @SuppressWarnings("unchecked")
    public static TagSet parseGetBucketTagging(InputStream responseBody) throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);
            TagSet tagSet = new TagSet();
            if (root.getChild("TagSet") != null && root.getChild("TagSet").getChildren("Tag") != null) {

                List<Element> tagElems = root.getChild("TagSet").getChildren("Tag");

                for (Element tagElem : tagElems) {
                    String key = null;
                    String value = null;

                    if (tagElem.getChild("Key") != null) {
                        key = tagElem.getChildText("Key");
                    }

                    if (tagElem.getChild("Value") != null) {
                        value = tagElem.getChildText("Value");
                    }

                    tagSet.setTag(key, value);
                }
            }

            return tagSet;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket info response body to bucket info.
     */
    public static Bucket parseGetBucketInfo(ResponseMessage response) throws ResponseParseException {
        try {
            Bucket bucket = new Bucket();
            Map<String, String> headers = response.getHeaders();
            String bytesUsed = headers.get("x-hos-bytes-used");
            String objectCount = headers.get("x-hos-object-count");
            Date date = DateUtil.parseGMTDate(headers.get("Date"));
            bucket.setBytesUsed(isNullOrEmpty(bytesUsed) ? 0 : Long.valueOf(bytesUsed));
            bucket.setObjectCount(isNullOrEmpty(objectCount) ? 0 : Integer.valueOf(objectCount));
            bucket.setCreationDate(date);
            return bucket;
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket info response body to account info.
     */
    public static BucketQuotaResult parseGetQuotaBucket(ResponseMessage response) throws ResponseParseException {
        try {
            BucketQuotaResult bucketQuota = new BucketQuotaResult();
            // do with xml
            if ("application/xml".equals(response.getHeaders().get(CONTENT_TYPE))) {
                Element root = getXmlRootElement(response.getContent());
                String storageQuota = getElementValue(root, "StorageQuota");
                String storageMaxCount = getElementValue(root, "StorageMaxCount");
                bucketQuota.setStorageQuota(storageQuota == null ? 0L : Long.valueOf(storageQuota));
                bucketQuota.setStorageMaxCount(storageMaxCount == null ? 0 : Integer.valueOf(storageMaxCount));
            }
            ResponseMessage responseMessage = new ResponseMessage(response.getRequest());
            responseMessage.setStatusCode(response.getStatusCode());
            bucketQuota.setResponse(responseMessage);
            bucketQuota.setRequestId(response.getHeaders().get("x-hos-request-id"));
            return bucketQuota;
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket info response body to account info.
     */
    public static AccountInfo parseGetAccountInfo(ResponseMessage response) throws ResponseParseException {
        try {
            AccountInfo accountInfo = new AccountInfo();
            // do with xml
            if ("application/xml".equals(response.getHeaders().get(CONTENT_TYPE))) {
                Element root = getXmlRootElement(response.getContent());
                Element storageQuota = root.getChild("StorageQuota");
                if (storageQuota != null) {
                    accountInfo.setStorageQuota(isNullOrEmpty(storageQuota.getValue()) ? 0L : Long.valueOf(storageQuota.getValue()));
                } else {
                    accountInfo.setStorageQuota(0L);
                }
            } else {
                accountInfo.setBucketCount(Integer.valueOf(response.getHeaders().get("x-hos-bucket-count")));
                accountInfo.setObjCount(Integer.valueOf(response.getHeaders().get("x-hos-object-count")));
                accountInfo.setBytesCount(Long.valueOf(response.getHeaders().get("x-hos-bytes-used")));
            }
            ResponseMessage responseMessage = new ResponseMessage(response.getRequest());
            responseMessage.setStatusCode(response.getStatusCode());
            accountInfo.setResponse(responseMessage);
            accountInfo.setRequestId(response.getHeaders().get("x-hos-request-id"));

            return accountInfo;
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket versioning response body to versioning configuration.
     */
    public static BucketVersioningConfiguration parseGetBucketVersioning(InputStream responseBody)
            throws ResponseParseException {

        try {
            Element root = getXmlRootElement(responseBody);
            BucketVersioningConfiguration configuration = new BucketVersioningConfiguration();
            configuration.setStatus(root.getChildText("Status"));
            return configuration;
        } catch (JDOMParseException e) {
            throw new ResponseParseException(e.getPartialDocument() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }

    }

    /**
     * 修复漏洞3.1.1.2    漏洞来源代码扫描报告-cmstoreos-sdk-java-1215-0b57751a.pdf
     * Unmarshall get bucket policy response body .
     */
    public static GetBucketPolicyResult parseGetBucketPolicy(InputStream responseBody) throws ResponseParseException {
        int len;
        byte[] buffer = new byte[10 * 1024];
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            while ((len = responseBody.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }

            StringBuilder sb = new StringBuilder();
            byte[] bytes = os.toByteArray();
            if (bytes.length > 0) {
                sb.append(new String(bytes, StandardCharsets.UTF_8));
            }

            GetBucketPolicyResult result = new GetBucketPolicyResult();
            result.setPolicyText(sb.toString());
            return result;
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket lifecycle response body to lifecycle rules.
     */
    @SuppressWarnings("unchecked")
    public static List<LifecycleRule> parseGetBucketLifecycle(InputStream responseBody) throws ResponseParseException {

        try {

            Element root = getXmlRootElement(responseBody);

            List<LifecycleRule> lifecycleRules = new ArrayList<>();
            List<Element> ruleElements = root.getChildren("Rule");

            for (Element ruleElem : ruleElements) {
                LifecycleRule rule = new LifecycleRule();

                if (ruleElem.getChild("ID") != null) {
                    rule.setId(ruleElem.getChildText("ID"));
                }

                if (ruleElem.getChild("Filter") != null) {
                    LifecycleRule.Filter filter = new LifecycleRule.Filter();
                    filter.setPrefix(ruleElem.getChild("Filter").getChildText("Prefix"));
                    filter.setTag(ruleElem.getChild("Filter").getChildText("Tag"));
                    rule.setFilter(filter);
                }

                if (ruleElem.getChild("Status") != null) {
                    rule.setStatus(LifecycleRule.RuleStatus.valueOf(ruleElem.getChildText("Status")));
                }

                if (ruleElem.getChild("Expiration") != null) {
                    LifecycleRule.Expiration expiration = new LifecycleRule.Expiration();
                    if (ruleElem.getChild("Expiration").getChild("Date") != null) {
                        expiration.setDate(DateUtil.parseIso8601Date(ruleElem.getChild("Expiration").getChildText("Date")));
                    }
                    if (ruleElem.getChild("Expiration").getChild("Days") != null) {
                        expiration.setDays(Integer.parseInt(ruleElem.getChild("Expiration").getChildText("Days")));
                    } else if (ruleElem.getChild("Expiration").getChild("ExpiredObjectDeleteMarker") != null) {
                        expiration.setExpiredObjectDeleteMarker(Boolean.valueOf(ruleElem.getChild("Expiration").getChildText("ExpiredObjectDeleteMarker")));
                    }
                    rule.setExpiration(expiration);
                }


                if (ruleElem.getChild("AbortIncompleteMultipartUpload") != null) {
                    LifecycleRule.AbortIncompleteMultipartUpload abortIncompleteMultipartUpload = new LifecycleRule.AbortIncompleteMultipartUpload();
                    if (ruleElem.getChild("AbortIncompleteMultipartUpload").getChild("daysAfterInitiation") != null) {
                        abortIncompleteMultipartUpload.setDaysAfterInitiation(
                                Integer.parseInt(ruleElem.getChild("AbortMultipartUpload").getChildText("Days")));
                    }

                    rule.setAbortIncompleteMultipartUpload(abortIncompleteMultipartUpload);
                }

                List<Element> transitionElements = ruleElem.getChildren("Transition");
                List<LifecycleRule.Transition> transitions = new ArrayList<>();
                for (Element transitionElem : transitionElements) {
                    LifecycleRule.Transition transition = new LifecycleRule.Transition();
                    if (transitionElem.getChild("Days") != null) {
                        transition.setDays(Integer.parseInt(transitionElem.getChildText("Days")));
                    } else if (transitionElem.getChild("Date") != null) {
                        Date date = DateUtil
                                .parseIso8601Date(transitionElem.getChildText("Date"));
                        transition.setDate(date);
                    }
                    if (transitionElem.getChild("StorageClass") != null) {
                        transition
                                .setStorageClass(StorageClass.parse(transitionElem.getChildText("StorageClass")));
                    }
                    transitions.add(transition);
                }
                rule.setTransitions(transitions);

                if (ruleElem.getChild("NoncurrentVersionExpiration") != null) {
                    if (ruleElem.getChild("NoncurrentVersionExpiration").getChild("NoncurrentDays") != null) {
                        LifecycleRule.NoncurrentVersionExpiration noncurrentVersionExpiration = new LifecycleRule.NoncurrentVersionExpiration();
                        noncurrentVersionExpiration.setNoncurrentDays(Integer.parseInt(ruleElem.getChild("NoncurrentVersionExpiration").getChildText("NoncurrentDays")));
                        rule.setNoncurrentVersionExpiration(noncurrentVersionExpiration);
                    }
                }

                Element noncurrentVersionTransitionElement = ruleElem.getChild("NoncurrentVersionTransition");
                if (noncurrentVersionTransitionElement != null) {
                    LifecycleRule.NoncurrentVersionTransition noncurrentVersionTransition = new LifecycleRule.NoncurrentVersionTransition();
                    if (noncurrentVersionTransitionElement.getChild("NoncurrentDays") != null) {
                        noncurrentVersionTransition.setNoncurrentDays(Integer.parseInt(noncurrentVersionTransitionElement.getChildText("NoncurrentDays")));
                    }
                    rule.setNoncurrentVersionTransition(noncurrentVersionTransition);
                }
                lifecycleRules.add(rule);
            }
            return lifecycleRules;
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }
}
