package com.heredata.hos.operation;

import com.heredata.ResponseMessage;
import com.heredata.comm.HttpMethod;
import com.heredata.comm.ServiceClient;
import com.heredata.comm.io.FixedLengthInputStream;
import com.heredata.event.ProgressEventType;
import com.heredata.event.ProgressListener;
import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import com.heredata.handler.ResponseHandler;
import com.heredata.auth.CredentialsProvider;
import com.heredata.hos.comm.HOSHeaders;
import com.heredata.hos.comm.HOSRequestMessage;
import com.heredata.hos.handler.HOSCallbackErrorResponseHandler;
import com.heredata.hos.handler.HOSRequestMessageBuilder;
import com.heredata.hos.model.*;
import com.heredata.hos.parser.ResponseParsers;
import com.heredata.hos.utils.HOSUtils;
import com.heredata.model.VoidResult;
import com.heredata.model.WebServiceRequest;
import com.heredata.utils.IOUtils;
import com.heredata.utils.LogUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.heredata.comm.HttpConstants.DEFAULT_FILE_SIZE_LIMIT;
import static com.heredata.event.ProgressPublisher.publishProgress;
import static com.heredata.hos.comm.HOSRequestParameters.*;
import static com.heredata.hos.parser.RequestMarshallers.completeMultipartUploadRequestMarshaller;
import static com.heredata.hos.parser.ResponseParsers.*;
import static com.heredata.hos.utils.HOSUtils.*;
import static com.heredata.utils.CodingUtils.*;
import static com.heredata.utils.IOUtils.newRepeatableInputStream;
import static com.heredata.utils.LogUtils.logException;
import static com.heredata.utils.StringUtils.trimQuotes;

/**
 * <p>Title: HOSMultipartOperation</p>
 * <p>Description: 分片操作类 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:12
 */
public class HOSMultipartOperation extends HOSOperation {

    private static final int LIST_PART_MAX_RETURNS = 1000;
    private static final int LIST_UPLOAD_MAX_RETURNS = 1000;
    private static final int MAX_PART_NUMBER = 10000;

    public HOSMultipartOperation(ServiceClient client, CredentialsProvider credsProvider) {
        super(client, credsProvider);
    }

    @Override
    protected boolean isRetryablePostRequest(WebServiceRequest request) {
        if (request instanceof InitiateMultipartUploadRequest) {
            return true;
        }
        return super.isRetryablePostRequest(request);
    }

    /**
     * Abort multipart upload.
     */
    public VoidResult abortMultipartUpload(AbortMultipartUploadRequest abortMultipartUploadRequest)
            throws ServiceException, ClientException {

        assertParameterNotNull(abortMultipartUploadRequest, "abortMultipartUploadRequest");

        String key = abortMultipartUploadRequest.getKey();
        String bucketName = abortMultipartUploadRequest.getBucketName();
        String uploadId = abortMultipartUploadRequest.getUploadId();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);
        assertStringNotNullOrEmpty(uploadId, "uploadId");

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(UPLOAD_ID, uploadId);

        Map<String, String> headers = new HashMap<String, String>();

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(abortMultipartUploadRequest))
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(parameters)
                .setAccount(credsProvider.getCredentials().getAccount()).setOriginalRequest(abortMultipartUploadRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, key);
    }

    /**
     * Complete multipart upload.
     */
    public CompleteMultipartUploadResult completeMultipartUpload(
            CompleteMultipartUploadRequest completeMultipartUploadRequest) throws ServiceException, ClientException {

        assertParameterNotNull(completeMultipartUploadRequest, "completeMultipartUploadRequest");

        String key = completeMultipartUploadRequest.getKey();
        String bucketName = completeMultipartUploadRequest.getBucketName();
        String uploadId = completeMultipartUploadRequest.getUploadId();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);
        assertStringNotNullOrEmpty(uploadId, "uploadId");
//        ensureCallbackValid(completeMultipartUploadRequest.getCallback());

        Map<String, String> headers = new HashMap<String, String>();
