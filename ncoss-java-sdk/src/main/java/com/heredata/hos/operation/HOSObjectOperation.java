package com.heredata.hos.operation;

import com.heredata.HttpHeaders;
import com.heredata.comm.HttpMethod;
import com.heredata.comm.ServiceClient;
import com.heredata.comm.io.RepeatableFileInputStream;
import com.heredata.event.ProgressEventType;
import com.heredata.event.ProgressListener;
import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import com.heredata.handler.ResponseHandler;
import com.heredata.hos.HOSErrorCode;
import com.heredata.auth.CredentialsProvider;
import com.heredata.hos.comm.HOSHeaders;
import com.heredata.hos.comm.HOSRequestMessage;
import com.heredata.hos.comm.HOSRequestParameters;
import com.heredata.hos.handler.HOSCallbackErrorResponseHandler;
import com.heredata.hos.handler.HOSRequestMessageBuilder;
import com.heredata.hos.model.*;
import com.heredata.hos.parser.ResponseParsers;
import com.heredata.hos.utils.HOSUtils;
import com.heredata.model.VoidResult;
import com.heredata.parser.ResponseParser;
import com.heredata.utils.*;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import static com.heredata.HttpHeaders.CONTENT_LENGTH;
import static com.heredata.comm.HttpConstants.DEFAULT_BUFFER_SIZE;
import static com.heredata.event.ProgressPublisher.publishProgress;
import static com.heredata.hos.comm.HOSRequestParameters.*;
import static com.heredata.hos.parser.RequestMarshallers.*;
import static com.heredata.hos.parser.ResponseParsers.*;
import static com.heredata.hos.utils.HOSUtils.*;
import static com.heredata.utils.CodingUtils.assertParameterNotNull;
import static com.heredata.utils.CodingUtils.assertTrue;
import static com.heredata.utils.IOUtils.*;
import static com.heredata.utils.LogUtils.getLog;
import static com.heredata.utils.LogUtils.logException;
import static com.heredata.utils.StringUtils.DEFAULT_ENCODING;

/**
 * <p>Title: HOSObjectOperation</p>
 * <p>Description: 对象操作类 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:12
 */
public class HOSObjectOperation extends HOSOperation {

    public HOSObjectOperation(ServiceClient client, CredentialsProvider credsProvider) {
        super(client, credsProvider);
    }

