package com.heredata.swift.operation;

import com.heredata.HttpHeaders;
import com.heredata.comm.HttpMethod;
import com.heredata.comm.ServiceClient;
import com.heredata.comm.io.RepeatableFileInputStream;
import com.heredata.event.ProgressEventType;
import com.heredata.event.ProgressListener;
import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import com.heredata.handler.ResponseHandler;
import com.heredata.model.VoidResult;
import com.heredata.parser.ResponseParser;
import com.heredata.swift.SwiftErrorCode;
import com.heredata.auth.CredentialsProvider;
import com.heredata.swift.comm.SWIFTRequestMessage;
import com.heredata.swift.comm.SwiftHeaders;
import com.heredata.swift.handler.SwiftCallbackErrorResponseHandler;
import com.heredata.swift.internal.Mimetypes;
import com.heredata.swift.internal.RequestMessageBuilder;
import com.heredata.swift.model.*;
import com.heredata.swift.utils.SwiftUtils;
import com.heredata.utils.IOUtils;
import com.heredata.utils.RangeSpec;

import java.io.*;
import java.net.URL;
import java.util.*;

import static com.heredata.HttpHeaders.CONTENT_LENGTH;
import static com.heredata.HttpHeaders.CONTENT_TYPE;
import static com.heredata.comm.HttpConstants.DEFAULT_BUFFER_SIZE;
import static com.heredata.event.ProgressPublisher.publishProgress;
import static com.heredata.swift.comm.SwiftHeaders.SWIFT_USER_METADATA_PREFIX;
import static com.heredata.swift.parser.ResponseParsers.*;
import static com.heredata.swift.utils.SwiftUtils.*;
import static com.heredata.utils.CodingUtils.assertParameterNotNull;
import static com.heredata.utils.CodingUtils.assertTrue;
import static com.heredata.utils.IOUtils.*;
import static com.heredata.utils.LogUtils.getLog;
import static com.heredata.utils.LogUtils.logException;

/**
 * <p>Title: SwiftObjectOperation</p>
 * <p>Description: 对象操作类 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 18:05
 */
public class SwiftObjectOperation extends SwiftOperation {

    public SwiftObjectOperation(ServiceClient client, CredentialsProvider credsProvider) {
        super(client, credsProvider);
    }

    /**
     * Upload input stream or file to HOS.
     */
    public PutObjectResult putObject(PutObjectRequest putObjectRequest) throws ServiceException, ClientException {

        assertParameterNotNull(putObjectRequest, "putObjectRequest");

        PutObjectResult result = null;

        result = writeObjectInternal(WriteMode.OVERWRITE, putObjectRequest, putObjectReponseParser);

        if (isCrcCheckEnabled()) {
            SwiftUtils.checkChecksum(result.getClientCRC(), result.getServerCRC(), result.getRequestId());
        }

        return result;
    }

    /**
     * Upload input stream to HOS by using url signature.
     */
    public PutObjectResult putObject(URL signedUrl, InputStream requestContent, long contentLength,
                                     Map<String, String> requestHeaders, boolean useChunkEncoding) throws ServiceException, ClientException {

        assertParameterNotNull(signedUrl, "signedUrl");
        assertParameterNotNull(requestContent, "requestContent");

        if (requestHeaders == null) {
            requestHeaders = new HashMap<String, String>();
        }

        SWIFTRequestMessage request = new SWIFTRequestMessage(null, null);
        request.setMethod(HttpMethod.PUT);
        request.setAbsoluteUrl(signedUrl);
        request.setUseUrlSignature(true);
        request.setContent(requestContent);
        request.setContentLength(determineInputStreamLength(requestContent, contentLength, useChunkEncoding));
        request.setHeaders(requestHeaders);
        request.setUseChunkEncoding(useChunkEncoding);

        PutObjectResult result = null;
        if (requestHeaders.get(SwiftHeaders.SWIFT_HEADER_CALLBACK) == null) {
            result = doOperation(request, putObjectReponseParser, null, null, true);
        } else {
            result = doOperation(request, putObjectProcessReponseParser, null, null, true);
        }

        if (isCrcCheckEnabled()) {
            SwiftUtils.checkChecksum(result.getClientCRC(), result.getServerCRC(), result.getRequestId());
        }

        return result;
    }

