package com.heredata.swift.parser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.heredata.ResponseMessage;
import com.heredata.exception.ResponseParseException;
import com.heredata.exception.ServiceException;
import com.heredata.model.GenericResult;
import com.heredata.model.VoidResult;
import com.heredata.parser.ResponseParser;
import com.heredata.swift.comm.SwiftHeaders;
import com.heredata.swift.model.*;
import com.heredata.swift.model.bucket.Bucket;
import com.heredata.swift.model.bucket.BucketAclRequest;
import com.heredata.swift.model.bucket.BucketQuotaResult;
import com.heredata.swift.model.bucket.BukcetListResult;
import com.heredata.utils.DateUtil;
import com.heredata.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.JDOMParseException;
import org.jdom2.input.SAXBuilder;

import java.io.*;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.*;
import java.util.zip.CheckedInputStream;

import static com.heredata.HttpHeaders.CONTENT_TYPE;
import static com.heredata.HttpHeaders.ETAG;
import static com.heredata.swift.comm.SwiftHeaders.X_OPENSTACK_REQUEST_ID;
import static com.heredata.utils.ResourceUtils.safeCloseResponse;
import static com.heredata.utils.StringUtils.*;

public final class ResponseParsers {
    public static final ListBucketResponseParser listBucketResponseParser = new ListBucketResponseParser();
    public static final GetBucketInfoResponseParser getBucketInfoResponseParser = new GetBucketInfoResponseParser();

    public static final PutObjectReponseParser putObjectReponseParser = new PutObjectReponseParser();
    public static final PutObjectProcessReponseParser putObjectProcessReponseParser = new PutObjectProcessReponseParser();
    public static final GetObjectMetadataResponseParser getObjectMetadataResponseParser = new GetObjectMetadataResponseParser();
    public static final CopyObjectResponseParser copyObjectResponseParser = new CopyObjectResponseParser();
    public static final DeleteObjectsResponseParser deleteObjectsResponseParser = new DeleteObjectsResponseParser();
    public static final HeadObjectResponseParser headObjectResponseParser = new HeadObjectResponseParser();

    public static final GetBukcetListResponseParser getBukcetListResponseParser = new GetBukcetListResponseParser();
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

    public static final class ListBucketResponseParser implements ResponseParser<BukcetListResult> {