    /**
     * Upload input stream or file to HOS.
     */
    public PutObjectResult putObject(PutObjectRequest putObjectRequest) throws ServiceException, ClientException {

        assertParameterNotNull(putObjectRequest, "putObjectRequest");

        PutObjectResult result = writeObjectInternal(WriteMode.OVERWRITE, putObjectRequest, putObjectProcessReponseParser);

        if (isCrcCheckEnabled()) {
            HOSUtils.checkChecksum(result.getClientCRC(), result.getServerCRC(), result.getRequestId());
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

        HOSRequestMessage request = new HOSRequestMessage(null, null);
        request.setMethod(HttpMethod.PUT);
        request.setAbsoluteUrl(signedUrl);
        request.setUseUrlSignature(true);
        request.setContent(requestContent);
        request.setContentLength(determineInputStreamLength(requestContent, contentLength, useChunkEncoding));
        request.setHeaders(requestHeaders);
        request.setUseChunkEncoding(useChunkEncoding);

        PutObjectResult result = null;
        if (requestHeaders.get(HOSHeaders.HOS_HEADER_CALLBACK) == null) {
            result = doOperation(request, putObjectReponseParser, null, null, true);
        } else {
            result = doOperation(request, putObjectProcessReponseParser, null, null, true);
        }

        if (isCrcCheckEnabled()) {
            HOSUtils.checkChecksum(result.getClientCRC(), result.getServerCRC(), result.getRequestId());
        }

        return result;
    }

    /**
     * Pull an object from HOS.
     */
    public HOSObject getObject(GetObjectRequest getObjectRequest) throws ServiceException, ClientException {

        assertParameterNotNull(getObjectRequest, "getObjectRequest");

        String bucketName = null;
        String key = null;
        HOSRequestMessage request = null;

        // 下面是获取对象详细信息，不包含流信息
        assertParameterNotNull(getObjectRequest, "getObjectRequest");

        bucketName = getObjectRequest.getBucketName();
        key = getObjectRequest.getKey();

        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameValid(bucketName);
        ensureObjectKeyValid(key);
        Map<String, String> headers = new HashMap<String, String>();
        populateGetObjectRequestHeaders(getObjectRequest, headers);
        Map<String, String> params = new HashMap<String, String>();

        String versionId = getObjectRequest.getVersionId();
        if (versionId != null) {
            params.put(HOSRequestParameters.SUBRESOURCE_VRESION_ID, versionId);
        }
        if (!getObjectRequest.isIncludeInputStream()) {


            request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(getObjectRequest))
                    .setMethod(HttpMethod.HEAD).setBucket(bucketName).setKey(key).setHeaders(headers)
                    .setAccount(credsProvider.getCredentials().getAccount())
                    .setParameters(params).setOriginalRequest(getObjectRequest).build();
        } else {
            // 下面是获取对象详细信息，包含流信息，可进行下载
            if (getObjectRequest.getClientSideEncryptionAlgorithm() != null) {
                assertParameterNotNull(getObjectRequest.getClientSideEncryptionAlgorithm(), "serverSideEncryptionCustomerAlgorithm");
                assertParameterNotNull(getObjectRequest.getClientSideEncryptionKey(), "serverSideEncryptionCustomerKey");
                assertParameterNotNull(getObjectRequest.getClientSideEncryptionKeyMD5(), "serverSideEncryptionCustomerKeyMD5");
                headers.put(HOSHeaders.HOS_CLIENT_SIDE_ENCRYPTION_ALGORITHM, getObjectRequest.getClientSideEncryptionAlgorithm());
                headers.put(HOSHeaders.HOS_CLIENT_SIDE_ENCRYPTION_KEY, getObjectRequest.getClientSideEncryptionKey());
                headers.put(HOSHeaders.HOS_CLIENT_SIDE_ENCRYPTION_KEY_MD5, getObjectRequest.getClientSideEncryptionKeyMD5());
            }

            request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(getObjectRequest))
                    .setMethod(HttpMethod.GET).setBucket(getObjectRequest.getBucketName()).setKey(getObjectRequest.getKey())
                    .setAccount(credsProvider.getCredentials().getAccount()).setHeaders(headers).setParameters(params)
                    .setOriginalRequest(getObjectRequest).build();
        }

        final ProgressListener listener = getObjectRequest.getProgressListener();
        HOSObject HOSObject = null;
        try {
            publishProgress(listener, ProgressEventType.TRANSFER_STARTED_EVENT);
            /**
             * 执行
             */
            HOSObject = doOperation(request, new GetObjectResponseParser(bucketName, key), bucketName, key, true);
            HOSObject.setBucketName(bucketName);
            HOSObject.setKey(key);
            /**
             * 处理流
             */
//            InputStream instream = HOSObject.getObjectContent();
//            ProgressInputStream progressInputStream = new ProgressInputStream(instream, listener) {
//                @Override
//                protected void onEOF() {
//                    publishProgress(getListener(), ProgressEventType.TRANSFER_COMPLETED_EVENT);
//                }
//            };
//            CRC64 crc = new CRC64();
//            CheckedInputStream checkedInputstream = new CheckedInputStream(progressInputStream, crc);
//            HOSObject.setObjectContent(checkedInputstream);
        } catch (RuntimeException e) {
            publishProgress(listener, ProgressEventType.TRANSFER_FAILED_EVENT);
            throw e;
        }