    /**
     * Pull an object from HOS.
     */
    public SwiftObject getObject(GetObjectRequest getObjectRequest) throws ServiceException, ClientException {

        assertParameterNotNull(getObjectRequest, "getObjectRequest");

        String bucketName = null;
        String key = null;
        SWIFTRequestMessage request = null;

        assertParameterNotNull(getObjectRequest, "getObjectRequest");

        bucketName = getObjectRequest.getBucketName();
        key = getObjectRequest.getKey();

        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameCreationValid(bucketName);
        ensureObjectKeyValid(key);

        Map<String, String> headers = new HashMap<String, String>();
        populateGetObjectRequestHeaders(getObjectRequest, headers);

        Map<String, String> params = new HashMap<String, String>();

        request = new RequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(getObjectRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setKey(key).setHeaders(headers)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setParameters(params).setOriginalRequest(getObjectRequest).build();

        final ProgressListener listener = getObjectRequest.getProgressListener();
        SwiftObject SwiftObject = null;
        try {
            publishProgress(listener, ProgressEventType.TRANSFER_STARTED_EVENT);
            /**
             * 执行
             */
            SwiftObject = doOperation(request, new GetObjectResponseParser(bucketName, key), bucketName, key, true);
            SwiftObject.setBucketName(bucketName);
            SwiftObject.setKey(key);
        } catch (RuntimeException e) {
            publishProgress(listener, ProgressEventType.TRANSFER_FAILED_EVENT);
            throw e;
        }

        return SwiftObject;
    }

    /**
     * Populate a local file with the specified object.
     */
    public ObjectMetadata getObject(GetObjectRequest getObjectRequest, File file) throws ServiceException, ClientException {

        assertParameterNotNull(file, "file");

        SwiftObject SwiftObject = getObject(getObjectRequest);

        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = IOUtils.readNBytes(SwiftObject.getObjectContent(), buffer, 0, buffer.length)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }

            if (isCrcCheckEnabled() && !hasRangeInRequest(getObjectRequest)) {
                Long clientCRC = IOUtils.getCRCValue(SwiftObject.getObjectContent());
                SwiftUtils.checkChecksum(clientCRC, SwiftObject.getServerCRC(), SwiftObject.getRequestId());
            }

            return SwiftObject.getMetadata();
        } catch (IOException ex) {
            logException("Cannot read object content stream: ", ex);
            throw new ClientException(SWIFT_RESOURCE_MANAGER.getString("CannotReadContentStream"), ex);
        } finally {
            safeClose(outputStream);
            safeClose(SwiftObject.getObjectContent());
        }
    }

    /**
     * Get simplified object meta.
     */
    public SimplifiedObjectMeta getSimplifiedObjectMeta(GenericRequest genericRequest) {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        String key = genericRequest.getKey();

        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameCreationValid(bucketName);
        ensureObjectKeyValid(key);

        Map<String, String> params = new HashMap<String, String>();

        Map<String, String> headers = new HashMap<String, String>();

        SWIFTRequestMessage request = new RequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.HEAD).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(params)
                .setOriginalRequest(genericRequest).setAccount(credsProvider.getCredentials().getAccount()).build();

        SwiftObject SwiftObject = doOperation(request, new GetObjectResponseParser(bucketName, key), bucketName, key);
        SimplifiedObjectMeta simplifiedObjectMeta = new SimplifiedObjectMeta();
        simplifiedObjectMeta.setETag(SwiftObject.getMetadata().getETag());
        simplifiedObjectMeta.setSize(SwiftObject.getMetadata().getContentLength());
        simplifiedObjectMeta.setRequestId(SwiftObject.getRequestId());
        simplifiedObjectMeta.setResponse(SwiftObject.getResponse());
        return simplifiedObjectMeta;