        @Override
        public BukcetListResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                BukcetListResult result = parseListBucket(response.getContent());
                result.setRequestId(response.getRequestId());
                return result;
            } finally {
                safeCloseResponse(response);
            }
        }

    }

    /**
     * Unmarshall list bucket response body to bucket list.
     */
    @SuppressWarnings("unchecked")
    public static BukcetListResult parseListBucket(InputStream responseBody) throws ResponseParseException {

        try {
            String s = inputStreamToString(responseBody);
            BukcetListResult result = new BukcetListResult();
            if (s != null && s.length() != 0) {
                String[] split = s.split("\n");
                List<Bucket> list = new ArrayList<>();
                Arrays.stream(split).forEach(item -> list.add(new Bucket(item)));
                result.setBucketList(list);
            }
            return result;
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }

    }


    public static final class RequestIdResponseParser implements ResponseParser<VoidResult> {
        @Override
        public VoidResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                VoidResult result = new VoidResult();
                result.setResponse(response);
                result.setRequestId(response.getRequestId());
                return result;
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

    public static final class GetBukcetListResponseParser implements ResponseParser<BukcetListResult> {
        @Override
        public BukcetListResult parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseGetAccountInfo(response);
            } finally {
                safeCloseResponse(response);
            }
        }
    }

    public static final class GetAccountInfoResponseParser implements ResponseParser<AccountInfo> {
        @Override
        public AccountInfo parse(ResponseMessage response) throws ResponseParseException {
            try {
                return parseGetAccountResult(response);
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

    public static final class ListObjectsReponseParser implements ResponseParser<ObjectListing> {
        private String bucketName;

        public ListObjectsReponseParser(String bucketName) {
            this.bucketName = bucketName;
        }

        @Override
        public ObjectListing parse(ResponseMessage response) throws ResponseParseException {
            try {
                ObjectListing result = parseListObjects(response, bucketName);
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
                result.setETag(trimQuotes(response.getHeaders().get(ETAG)));
                result.setRequestId(response.getHeaders().get(X_OPENSTACK_REQUEST_ID));
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
            result.setETag(trimQuotes(response.getHeaders().get(ETAG)));
            result.setResponse(response);
            return result;
        }

    }

    public static final class GetObjectResponseParser implements ResponseParser<SwiftObject> {
        private String bucketName;
        private String key;

        public GetObjectResponseParser(final String bucketName, final String key) {
            this.bucketName = bucketName;
            this.key = key;
        }

        @Override
        public SwiftObject parse(ResponseMessage response) throws ResponseParseException {
            SwiftObject object = new SwiftObject();
            object.setBucketName(this.bucketName);
            object.setKey(this.key);
            object.setObjectContent(response.getContent());
            object.setResponse(response);
            object.setMimeType(response.getHeaders().get(CONTENT_TYPE));
            try {
                object.setETag(response.getHeaders().get(ETAG));
                object.setMetadata(parseObjectMetadata(response.getHeaders()));
                object.setObjectContent(response.getContent());
                return object;
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
                CopyObjectResult result = parseCopyObjectResult(response);
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

    public static <ResultType extends GenericResult> void setCRC(ResultType result, ResponseMessage response) {
        InputStream inputStream = response.getRequest().getContent();
        if (inputStream instanceof CheckedInputStream) {
            CheckedInputStream checkedInputStream = (CheckedInputStream) inputStream;
            result.setClientCRC(checkedInputStream.getChecksum().getValue());
        }

        String strSrvCrc = response.getHeaders().get(SwiftHeaders.SWIFT_HASH_CRC64_ECMA);
        if (strSrvCrc != null) {
            BigInteger bi = new BigInteger(strSrvCrc);
            result.setServerCRC(bi.longValue());
        }
    }

    public static <ResultType extends GenericResult> void setServerCRC(ResultType result, ResponseMessage response) {
        String strSrvCrc = response.getHeaders().get(SwiftHeaders.SWIFT_HASH_CRC64_ECMA);
        if (strSrvCrc != null) {
            BigInteger bi = new BigInteger(strSrvCrc);
            result.setServerCRC(bi.longValue());
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
    public static ObjectListing parseListObjects(ResponseMessage responseBody, String bucketName) throws ResponseParseException {

        try {
            String s = inputStreamToString(responseBody.getContent());
            JSONArray jsonArray = JSONArray.parseArray(s);

            ObjectListing result = new ObjectListing();

            result.setKeyCounts(jsonArray.size());
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                SwiftObjectSummary swiftObjectSummary = new SwiftObjectSummary();
                swiftObjectSummary.setBucketName(bucketName);
                swiftObjectSummary.setETag(jsonObject.getString("hash"));
                swiftObjectSummary.setLastModified(DateUtil.parseIso8601Date(jsonObject.getString("last_modified")));

                swiftObjectSummary.setKey(jsonObject.getString("name"));
                swiftObjectSummary.setSize(Long.valueOf(jsonObject.getString("bytes")));
                result.addObjectSummary(swiftObjectSummary);
            }
            return result;
        } catch (Exception e) {
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
     * Unmarshall simplified object meta from response headers.
     */
    public static SimplifiedObjectMeta parseSimplifiedObjectMeta(Map<String, String> headers)
            throws ResponseParseException {

        try {
            SimplifiedObjectMeta objectMeta = new SimplifiedObjectMeta();

            for (Iterator<String> it = headers.keySet().iterator(); it.hasNext(); ) {
                String key = it.next();

                if (key.equalsIgnoreCase(SwiftHeaders.LAST_MODIFIED)) {
                    try {
                        objectMeta.setLastModified(DateUtil.parseRfc822Date(headers.get(key)));
                    } catch (ParseException pe) {
                        throw new ResponseParseException(pe.getMessage(), pe);
                    }
                } else if (key.equalsIgnoreCase(SwiftHeaders.CONTENT_LENGTH)) {
                    Long value = Long.valueOf(headers.get(key));
                    objectMeta.setSize(value);
                } else if (key.equalsIgnoreCase(ETAG)) {
                    objectMeta.setETag(trimQuotes(headers.get(key)));
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

                if (key.indexOf(SwiftHeaders.SWIFT_USER_METADATA_PREFIX) >= 0) {
                    key = key.substring(SwiftHeaders.SWIFT_USER_METADATA_PREFIX.length());
                    objectMetadata.addUserMetadata(key, headers.get(SwiftHeaders.SWIFT_USER_METADATA_PREFIX + key));
                } else if (key.equalsIgnoreCase(SwiftHeaders.LAST_MODIFIED) || key.equalsIgnoreCase(SwiftHeaders.DATE)) {
                    try {
                        objectMetadata.setHeader(key, DateUtil.parseRfc822Date(headers.get(key)));
                    } catch (ParseException pe) {
                        throw new ResponseParseException(pe.getMessage(), pe);
                    }
                } else if (key.equalsIgnoreCase(SwiftHeaders.CONTENT_LENGTH)) {
                    Long value = Long.valueOf(headers.get(key));
                    objectMetadata.setHeader(key, value);
                } else if (key.equalsIgnoreCase(ETAG)) {
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
     * Unmarshall copy object response body to corresponding result.
     */
    public static CopyObjectResult parseCopyObjectResult(ResponseMessage responseMessage) throws ResponseParseException {

        try {
            CopyObjectResult result = new CopyObjectResult();
            Map<String, String> headers = responseMessage.getHeaders();
            result.setEtag(headers.get(ETAG));
            result.setRequestId(headers.get(X_OPENSTACK_REQUEST_ID));
            result.setKey(headers.get("X-Copied-From"));
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
     * Unmarshall get bucket info response body to bucket info.
     */
    public static Bucket parseGetBucketInfo(ResponseMessage response) throws ResponseParseException {
        try {
            Bucket bucket = new Bucket();
            Map<String, String> headers = response.getHeaders();
            String bytesUsed = headers.get("X-Container-Bytes-Used");
            String objCount = headers.get("X-Container-Object-Count");
            bucket.setBytesUsed(isNullOrEmpty(bytesUsed) ? null : Long.valueOf(bytesUsed));
            bucket.setObjCount(isNullOrEmpty(objCount) ? null : Integer.valueOf(objCount));

            String quotaCount = headers.get("X-Container-Meta-Quota-Count");
            String quotaBytes = headers.get("X-Container-Meta-Quota-Bytes");
            bucket.setQuotaCount(isNullOrEmpty(quotaCount) ? 0 : Integer.valueOf(quotaBytes));
            bucket.setQuotaByte(isNullOrEmpty(quotaBytes) ? 0L : Long.valueOf(quotaBytes));

            String readStr = headers.get("X-Container-Read");
            BucketAclRequest bucketAclRequest = new BucketAclRequest();
            if (!isNullOrEmpty(readStr) && readStr.contains(".r:*")) {
                bucketAclRequest.setAllUserReadObject(true);
            } else {
                bucketAclRequest.setAllUserReadObject(false);
            }
            if (!isNullOrEmpty(readStr) && readStr.contains(".rlistings")) {
                bucketAclRequest.setHeadOrGetBukcet(true);
            } else {
                bucketAclRequest.setHeadOrGetBukcet(false);
            }
            if (!isNullOrEmpty(readStr)) {
                String[] split = readStr.split(",");
                for (int i = 0; i < split.length; i++) {
                    String[] split1 = split[i].split(":");
                    if (split1.length == 2) {
                        if (!".r".equals(split1[0])) {
                            KeyValue keyValue = new KeyValue(split1[0], split1[1]);
                            bucketAclRequest.addTokenReadKeyValue(keyValue);
                        }
                    }
                }
            }

            String writeStr = headers.get("X-Container-Write");
            if (!isNullOrEmpty(writeStr)) {
                String[] split = writeStr.split(",");
                for (int i = 0; i < split.length; i++) {
                    String[] split1 = split[i].split(":");
                    if (split1.length == 2) {
                        KeyValue keyValue = new KeyValue(split1[0], split1[1]);
                        bucketAclRequest.addTokenWriteKeyValue(keyValue);
                    }
                }
            }
            bucket.setBucketAclRequest(bucketAclRequest);

            // 自定义元数据的获取
            Map<String, String> meta = new HashMap<>();
            headers.forEach((k, v) -> {
                if (k.contains("X-Container-Meta-")) {
                    meta.put(k.substring("X-Container-Meta-".length()), v);
                }
            });
            bucket.setMeta(meta);
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
    public static BukcetListResult parseGetAccountInfo(ResponseMessage response) throws ResponseParseException {
        String content = inputStreamToString(response.getContent());
        try {
            BukcetListResult accountInfo = new BukcetListResult();

            Map<String, String> headers = response.getHeaders();
            accountInfo.setRequestId(headers.get("X-Openstack-Request-Id"));

            List<Bucket> list = new ArrayList<>();
            if (content == null) {
                return accountInfo;
            }
            JSONArray jsonArray = JSONArray.parseArray(content);
            // 封装桶列表
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Bucket bucket = new Bucket();
                bucket.setObjCount(jsonObject.getInteger("count"));
                bucket.setBytesUsed(jsonObject.getLong("bytes"));
                bucket.setBucketName(jsonObject.getString("name"));
                list.add(bucket);
            }
            accountInfo.setBucketList(list);
            return accountInfo;
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }

    /**
     * Unmarshall get bucket info response body to account info.
     */
    public static AccountInfo parseGetAccountResult(ResponseMessage response) throws ResponseParseException {
        try {
            AccountInfo accountInfo = new AccountInfo();
            Map<String, String> headers = response.getHeaders();
            accountInfo.setBucketCount(Integer.parseInt(headers.get("X-Account-Container-Count")));
            accountInfo.setObjCount(Integer.parseInt(headers.get("X-Account-Object-Count")));
            accountInfo.setBytesCount(Long.parseLong(headers.get("X-Account-Bytes-Used")));
            accountInfo.setRequestId(headers.get("X-Openstack-Request-Id"));

            return accountInfo;
        } catch (Exception e) {
            throw new ResponseParseException(e.getMessage(), e);
        }
    }


    public static String inputStreamToString(InputStream is) {
        if (is == null) {
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] b = new byte[10240];
        int n;
        try {
            while ((n = is.read(b)) != -1) {
                stream.write(b, 0, n);
            }
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }
        return stream.toString();
    }

    public static String getStringByInputStream_2(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            return result.toString();
        } catch (Exception e) {
            try {
                inputStream.close();
                bufferedReader.close();
            } catch (Exception e1) {
            }
        }
        return null;
    }
}