        return HOSObject;
    }

    /**
     * Populate a local file with the specified object.
     */
    public ObjectMetadata getObject(GetObjectRequest getObjectRequest, File file) throws ServiceException, ClientException {

        assertParameterNotNull(file, "file");

        HOSObject HOSObject = getObject(getObjectRequest);

        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = IOUtils.readNBytes(HOSObject.getObjectContent(), buffer, 0, buffer.length)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }

            if (isCrcCheckEnabled() && !hasRangeInRequest(getObjectRequest)) {
                Long clientCRC = IOUtils.getCRCValue(HOSObject.getObjectContent());
                HOSUtils.checkChecksum(clientCRC, HOSObject.getServerCRC(), HOSObject.getRequestId());
            }

            return HOSObject.getMetadata();
        } catch (IOException ex) {
            logException("Cannot read object content stream: ", ex);
            throw new ClientException(HOS_RESOURCE_MANAGER.getString("CannotReadContentStream"), ex);
        } finally {
            safeClose(outputStream);
            safeClose(HOSObject.getObjectContent());
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
        ensureBucketNameValid(bucketName);
        ensureObjectKeyValid(key);

        Map<String, String> params = new HashMap<String, String>();
        if (genericRequest.getVersionId() != null) {
            params.put(HOSRequestParameters.SUBRESOURCE_VRESION_ID,
                    genericRequest.getVersionId());
        }

        Map<String, String> headers = new HashMap<String, String>();

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.HEAD).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(params)
                .setOriginalRequest(genericRequest).setAccount(credsProvider.getCredentials().getAccount()).build();

        HOSObject HOSObject = doOperation(request, new GetObjectResponseParser(bucketName, key), bucketName, key);
        SimplifiedObjectMeta simplifiedObjectMeta = new SimplifiedObjectMeta();
        simplifiedObjectMeta.setETag(HOSObject.getMetadata().getETag());
        simplifiedObjectMeta.setSize(HOSObject.getMetadata().getContentLength());
        simplifiedObjectMeta.setVersionId(HOSObject.getMetadata().getVersionId());
        simplifiedObjectMeta.setRequestId(HOSObject.getRequestId());
        simplifiedObjectMeta.setResponse(HOSObject.getResponse());
        simplifiedObjectMeta.setLastModified(HOSObject.getMetadata().getLastModified());
        return simplifiedObjectMeta;