//        return doOperation(request, getSimplifiedObjectMetaResponseParser, bucketName, key);
    }

    /**
     * Get object matadata.
     */
    public ObjectMetadata getObjectMeta(GenericRequest genericRequest)
            throws ServiceException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        String key = genericRequest.getKey();

        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameCreationValid(bucketName);
        ensureObjectKeyValid(key);

        Map<String, String> params = new HashMap<String, String>();

        Map<String, String> headers = new HashMap<>();
        SWIFTRequestMessage request = new RequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.HEAD).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(params)
                .setAccount(this.credsProvider.getCredentials().getAccount())
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getObjectMetadataResponseParser, bucketName, key);
    }

    /**
     * Copy an existing object to another one.
     */
    public CopyObjectResult copyObject(CopyObjectRequest copyObjectRequest) throws ServiceException, ClientException {

        assertParameterNotNull(copyObjectRequest, "copyObjectRequest");

        Map<String, String> headers = new HashMap<>();
        populateCopyObjectHeaders(copyObjectRequest, headers);
        headers.put(CONTENT_LENGTH, 0 + "");
//        headers.put(CONTENT_TYPE, Mimetypes.DEFAULT_MIMETYPE);


        SWIFTRequestMessage request = new RequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(copyObjectRequest))
                .setMethod(HttpMethod.PUT).setBucket(copyObjectRequest.getTargetBucketName())
                .setAccount(credsProvider.getCredentials().getAccount()).setKey(copyObjectRequest.getTargetKey())
                .setHeaders(headers).setOriginalRequest(copyObjectRequest).build();

        return doOperation(request, copyObjectResponseParser, copyObjectRequest.getTargetBucketName(),
                copyObjectRequest.getTargetKey(), true);
    }

    /**
     * Delete an object.
     */
    public VoidResult deleteObject(GenericRequest genericRequest) throws ServiceException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        String key = genericRequest.getKey();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameCreationValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);

        Map<String, String> headers = new HashMap<String, String>();

        SWIFTRequestMessage request = new RequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint(genericRequest)).setMethod(HttpMethod.DELETE).setBucket(bucketName)
                .setKey(key).setHeaders(headers).setOriginalRequest(genericRequest)
                .setAccount(credsProvider.getCredentials().getAccount())
                .build();

        return doOperation(request, requestIdResponseParser, bucketName, key);
    }

    /**
     * Delete multiple objects.
     */
    public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest) {

        assertParameterNotNull(deleteObjectsRequest, "deleteObjectsRequest");

        String bucketName = deleteObjectsRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameCreationValid(bucketName);
        DeleteObjectsResult deleteObjectsResult = new DeleteObjectsResult();
        List<String> deletedObjects = new ArrayList<String>();
        if (!deleteObjectsRequest.getKeys().isEmpty()) {
            List<String> key = deleteObjectsRequest.getKeys();
            key.forEach(item -> {
                VoidResult voidResult = deleteObject(new GenericRequest(bucketName, item));
                if (voidResult.getResponse().isSuccessful()) {
                    deletedObjects.add(item);
                }
            });

            deleteObjectsResult.setDeletedObjects(deletedObjects);
        }
        return deleteObjectsResult;
    }

    public boolean doesObjectExist(GenericRequest genericRequest) throws ServiceException, ClientException {
        try {
            this.getSimplifiedObjectMeta(genericRequest);
            return true;
        } catch (ServiceException e) {
            if (e.getErrorCode().equals(SwiftErrorCode.NO_SUCH_BUCKET)
                    || e.getErrorCode().equals(SwiftErrorCode.NO_SUCH_KEY)) {
                return false;
            }
            throw e;
        }
    }

    public VoidResult setObjectMeta(String bucketName, String key, ObjectMetadata objectMetadata) {
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameCreationValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);

        Map<String, String> headers = new HashMap<>();
        Map<String, String> userMetadata = objectMetadata.getUserMetadata();
        userMetadata.forEach((k, v) -> headers.put(SWIFT_USER_METADATA_PREFIX + k, v));

        Map<String, String> params = new HashMap<String, String>();

        SWIFTRequestMessage request = new RequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST).setBucket(bucketName).setKey(key).setParameters(params).setHeaders(headers)
                .setAccount(credsProvider.getCredentials().getAccount()).build();

        return doOperation(request, requestIdResponseParser, bucketName, key);
    }


    /**
     * An enum to represent different modes the client may specify to upload
     * specified file or inputstream.
     */
    private static enum WriteMode {

        /*
         * If object already not exists, create it. otherwise, append it with
         * the new input
         */
        APPEND("APPEND"),

        /*
         * No matter object exists or not, just overwrite it with the new input
         */
        OVERWRITE("OVERWRITE");

        private final String modeAsString;

        private WriteMode(String modeAsString) {
            this.modeAsString = modeAsString;
        }

        @Override
        public String toString() {
            return this.modeAsString;
        }

        public static HttpMethod getMappingMethod(WriteMode mode) {
            switch (mode) {
                case APPEND:
                    return HttpMethod.POST;

                case OVERWRITE:
                    return HttpMethod.PUT;

                default:
                    throw new IllegalArgumentException("Unsuported write mode" + mode.toString());
            }
        }
    }

    private <RequestType extends PutObjectRequest, ResponseType> ResponseType writeObjectInternal(WriteMode mode,
                                                                                                  RequestType originalRequest, ResponseParser<ResponseType> responseParser) {
        final String bucketName = originalRequest.getBucketName();
        final String key = originalRequest.getKey();
        InputStream originalInputStream = originalRequest.getInputStream();

        /**
         * 校验必要信息
         */
        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameCreationValid(bucketName);
        ensureObjectKeyValid(key);

        String contentType = null;
        Long contentLength = null;

        InputStream repeatableInputStream = null;
        if (originalRequest.getFile() != null) {
            File toUpload = originalRequest.getFile();
            if (!checkFile(toUpload)) {
                getLog().info("Illegal file path: " + toUpload.getPath());
                throw new ClientException("Illegal file path: " + toUpload.getPath());
            }

            contentLength = toUpload.length();
            contentType = Mimetypes.getInstance().getMimetype(toUpload, key);

            try {
                repeatableInputStream = new RepeatableFileInputStream(toUpload);
            } catch (IOException ex) {
                logException("Cannot locate file to upload: ", ex);
                throw new ClientException("Cannot locate file to upload: ", ex);
            }
        } else {
            assertTrue(originalInputStream != null, "Please specify input stream or file to upload");

            contentType = Mimetypes.getInstance().getMimetype(key);

            try {
                contentLength = Long.valueOf(originalInputStream.available());
            } catch (IOException e) {
                throw new ServiceException("上传的流无效");
            }

            try {
                repeatableInputStream = newRepeatableInputStream(originalInputStream);
            } catch (IOException ex) {
                logException("Cannot wrap to repeatable input stream: ", ex);
                throw new ClientException("Cannot wrap to repeatable input stream: ", ex);
            }
        }

        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_LENGTH, contentLength + "");
        headers.put(CONTENT_TYPE, contentType);

        Map<String, String> params = new LinkedHashMap<>();

        SWIFTRequestMessage httpRequest = new RequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(originalRequest))
                .setMethod(WriteMode.getMappingMethod(mode)).setBucket(bucketName).setKey(key).setHeaders(headers)
                .setParameters(params).setInputStream(repeatableInputStream).setAccount(credsProvider.getCredentials().getAccount())
                .setInputSize(determineInputStreamLength(repeatableInputStream, contentLength))
                .setOriginalRequest(originalRequest).build();

        List<ResponseHandler> reponseHandlers = new ArrayList<>();
        reponseHandlers.add(new SwiftCallbackErrorResponseHandler());

        final ProgressListener listener = originalRequest.getProgressListener();
        ResponseType result = null;
        try {
            publishProgress(listener, ProgressEventType.TRANSFER_STARTED_EVENT);
            result = doOperation(httpRequest, responseParser, bucketName, key, true);
            publishProgress(listener, ProgressEventType.TRANSFER_COMPLETED_EVENT);
        } catch (RuntimeException e) {
            publishProgress(listener, ProgressEventType.TRANSFER_FAILED_EVENT);
            throw e;
        }
        return result;
    }

    private boolean isCrcCheckEnabled() {
        return getInnerClient().getClientConfiguration().isCrcCheckEnabled();
    }

    private boolean hasRangeInRequest(GetObjectRequest getObjectRequest) {
        return getObjectRequest.getHeaders().get(SwiftHeaders.RANGE) != null;
    }

    private static void populateCopyObjectHeaders(CopyObjectRequest copyObjectRequest, Map<String, String> headers) {

        String copySourceHeader = copyObjectRequest.getSourceBucketName() + "/"
                + copyObjectRequest.getSourceKey();

        headers.put(SwiftHeaders.X_COPY_FROM, copySourceHeader);
        if (copyObjectRequest != null && copyObjectRequest.getFreshMetadata()) {
            headers.put("X-Fresh-Metadata", true + "");
            if (copyObjectRequest.getNewObjectMetadata() != null) {
                populateRequestMetadata(headers, copyObjectRequest.getNewObjectMetadata());
            }
        }


        // The header of Content-Length should not be specified on copying an
        // object.
        removeHeader(headers, HttpHeaders.CONTENT_LENGTH);
    }

    private static void populateGetObjectRequestHeaders(GetObjectRequest getObjectRequest,
                                                        Map<String, String> headers) {

        if (getObjectRequest.getRange() != null) {
            addGetObjectRangeHeader(getObjectRequest.getRange(), headers);
        }
    }

    private static void addGetObjectRangeHeader(long[] range, Map<String, String> headers) {
        RangeSpec rangeSpec = RangeSpec.parse(range);
        headers.put(SwiftHeaders.RANGE, rangeSpec.toString());
    }
}