//        populateRequestCallback(headers, completeMultipartUploadRequest.getCallback());

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(UPLOAD_ID, uploadId);

        List<PartETag> partETags = completeMultipartUploadRequest.getPartETags();
        FixedLengthInputStream requestInstream;
        if (partETags != null) {
            Collections.sort(partETags, new Comparator<PartETag>() {
                @Override
                public int compare(PartETag p1, PartETag p2) {
                    return p1.getPartNumber() - p2.getPartNumber();
                }
            });
            byte[] marshall = completeMultipartUploadRequestMarshaller.marshall(completeMultipartUploadRequest);
            requestInstream = new FixedLengthInputStream(new ByteArrayInputStream(marshall), marshall.length);
        } else {
            requestInstream = new FixedLengthInputStream(new ByteArrayInputStream("".getBytes()), 0);
        }

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(completeMultipartUploadRequest))
                .setMethod(HttpMethod.POST).setBucket(bucketName).setKey(key).setHeaders(headers)
                .setParameters(parameters).setAccount(credsProvider.getCredentials().getAccount())
                .setInputStreamWithLength(requestInstream)
                .setOriginalRequest(completeMultipartUploadRequest).build();

        List<ResponseHandler> reponseHandlers = new ArrayList<ResponseHandler>();
        reponseHandlers.add(new HOSCallbackErrorResponseHandler());

        CompleteMultipartUploadResult result = null;
        if (!isNeedReturnResponse(completeMultipartUploadRequest)) {
            result = doOperation(request, completeMultipartUploadResponseParser, bucketName, key, true);
        } else {
            result = doOperation(request, completeMultipartUploadProcessResponseParser, bucketName, key, true, null,
                    reponseHandlers);
        }

