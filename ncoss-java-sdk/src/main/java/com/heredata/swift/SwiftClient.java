package com.heredata.swift;

import com.heredata.ClientConfiguration;
import com.heredata.comm.ServiceClient;
import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import com.heredata.model.VoidResult;
import com.heredata.request.DefaultServiceClient;
import com.heredata.request.TimeoutServiceClient;
import com.heredata.auth.CredentialsProvider;
import com.heredata.swift.model.*;
import com.heredata.swift.model.bucket.*;
import com.heredata.swift.operation.SwiftAccountOperation;
import com.heredata.swift.operation.SwiftBucketOperation;
import com.heredata.swift.operation.SwiftDownloadOperation;
import com.heredata.swift.operation.SwiftObjectOperation;
import com.heredata.swift.utils.SwiftUtils;
import com.heredata.utils.LogUtils;

import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The entry point class of Swift that implements the Swift interface.
 */
public class SwiftClient implements Swift {

    /* The default credentials provider */
    private CredentialsProvider credsProvider;

    /* The valid endpoint for accessing to Swift services */
    private URI endpoint;

    /* The default service client */
    private ServiceClient serviceClient;

    /* The miscellaneous Swift operations */
    private SwiftBucketOperation bucketOperation;
    private SwiftObjectOperation objectOperation;
    private SwiftDownloadOperation downloadOperation;
    private SwiftAccountOperation accountOperation;

    /**Gets the inner objectOperation, used for subclass to do implement opreation.*/
    public SwiftObjectOperation getObjectOperation() {
        return objectOperation;
    }

    /**Sets the inner downloadOperation.*/
    public void setDownloadOperation(SwiftDownloadOperation downloadOperation) {
        this.downloadOperation = downloadOperation;
    }