//        return doOperation(request, getSimplifiedObjectMetaResponseParser, bucketName, key);
    }

    /**
     * Get object matadata.
     */
    public ObjectMetadata getMetadata(GenericRequest genericRequest)
            throws ServiceException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        String key = genericRequest.getKey();

        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameValid(bucketName);
        ensureObjectKeyValid(key);

        Map<String, String> params = new HashMap<String, String>();
        if (genericRequest.getVersionId() != null) {
            params.put(HOSRequestParameters.SUBRESOURCE_VRESION_ID, genericRequest.getVersionId());
        }

        Map<String, String> headers = new HashMap<>();
        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.HEAD).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(params)
                .setOriginalRequest(genericRequest).setAccount(this.credsProvider.getCredentials().getAccount()).build();

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

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(copyObjectRequest))
                .setMethod(HttpMethod.PUT).setBucket(copyObjectRequest.getDestinationBucketName())
                .setAccount(credsProvider.getCredentials().getAccount()).setKey(copyObjectRequest.getDestinationKey())
                .setHeaders(headers).setOriginalRequest(copyObjectRequest).build();

        return doOperation(request, copyObjectResponseParser, copyObjectRequest.getDestinationBucketName(),
                copyObjectRequest.getDestinationKey(), true);
    }

    /**
     * Delete an object.
     */
    public VoidResult deleteObject(GenericRequest genericRequest) throws ServiceException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        String key = genericRequest.getKey();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);

        Map<String, String> headers = new HashMap<String, String>();

        Map<String, String> params = new LinkedHashMap<String, String>();
        if (!StringUtils.isNullOrEmpty(genericRequest.getVersionId())) {
            params.put(VERSION_ID, genericRequest.getVersionId());
        }

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint(genericRequest)).setMethod(HttpMethod.DELETE).setBucket(bucketName)
                .setKey(key).setHeaders(headers).setOriginalRequest(genericRequest)
                .setAccount(credsProvider.getCredentials().getAccount()).setParameters(params)
                .build();

        return doOperation(request, requestIdResponseParser, bucketName, key);
    }

    /**
     * Delete an object version.
     */
    public VoidResult deleteVersion(DeleteVersionRequest deleteVersionRequest) throws ServiceException, ClientException {

        assertParameterNotNull(deleteVersionRequest, "deleteVersionRequest");

        String bucketName = deleteVersionRequest.getBucketName();
        String key = deleteVersionRequest.getKey();
        String versionId = deleteVersionRequest.getVersionId();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);
        assertParameterNotNull(versionId, "versionId");

        Map<String, String> params = new HashMap<String, String>();
        params.put(HOSRequestParameters.SUBRESOURCE_VRESION_ID, versionId);

        Map<String, String> headers = new HashMap<String, String>();

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(deleteVersionRequest))
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(params)
                .setOriginalRequest(deleteVersionRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, key);
    }

    /**
     * Delete multiple objects.
     */
    public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest) {

        assertParameterNotNull(deleteObjectsRequest, "deleteObjectsRequest");

        String bucketName = deleteObjectsRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);


        DeleteObjectsResult deleteObjectsResult = new DeleteObjectsResult();
        List<String> list = new ArrayList<>();
        List<String> keys = deleteObjectsRequest.getKeys();
        keys.stream().forEach(item -> {
            GenericRequest genericRequest = new GenericRequest(bucketName, item);
            VoidResult voidResult = deleteObject(genericRequest);
            if (voidResult.getResponse().isSuccessful()) {
                list.add(item);
            }
        });
        deleteObjectsResult.setDeletedObjects(list);
        return deleteObjectsResult;
    }

    /**
     * Delete multiple versions.
     */
    public DeleteVersionsResult deleteVersions(DeleteVersionsRequest deleteVersionsRequest)
            throws ServiceException, ClientException {

        assertParameterNotNull(deleteVersionsRequest, "deleteObjectsRequest");

        String bucketName = deleteVersionsRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_DELETE, null);

        byte[] rawContent = deleteVersionsRequestMarshaller.marshall(deleteVersionsRequest);
        Map<String, String> headers = new HashMap<String, String>();
        addDeleteVersionsRequiredHeaders(headers, rawContent);


        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(deleteVersionsRequest))
                .setMethod(HttpMethod.POST).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(deleteVersionsRequest).build();

        return doOperation(request, deleteVersionsResponseParser, bucketName, null, true);
    }

    public VoidResult setObjectAcl(SetAclRequest setAclRequest) throws ServiceException, ClientException {

        assertParameterNotNull(setAclRequest, "setAclRequest");

        String bucketName = setAclRequest.getBucketName();
        String key = setAclRequest.getKey();
        AccessControlList cannedAcl = setAclRequest.getAccessControlList();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);
        assertParameterNotNull(cannedAcl, "cannedAcl");

        Map<String, String> headers = new HashMap<String, String>();