//        if (partETags != null) {
//            result.setClientCRC(calcObjectCRCFromParts(partETags));
//        }
        if (getInnerClient().getClientConfiguration().isCrcCheckEnabled()) {
            HOSUtils.checkChecksum(result.getClientCRC(), result.getServerCRC(), result.getRequestId());
        }

        result.setBucketName(bucketName);
        result.setKey(key);
        return result;
    }

    /**
     * Initiate multipart upload.
     */
    public InitiateMultipartUploadResult initiateMultipartUpload(
            InitiateMultipartUploadRequest initiateMultipartUploadRequest) throws ServiceException, ClientException {

        assertParameterNotNull(initiateMultipartUploadRequest, "initiateMultipartUploadRequest");

        String key = initiateMultipartUploadRequest.getKey();
        String bucketName = initiateMultipartUploadRequest.getBucketName();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);

        Map<String, String> headers = new HashMap<String, String>();
        if (initiateMultipartUploadRequest.getObjectMetadata() != null) {
            populateRequestMetadata(headers, initiateMultipartUploadRequest.getObjectMetadata());
        }

        if (initiateMultipartUploadRequest.getServerSideEncryption() != null) {
            headers.put(HOSHeaders.HOS_SERVER_SIDE_ENCRYPTION, initiateMultipartUploadRequest.getServerSideEncryption());
            headers.put(HOSHeaders.HOS_SERVER_SIDE_ENCRYPTION_KEY_ID, initiateMultipartUploadRequest.getServerSideEncryption());
        }

        if (initiateMultipartUploadRequest.getClientSideEncryptionAlgorithm() != null) {
            assertParameterNotNull(initiateMultipartUploadRequest.getClientSideEncryptionAlgorithm(), "serverSideEncryptionCustomerAlgorithm");
            assertParameterNotNull(initiateMultipartUploadRequest.getClientSideEncryptionKey(), "serverSideEncryptionCustomerKey");
            assertParameterNotNull(initiateMultipartUploadRequest.getClientSideEncryptionKeyMD5(), "serverSideEncryptionCustomerKeyMD5");
            headers.put(HOSHeaders.HOS_CLIENT_SIDE_ENCRYPTION_ALGORITHM, initiateMultipartUploadRequest.getClientSideEncryptionAlgorithm().name());
            headers.put(HOSHeaders.HOS_CLIENT_SIDE_ENCRYPTION_KEY, initiateMultipartUploadRequest.getClientSideEncryptionKey());
            headers.put(HOSHeaders.HOS_CLIENT_SIDE_ENCRYPTION_KEY_MD5, initiateMultipartUploadRequest.getClientSideEncryptionKeyMD5());
        }

        // Be careful that we don't send the object's total size as the content
        // length for the InitiateMultipartUpload request.
        removeHeader(headers, HOSHeaders.CONTENT_LENGTH);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_UPLOADS, null);

        // Set the request content to be empty (but not null) to avoid putting
        // parameters
        // to request body. Set HttpRequestFactory#createHttpRequest for
        // details.
        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(initiateMultipartUploadRequest))
                .setMethod(HttpMethod.POST).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(params)
                .setInputStream(new ByteArrayInputStream(new byte[0])).setInputSize(0).setAccount(credsProvider.getCredentials().getAccount())
                .setOriginalRequest(initiateMultipartUploadRequest).build();

        return doOperation(request, initiateMultipartUploadResponseParser, bucketName, key, true);
    }

    /**
     * List multipart uploads.
     */
    public MultipartUploadListing listMultipartUploads(ListMultipartUploadsRequest listMultipartUploadsRequest)
            throws ServiceException, ClientException {

        assertParameterNotNull(listMultipartUploadsRequest, "listMultipartUploadsRequest");

        String bucketName = listMultipartUploadsRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        // Use a LinkedHashMap to preserve the insertion order.
        Map<String, String> params = new LinkedHashMap<String, String>();
        populateListMultipartUploadsRequestParameters(listMultipartUploadsRequest, params);

        Map<String, String> headers = new HashMap<String, String>();

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(listMultipartUploadsRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setHeaders(headers).setParameters(params)
                .setAccount(credsProvider.getCredentials().getAccount()).setOriginalRequest(listMultipartUploadsRequest).build();

        return doOperation(request, listMultipartUploadsResponseParser, bucketName, null, true);
    }

    /**
     * List parts.
     */
    public PartListing listParts(ListPartsRequest listPartsRequest) throws ServiceException, ClientException {

        assertParameterNotNull(listPartsRequest, "listPartsRequest");

        String key = listPartsRequest.getKey();
        String bucketName = listPartsRequest.getBucketName();
        String uploadId = listPartsRequest.getUploadId();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);
        assertStringNotNullOrEmpty(uploadId, "uploadId");

        // Use a LinkedHashMap to preserve the insertion order.
        Map<String, String> params = new LinkedHashMap<>();
        populateListPartsRequestParameters(listPartsRequest, params);

        Map<String, String> headers = new HashMap<>();

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(listPartsRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(params)
                .setOriginalRequest(listPartsRequest).setAccount(credsProvider.getCredentials().getAccount()).build();

        return doOperation(request, listPartsResponseParser, bucketName, key, true);
    }

    /**
     * Upload part.
     */
    public UploadPartResult uploadPart(UploadPartRequest uploadPartRequest) throws ServiceException, ClientException {

        assertParameterNotNull(uploadPartRequest, "uploadPartRequest");

        String key = uploadPartRequest.getKey();
        String bucketName = uploadPartRequest.getBucketName();
        String uploadId = uploadPartRequest.getUploadId();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);
        assertStringNotNullOrEmpty(uploadId, "uploadId");

        if (uploadPartRequest.getInputStream() == null) {
            throw new IllegalArgumentException(HOS_RESOURCE_MANAGER.getString("MustSetContentStream"));
        }

        InputStream repeatableInputStream = null;
        try {
            repeatableInputStream = newRepeatableInputStream(uploadPartRequest.buildPartialStream());
        } catch (IOException ex) {
            logException("Cannot wrap to repeatable input stream: ", ex);
            throw new ClientException("Cannot wrap to repeatable input stream: ", ex);
        }

        int partNumber = uploadPartRequest.getPartNumber();
        if (!checkParamRange(partNumber, 0, false, MAX_PART_NUMBER, true)) {
            throw new IllegalArgumentException(HOS_RESOURCE_MANAGER.getString("PartNumberOutOfRange"));
        }

        Map<String, String> headers = new HashMap<String, String>();
        populateUploadPartOptionalHeaders(uploadPartRequest, headers);

        // Use a LinkedHashMap to preserve the insertion order.
        Map<String, String> params = new LinkedHashMap<String, String>();
        params.put(PART_NUMBER, Integer.toString(partNumber));
        params.put(UPLOAD_ID, uploadId);

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(uploadPartRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setKey(key).setParameters(params).setHeaders(headers)
                .setInputStream(repeatableInputStream).setInputSize(uploadPartRequest.getPartSize()).setAccount(credsProvider.getCredentials().getAccount())
                .setUseChunkEncoding(uploadPartRequest.isUseChunkEncoding()).setOriginalRequest(uploadPartRequest).build();

        final ProgressListener listener = uploadPartRequest.getProgressListener();
        ResponseMessage response = null;
        try {
            publishProgress(listener, ProgressEventType.TRANSFER_PART_STARTED_EVENT);
            response = doOperation(request, emptyResponseParser, bucketName, key);
            publishProgress(listener, ProgressEventType.TRANSFER_PART_COMPLETED_EVENT);
        } catch (RuntimeException e) {
            publishProgress(listener, ProgressEventType.TRANSFER_PART_FAILED_EVENT);
            throw e;
        } finally {
            // 修复漏洞3.2.2.1  资源没有安全释放  漏洞来源代码扫描报告-cmstoreos-sdk-java-1215-0b57751a.pdf
            IOUtils.safeCloseStream(repeatableInputStream);
        }

        UploadPartResult result = new UploadPartResult();
        result.setPartNumber(partNumber);
        result.setETag(trimQuotes(response.getHeaders().get(HOSHeaders.ETAG)));
        result.setRequestId(response.getRequestId());
        result.setPartSize(uploadPartRequest.getPartSize());
        result.setResponse(response);
        ResponseParsers.setCRC(result, response);

        if (getInnerClient().getClientConfiguration().isCrcCheckEnabled()) {
            HOSUtils.checkChecksum(result.getClientCRC(), result.getServerCRC(), result.getRequestId());
        }

        return result;
    }

    private static void populateListMultipartUploadsRequestParameters(
            ListMultipartUploadsRequest listMultipartUploadsRequest, Map<String, String> params) {

        // Make sure 'uploads' be the first parameter.
        params.put(SUBRESOURCE_UPLOADS, null);

        if (listMultipartUploadsRequest.getStartAfter() != null) {
            params.put(KEY_MARKER, listMultipartUploadsRequest.getStartAfter());
        }

        Integer maxUploads = listMultipartUploadsRequest.getMaxKeys();
        if (maxUploads != null) {
            if (!checkParamRange(maxUploads, 0, true, LIST_UPLOAD_MAX_RETURNS, true)) {
                throw new IllegalArgumentException(
                        HOS_RESOURCE_MANAGER.getFormattedString("MaxUploadsOutOfRange", LIST_UPLOAD_MAX_RETURNS));
            }
            params.put(MAX_UPLOADS, listMultipartUploadsRequest.getMaxKeys().toString());
        }

        if (listMultipartUploadsRequest.getPrefix() != null) {
            params.put(PREFIX, listMultipartUploadsRequest.getPrefix());
        }

//        if (listMultipartUploadsRequest.getUploadIdMarker() != null) {
//            params.put(UPLOAD_ID_MARKER, listMultipartUploadsRequest.getUploadIdMarker());
//        }

    }

    private static void populateListPartsRequestParameters(ListPartsRequest listPartsRequest,
                                                           Map<String, String> params) {

        params.put(UPLOAD_ID, listPartsRequest.getUploadId());

        Integer maxParts = listPartsRequest.getMaxKeys();
        if (maxParts != null) {
            if (!checkParamRange(maxParts, 0, true, LIST_PART_MAX_RETURNS, true)) {
                throw new IllegalArgumentException(
                        HOS_RESOURCE_MANAGER.getFormattedString("MaxPartsOutOfRange", LIST_PART_MAX_RETURNS));
            }
            params.put(MAX_PARTS, maxParts.toString());
        }

        Integer partNumberMarker = listPartsRequest.getPartNumberMarker();
        if (partNumberMarker != null) {
            if (!checkParamRange(partNumberMarker, 0, false, MAX_PART_NUMBER, true)) {
                throw new IllegalArgumentException(HOS_RESOURCE_MANAGER.getString("PartNumberMarkerOutOfRange"));
            }
            params.put(PART_NUMBER_MARKER, partNumberMarker.toString());
        }

    }

    private static void populateUploadPartOptionalHeaders(UploadPartRequest uploadPartRequest,
                                                          Map<String, String> headers) {

        if (!uploadPartRequest.isUseChunkEncoding()) {
            long partSize = uploadPartRequest.getPartSize();
            if (!checkParamRange(partSize, 0, true, DEFAULT_FILE_SIZE_LIMIT, true)) {
                throw new IllegalArgumentException(HOS_RESOURCE_MANAGER.getString("FileSizeOutOfRange"));
            }
            headers.put(HOSHeaders.CONTENT_LENGTH, Long.toString(partSize));
        }

        if (uploadPartRequest.getMd5Digest() != null) {
            headers.put(HOSHeaders.CONTENT_MD5, uploadPartRequest.getMd5Digest());
        }
        ObjectMetadata objectMetadata = uploadPartRequest.getObjectMetadata();
        if (objectMetadata != null && objectMetadata.getClientSideEncryptionAlgorithm() != null) {
            assertParameterNotNull(objectMetadata.getClientSideEncryptionAlgorithm(), "serverSideEncryptionCustomerAlgorithm");
            assertParameterNotNull(objectMetadata.getClientSideEncryptionKey(), "serverSideEncryptionCustomerKey");
            assertParameterNotNull(objectMetadata.getClientSideEncryptionKeyMD5(), "serverSideEncryptionCustomerKeyMD5");
            headers.put(HOSHeaders.HOS_CLIENT_SIDE_ENCRYPTION_ALGORITHM, objectMetadata.getClientSideEncryptionAlgorithm());
            headers.put(HOSHeaders.HOS_CLIENT_SIDE_ENCRYPTION_KEY, objectMetadata.getClientSideEncryptionKey());
            headers.put(HOSHeaders.HOS_CLIENT_SIDE_ENCRYPTION_KEY_MD5, objectMetadata.getClientSideEncryptionKeyMD5());
        }

    }

    private static boolean isNeedReturnResponse(CompleteMultipartUploadRequest completeMultipartUploadRequest) {
//        if (completeMultipartUploadRequest.getCallback() != null
//                || completeMultipartUploadRequest.getProcess() != null) {
//            return true;
//        }
        return false;
    }
}