    /**
     * Uses the specified {@link CredentialsProvider}, client configuration and
     * Swift endpoint to create a new {@link SwiftClient} instance.
     *
     * @param endpoint
     *            Swift services Endpoint.
     * @param credsProvider
     *            Credentials provider.
     * @param config
     *            client configuration.
     */
    public SwiftClient(String endpoint, CredentialsProvider credsProvider, ClientConfiguration config) {
        this.credsProvider = credsProvider;
        config = config == null ? new ClientConfiguration() : config;
        if (config.isRequestTimeoutEnabled()) {
            this.serviceClient = new TimeoutServiceClient(config);
        } else {
            this.serviceClient = new DefaultServiceClient(config);
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Auth-Token", credsProvider.getCredentials().getToken());
        config.setDefaultHeaders(headers);

        initOperations();
        setEndpoint(endpoint);
    }

    /**
     * Gets Swift services Endpoint.
     *
     * @return Swift services Endpoint.
     */
    public synchronized URI getEndpoint() {
        return URI.create(endpoint.toString());
    }

    /**
     * Sets Swift services endpoint.
     *
     * @param endpoint
     *            Swift services endpoint.
     */
    public synchronized void setEndpoint(String endpoint) {
        URI uri = toURI(endpoint);
        this.endpoint = uri;

        SwiftUtils.ensureEndpointValid(uri.getHost());

        if (isIpOrLocalhost(uri)) {
            serviceClient.getClientConfiguration().setSLDEnabled(true);
        }

        this.bucketOperation.setEndpoint(uri);
        this.objectOperation.setEndpoint(uri);
        this.accountOperation.setEndpoint(uri);
    }

    /**
     * Checks if the uri is an IP or domain. If it's IP or local host, then it
     * will use secondary domain of Alibaba cloud. Otherwise, it will use domain
     * directly to access the HOS.
     *
     * @param uri
     *            URI。
     */
    private boolean isIpOrLocalhost(URI uri) {
        if (uri.getHost().equals("localhost")) {
            return true;
        }

        InetAddress ia;
        try {
            ia = InetAddress.getByName(uri.getHost());
        } catch (UnknownHostException e) {
            return false;
        }

        if (uri.getHost().equals(ia.getHostAddress())) {
            return true;
        }

        return false;
    }

    private URI toURI(String endpoint) throws IllegalArgumentException {
        return SwiftUtils.toEndpointURI(endpoint, this.serviceClient.getClientConfiguration().getProtocol().toString());
    }

    private void initOperations() {
        this.bucketOperation = new SwiftBucketOperation(this.serviceClient, this.credsProvider);
        this.objectOperation = new SwiftObjectOperation(this.serviceClient, this.credsProvider);
        this.downloadOperation = new SwiftDownloadOperation(objectOperation);
        this.accountOperation = new SwiftAccountOperation(this.serviceClient, this.credsProvider);
    }

    public CredentialsProvider getCredentialsProvider() {
        return this.credsProvider;
    }

    @Override
    public VoidResult createBucket(String bucketName) throws ServiceException, ClientException {
        return this.createBucket(new CreateBucketRequest(bucketName));
    }

    @Override
    public VoidResult createBucket(CreateBucketRequest createBucketRequest) throws ServiceException, ClientException {
        return bucketOperation.createBucket(createBucketRequest);
    }

    @Override
    public VoidResult deleteBucket(String bucketName) throws ServiceException, ClientException {
        return this.deleteBucket(new GenericRequest(bucketName));
    }

    @Override
    public VoidResult deleteBucket(String bucketName, Boolean isForceDelete) throws ServiceException, ClientException {
        if (isForceDelete) {
            // 分页删除
            while (true) {
                // 删除对象
                ObjectListing objectListing = listObjects(new ListObjectsRequest(bucketName, null, null, 100));
                if (objectListing.getObjectSummaries() == null || objectListing.getObjectSummaries().isEmpty()) {
                    break;
                }
                objectListing.getObjectSummaries().forEach(item -> {
                    this.deleteObject(bucketName, item.getKey());
                });
            }
        }
        // 删除桶
        return bucketOperation.deleteBucket(new GenericRequest(bucketName));
    }

    @Override
    public VoidResult deleteBucket(GenericRequest genericRequest) throws ServiceException, ClientException {
        return bucketOperation.deleteBucket(genericRequest);
    }

    @Override
    public List<Bucket> listBuckets() throws ServiceException, ClientException {
        return bucketOperation.listBuckets();
    }

    @Override
    public BukcetListResult listBuckets(BucketListRequest listBucketsRequest) throws ServiceException, ClientException {
        return bucketOperation.listBuckets(listBucketsRequest);
    }

    @Override
    public BukcetListResult listBuckets(String prefix, String marker, Integer maxKeys) throws ServiceException, ClientException {
        return bucketOperation.listBuckets(new BucketListRequest(maxKeys, prefix, marker));
    }

    @Override
    public VoidResult setBucketAcl(String bucketName, BucketAclRequest bucketAclRequest)
            throws ServiceException, ClientException {
        bucketAclRequest.setBucketName(bucketName);
        return this.setBucketAcl(bucketAclRequest);
    }

    @Override
    public VoidResult setBucketAcl(BucketAclRequest bucketAclRequest) throws ServiceException, ClientException {
        return bucketOperation.bucketAcl(bucketAclRequest);
    }

    @Override
    public ObjectListing listObjects(String bucketName) throws ServiceException, ClientException {
        return listObjects(new ListObjectsRequest(bucketName, null, null, null));
    }

    @Override
    public ObjectListing listObjects(String bucketName, String prefix, String startAfter, Integer maxKeys) throws ServiceException, ClientException {
        return listObjects(new ListObjectsRequest(bucketName, prefix, startAfter, maxKeys));
    }

    @Override
    public ObjectListing listObjects(ListObjectsRequest listObjectsRequest) throws ServiceException, ClientException {
        return bucketOperation.listObjects(listObjectsRequest);
    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, InputStream input)
            throws ServiceException, ClientException {
        return putObject(bucketName, key, input, null);
    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata)
            throws ServiceException, ClientException {
        return putObject(new PutObjectRequest(bucketName, key, input));
    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, File file, ObjectMetadata metadata)
            throws ServiceException, ClientException {
        return putObject(new PutObjectRequest(bucketName, key, file));
    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, File file) throws ServiceException, ClientException {
        return putObject(bucketName, key, file, null);
    }

    @Override
    public PutObjectResult putObject(PutObjectRequest putObjectRequest) throws ServiceException, ClientException {
        return objectOperation.putObject(putObjectRequest);
    }

    @Override
    public CopyObjectResult copyObject(String sourceBucketName, String sourceKey, String destinationBucketName,
                                       String destinationKey) throws ServiceException, ClientException {
        return copyObject(new CopyObjectRequest(sourceBucketName, sourceKey, destinationBucketName, destinationKey));
    }

    @Override
    public CopyObjectResult copyObject(CopyObjectRequest copyObjectRequest) throws ServiceException, ClientException {
        return objectOperation.copyObject(copyObjectRequest);
    }

    @Override
    public SwiftObject getObject(String bucketName, String key) throws ServiceException, ClientException {
        return this.getObject(new GetObjectRequest(bucketName, key));
    }


    @Override
    public SwiftObject getObject(GetObjectRequest getObjectRequest) throws ServiceException, ClientException {
        return objectOperation.getObject(getObjectRequest);
    }

    @Override
    public ObjectMetadata getObjectMeta(String bucketName, String key) throws ServiceException, ClientException {
        return this.getObjectMeta(new GenericRequest(bucketName, key));
    }

    @Override
    public ObjectMetadata getObjectMeta(GenericRequest genericRequest) throws ServiceException, ClientException {
        return objectOperation.getObjectMeta(genericRequest);
    }

    @Override
    public VoidResult deleteObject(String bucketName, String key) throws ServiceException, ClientException {
        return this.deleteObject(new GenericRequest(bucketName, key));
    }

    @Override
    public VoidResult deleteObject(GenericRequest genericRequest) throws ServiceException, ClientException {
        return objectOperation.deleteObject(genericRequest);
    }

    @Override
    public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest)
            throws ServiceException, ClientException {
        return objectOperation.deleteObjects(deleteObjectsRequest);
    }