//        headers.put(HOSHeaders.HOS_OBJECT_ACL, cannedAcl.toString());


        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_ACL, null);
        if (setAclRequest.getVersionId() != null) {
            params.put(HOSRequestParameters.SUBRESOURCE_VRESION_ID, setAclRequest.getVersionId());
        }
        byte[] marshall = setAclRequestMarshaller.marshall(setAclRequest);
        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setAclRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setKey(key).setParameters(params).setHeaders(headers)
                .setInputStream(new ByteArrayInputStream(marshall)).setInputSize(marshall.length)
                .setOriginalRequest(setAclRequest).setAccount(credsProvider.getCredentials().getAccount()).build();

        return doOperation(request, requestIdResponseParser, bucketName, key);
    }

    public AccessControlList getObjectAcl(GenericRequest genericRequest) throws ServiceException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        String key = genericRequest.getKey();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_ACL, null);
        if (genericRequest.getVersionId() != null) {
            params.put(HOSRequestParameters.SUBRESOURCE_VRESION_ID, genericRequest.getVersionId());
        }

        Map<String, String> headers = new HashMap<String, String>();

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(params)
                .setOriginalRequest(genericRequest).setAccount(credsProvider.getCredentials().getAccount()).build();

        return doOperation(request, getAclResponseParser, bucketName, key, true);
    }

    public RestoreObjectResult restoreObject(GenericRequest genericRequest) throws ServiceException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        String key = genericRequest.getKey();
        String versionId = genericRequest.getVersionId();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);

        byte[] content = new byte[0];
        if (genericRequest instanceof RestoreObjectRequest) {
            RestoreObjectRequest restoreObjectRequest = (RestoreObjectRequest) genericRequest;
            if (restoreObjectRequest.getRestoreConfiguration() != null) {
                content = restoreObjectRequestMarshaller.marshall(restoreObjectRequest);
            }
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put(HOSRequestParameters.SUBRESOURCE_RESTORE, null);
        if (versionId != null) {
            params.put(HOSRequestParameters.SUBRESOURCE_VRESION_ID, versionId);
        }

        Map<String, String> headers = new HashMap<String, String>();

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.POST).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(params)
                .setInputStream(new ByteArrayInputStream(content)).setInputSize(content.length)
                .setOriginalRequest(genericRequest).setAccount(credsProvider.getCredentials().getAccount()).build();

        return doOperation(request, ResponseParsers.restoreObjectResponseParser, bucketName, key);
    }

    public VoidResult setObjectTagging(SetObjectTaggingRequest setObjectTaggingRequest) throws ServiceException, ClientException {
        assertParameterNotNull(setObjectTaggingRequest, "setBucketTaggingRequest");

        String bucketName = setObjectTaggingRequest.getBucketName();
        String key = setObjectTaggingRequest.getKey();
        String versionId = setObjectTaggingRequest.getVersionId();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_TAGGING, null);
        if (versionId != null) {
            params.put(HOSRequestParameters.SUBRESOURCE_VRESION_ID, versionId);
        }

        Map<String, String> headers = new HashMap<String, String>();
        byte[] marshall = setBucketTaggingRequestMarshaller.marshall(setObjectTaggingRequest);
        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setObjectTaggingRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(params)
                .setInputStream(new ByteArrayInputStream(marshall)).setInputSize(marshall.length)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setOriginalRequest(setObjectTaggingRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, key);
    }

    public TagSet getObjectTagging(GenericRequest genericRequest) throws ServiceException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        String key = genericRequest.getKey();
        String versionId = genericRequest.getVersionId();

        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameValid(bucketName);
        ensureObjectKeyValid(key);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_TAGGING, null);
        if (versionId != null) {
            params.put(HOSRequestParameters.SUBRESOURCE_VRESION_ID, versionId);
        }

        Map<String, String> headers = new HashMap<String, String>();

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(params)
                .setOriginalRequest(genericRequest).setAccount(credsProvider.getCredentials().getAccount()).build();

        return doOperation(request, getTaggingResponseParser, bucketName, key, true);
    }

    public VoidResult deleteObjectTagging(GenericRequest genericRequest) throws ServiceException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        String key = genericRequest.getKey();
        String versionId = genericRequest.getVersionId();

        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        assertParameterNotNull(key, "key");
        ensureObjectKeyValid(key);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_TAGGING, null);
        if (versionId != null) {
            params.put(HOSRequestParameters.SUBRESOURCE_VRESION_ID, versionId);
        }

        Map<String, String> headers = new HashMap<String, String>();

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.DELETE).setBucket(bucketName).setKey(key).setHeaders(headers).setParameters(params)
                .setOriginalRequest(genericRequest).setAccount(credsProvider.getCredentials().getAccount()).build();

        return doOperation(request, requestIdResponseParser, bucketName, key);
    }

    public boolean doesObjectExist(GenericRequest genericRequest) throws ServiceException, ClientException {
        try {
            this.getSimplifiedObjectMeta(genericRequest);
            return true;
        } catch (ServiceException e) {
            if (e.getErrorMessage().equals(HOSErrorCode.NO_SUCH_BUCKET)
                    || e.getErrorMessage().equals(HOSErrorCode.NO_SUCH_KEY)) {
                return false;
            }
            throw e;
        }
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
        ObjectMetadata metadata = originalRequest.getMetadata();
        if (metadata == null) {
            metadata = new ObjectMetadata();
        }

        /**
         * 校验必要信息
         */
        assertParameterNotNull(bucketName, "bucketName");
        assertParameterNotNull(key, "key");
        ensureBucketNameValid(bucketName);
        ensureObjectKeyValid(key);

        InputStream repeatableInputStream = null;
        if (originalRequest.getFile() != null) {
            File toUpload = originalRequest.getFile();
            if (!checkFile(toUpload)) {
                getLog().info("Illegal file path: " + toUpload.getPath());
                throw new ClientException("Illegal file path: " + toUpload.getPath());
            }

            metadata.setContentLength(toUpload.length());
//            if (metadata.getContentType() == null) {
//                metadata.setContentType(Mimetypes.getInstance().getMimetype(toUpload, key));
//            }

            try {
                repeatableInputStream = new RepeatableFileInputStream(toUpload);
            } catch (IOException ex) {
                logException("Cannot locate file to upload: ", ex);
                throw new ClientException("Cannot locate file to upload: ", ex);
            }
        } else {
            assertTrue(originalInputStream != null, "Please specify input stream or file to upload");

//            if (metadata.getContentType() == null) {
//                metadata.setContentType(Mimetypes.getInstance().getMimetype(key));
//            }

            try {
                metadata.setContentLength(originalInputStream.available());
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
        /**
         * 添加自定义源数据头
         */
        populateRequestMetadata(headers, metadata);

        Map<String, String> params = new LinkedHashMap<>();

        HOSRequestMessage httpRequest = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(originalRequest))
                .setMethod(WriteMode.getMappingMethod(mode)).setBucket(bucketName).setKey(key).setHeaders(headers)
                .setParameters(params).setInputStream(repeatableInputStream).setAccount(credsProvider.getCredentials().getAccount())
                .setInputSize(determineInputStreamLength(repeatableInputStream, metadata.getContentLength()))
                .setOriginalRequest(originalRequest).build();

        List<ResponseHandler> reponseHandlers = new ArrayList<>();
        reponseHandlers.add(new HOSCallbackErrorResponseHandler());

        final ProgressListener listener = originalRequest.getProgressListener();
        ResponseType result = null;
        try {
            publishProgress(listener, ProgressEventType.TRANSFER_STARTED_EVENT);
            result = doOperation(httpRequest, responseParser, bucketName, key, true, null, reponseHandlers);
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
        return getObjectRequest.getHeaders().get(HOSHeaders.RANGE) != null;
    }

    private static void populateCopyObjectHeaders(CopyObjectRequest copyObjectRequest, Map<String, String> headers) {

        String copySourceHeader = copyObjectRequest.getSourceBucketName() + "/"
                + copyObjectRequest.getSourceKey();

//            headers.put(HOSHeaders.COPY_OBJECT_SOURCE, copySourceHeader);
        String[] split = copySourceHeader.split("/");
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < split.length; i++) {
            try {
                stringBuffer.append(URLEncoder.encode(split[i], DEFAULT_ENCODING)).append("/");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        headers.put(HOSHeaders.COPY_OBJECT_SOURCE, stringBuffer.toString().substring(0, stringBuffer.length() - 1));

        addHeader(headers, HOSHeaders.HOS_SERVER_SIDE_ENCRYPTION, copyObjectRequest.getServerSideEncryption());
        addHeader(headers, HOSHeaders.HOS_SERVER_SIDE_ENCRYPTION_KEY_ID, copyObjectRequest.getServerSideEncryptionKeyID());

        if (copyObjectRequest.getClientSideEncryptionAlgorithm() != null) {
            addHeader(headers, HOSHeaders.HOS_CLIENT_SIDE_ENCRYPTION_ALGORITHM, copyObjectRequest.getClientSideEncryptionAlgorithm().getAlgorithm());
            addHeader(headers, HOSHeaders.HOS_CLIENT_SIDE_ENCRYPTION_KEY, copyObjectRequest.getClientSideEncryptionKey());
            addHeader(headers, HOSHeaders.HOS_CLIENT_SIDE_ENCRYPTION_KEY_MD5, copyObjectRequest.getClientSideEncryptionKeyMD5());
            addHeader(headers, HOSHeaders.HOS_COPY_CLIENT_SIDE_ENCRYPTION__ALGORITHM, copyObjectRequest.getCopyClientSideEncryptionAlgorithm());
            addHeader(headers, HOSHeaders.HOS_COPY_CLIENT_SIDE_ENCRYPTION__KEY, copyObjectRequest.getCopyClientSideEncryptionKey());
            addHeader(headers, HOSHeaders.HOS_COPY_CLIENT_SIDE_ENCRYPTION__KEY_MD5, copyObjectRequest.getCopyClientSideEncryptionKeyMD5());
        }

        ObjectMetadata newObjectMetadata = copyObjectRequest.getNewObjectMetadata();
        if (newObjectMetadata != null) {
            headers.put(HOSHeaders.COPY_OBJECT_METADATA_DIRECTIVE, CopyObjectRequest.MetadataDirective.COPY.toString());
            if (newObjectMetadata.getRawMetadata().get(HOSHeaders.HOS_TAGGING) != null) {
                headers.put(HOSHeaders.COPY_OBJECT_TAGGING_DIRECTIVE, CopyObjectRequest.MetadataDirective.REPLACE.toString());
            }
            populateRequestMetadata(headers, newObjectMetadata);
        }

        // The header of Content-Length should not be specified on copying an
        // object.
        removeHeader(headers, CONTENT_LENGTH);
    }

    private static void populateGetObjectRequestHeaders(GetObjectRequest getObjectRequest,
                                                        Map<String, String> headers) {

        if (getObjectRequest.getRange() != null) {
            addGetObjectRangeHeader(getObjectRequest.getRange(), headers);
        }

        if (getObjectRequest.getModifiedSinceConstraint() != null) {
            headers.put(HOSHeaders.GET_OBJECT_IF_MODIFIED_SINCE,
                    DateUtil.formatRfc822Date(getObjectRequest.getModifiedSinceConstraint()));
        }

        if (getObjectRequest.getUnmodifiedSinceConstraint() != null) {
            headers.put(HOSHeaders.GET_OBJECT_IF_UNMODIFIED_SINCE,
                    DateUtil.formatRfc822Date(getObjectRequest.getUnmodifiedSinceConstraint()));
        }

        if (getObjectRequest.getMatchingETagConstraints().size() > 0) {
            headers.put(HOSHeaders.GET_OBJECT_IF_MATCH, joinETags(getObjectRequest.getMatchingETagConstraints()));
        }

        if (getObjectRequest.getNonmatchingEtagConstraints().size() > 0) {
            headers.put(HOSHeaders.GET_OBJECT_IF_NONE_MATCH,
                    joinETags(getObjectRequest.getNonmatchingEtagConstraints()));
        }
    }

    private static void addDeleteObjectsRequiredHeaders(Map<String, String> headers, byte[] rawContent) {
        headers.put(CONTENT_LENGTH, String.valueOf(rawContent.length));

        byte[] md5 = BinaryUtil.calculateSha256(rawContent);
        String md5Base64 = BinaryUtil.toBase64String(md5);
        headers.put(HttpHeaders.CONTENT_MD5, md5Base64);
    }

    private static void addDeleteVersionsRequiredHeaders(Map<String, String> headers, byte[] rawContent) {
        addDeleteObjectsRequiredHeaders(headers, rawContent);
    }


    private static void addGetObjectRangeHeader(long[] range, Map<String, String> headers) {
        RangeSpec rangeSpec = RangeSpec.parse(range);
        headers.put(HOSHeaders.RANGE, rangeSpec.toString());
    }
}