    @Override
    public boolean doesObjectExist(String bucketName, String key) throws ServiceException, ClientException {
        return doesObjectExist(new GenericRequest(bucketName, key));
    }


    @Override
    public boolean doesObjectExist(GenericRequest genericRequest) throws ServiceException, ClientException {
        return objectOperation.doesObjectExist(genericRequest);
    }

    @Override
    public Bucket getBucket(String bucketName) throws ServiceException, ClientException {
        return this.getBucketInfo(new GenericRequest(bucketName));
    }

    @Override
    public Bucket getBucketInfo(GenericRequest genericRequest) throws ServiceException, ClientException {
        return this.bucketOperation.getBucketInfo(genericRequest);
    }

    @Override
    public DownloadFileResult downloadObject(DownloadFileRequest downloadFileRequest) throws Throwable {
        return downloadOperation.downloadObject(downloadFileRequest);
    }

    @Override
    public BucketQuotaResult getBucketQuota(String bucket) throws ServiceException, ClientException {
        return this.bucketOperation.getBucketQuota(bucket);
    }

    @Override
    public VoidResult setBucketQuota(SetBucketQuotaRequest setBucketQuotaRequest) throws ServiceException, ClientException {
        return this.bucketOperation.setBucketQuota(setBucketQuotaRequest);
    }

    @Override
    public AccountInfo getAccount() throws ServiceException, ClientException {
        return this.accountOperation.getAccountMeta();
    }

    @Override
    public VoidResult setObjectMeta(String bucketName, String key, ObjectMetadata objectMetadata) {
        return this.objectOperation.setObjectMeta(bucketName, key, objectMetadata);
    }

    @Override
    public VoidResult deleteBucketQuota(String bucketName, Boolean isRemoveByte, Boolean isRemoveCount) {
        return this.bucketOperation.deleteBucketQuota(bucketName, isRemoveByte, isRemoveCount);
    }

    @Override
    public VoidResult setBucketMeta(SetBucketMetaRequest setBucketMetaRequest) {
        return this.bucketOperation.setBucketMeta(setBucketMetaRequest);
    }

    @Override
    public VoidResult deleteBukcetAcl(String bucketName, Boolean removeRead, Boolean removeWrite) {
        return this.bucketOperation.deleteBukcetAcl(bucketName, removeRead, removeWrite);
    }

    @Override
    public VoidResult deleteBucketMeta(String bucketName, List<String> needRemoveMeta) {
        return this.bucketOperation.deleteBucketMeta(bucketName, needRemoveMeta);
    }

    @Override
    public void shutdown() {
        try {
            serviceClient.shutdown();
        } catch (Exception e) {
            LogUtils.logException("shutdown throw exception: ", e);
        }
    }
}

