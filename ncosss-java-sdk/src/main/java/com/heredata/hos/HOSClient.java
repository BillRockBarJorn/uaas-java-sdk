package com.heredata.hos;

import com.heredata.ClientConfiguration;
import com.heredata.comm.ServiceClient;
import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import com.heredata.hos.auth.CredentialsProvider;
import com.heredata.hos.model.*;
import com.heredata.hos.model.bucket.Bucket;
import com.heredata.hos.model.bucket.BucketList;
import com.heredata.hos.model.bucket.BucketQuotaResult;
import com.heredata.hos.model.bucket.BucketVersioningConfiguration;
import com.heredata.hos.operation.*;
import com.heredata.hos.utils.HOSUtils;
import com.heredata.model.VoidResult;
import com.heredata.request.DefaultServiceClient;
import com.heredata.request.TimeoutServiceClient;
import com.heredata.swift.model.DownloadFileRequest;
import com.heredata.utils.LogUtils;

import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: HOSClient</p>
 * <p>Description: hos服务客户端具体操作实现类 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:15
 */
public class HOSClient implements HOS {

    /* The default credentials provider */
    private CredentialsProvider credsProvider;

    /* The valid endpoint for accessing to HOS services */
    private URI endpoint;

    /* The default service client */
    private ServiceClient serviceClient;

    /* The miscellaneous HOS operations */
    private HOSBucketOperation bucketOperation;
    private HOSObjectOperation objectOperation;
    private HOSMultipartOperation multipartOperation;
    private HOSUploadOperation uploadOperation;
    private HOSDownloadOperation downloadOperation;
    private HOSAccountOperation accountOperation;

    /**Gets the inner multipartOperation, used for subclass to do implement opreation.*/
    public HOSMultipartOperation getMultipartOperation() {
        return multipartOperation;
    }

    /**Gets the inner objectOperation, used for subclass to do implement opreation.*/
    public HOSObjectOperation getObjectOperation() {
        return objectOperation;
    }

    /**Sets the inner downloadOperation.*/
    public void setDownloadOperation(HOSDownloadOperation downloadOperation) {
        this.downloadOperation = downloadOperation;
    }

    /**Sets the inner uploadOperation.*/
    public void setUploadOperation(HOSUploadOperation uploadOperation) {
        this.uploadOperation = uploadOperation;
    }

    /**
     * Uses the specified {@link CredentialsProvider}, client configuration and
     * HOS endpoint to create a new {@link HOSClient} instance.
     *
     * @param endpoint
     *            HOS services Endpoint.
     * @param credsProvider
     *            Credentials provider.
     * @param config
     *            client configuration.
     */
    public HOSClient(String endpoint, CredentialsProvider credsProvider, ClientConfiguration config) {
        this.credsProvider = credsProvider;
        config = config == null ? new ClientConfiguration() : config;
        if (config.isRequestTimeoutEnabled()) {
            this.serviceClient = new TimeoutServiceClient(config);
        } else {
            this.serviceClient = new DefaultServiceClient(config);
        }
        initOperations();
        setEndpoint(endpoint);
    }

    /**
     * Gets HOS services Endpoint.
     *
     * @return HOS services Endpoint.
     */
    public synchronized URI getEndpoint() {
        return URI.create(endpoint.toString());
    }

    /**
     * Sets HOS services endpoint.
     *
     * @param endpoint
     *            HOS services endpoint.
     */
    public synchronized void setEndpoint(String endpoint) {
        URI uri = toURI(endpoint);
        this.endpoint = uri;

        HOSUtils.ensureEndpointValid(uri.getHost());

        if (isIpOrLocalhost(uri)) {
            serviceClient.getClientConfiguration().setSLDEnabled(true);
        }

        this.bucketOperation.setEndpoint(uri);
        this.objectOperation.setEndpoint(uri);
        this.multipartOperation.setEndpoint(uri);
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
        return HOSUtils.toEndpointURI(endpoint, this.serviceClient.getClientConfiguration().getProtocol().toString());
    }

    private void initOperations() {
        this.bucketOperation = new HOSBucketOperation(this.serviceClient, this.credsProvider);
        this.objectOperation = new HOSObjectOperation(this.serviceClient, this.credsProvider);
        this.multipartOperation = new HOSMultipartOperation(this.serviceClient, this.credsProvider);
        this.uploadOperation = new HOSUploadOperation(this.multipartOperation);
        this.downloadOperation = new HOSDownloadOperation(objectOperation);
        this.accountOperation = new HOSAccountOperation(this.serviceClient, this.credsProvider);
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
    public VoidResult deleteBucket(GenericRequest genericRequest) throws ServiceException, ClientException {
        return bucketOperation.deleteBucket(genericRequest);
    }

    @Override
    public List<Bucket> listBuckets() throws ServiceException, ClientException {
        return bucketOperation.listBuckets();
    }

    @Override
    public BucketList listBuckets(ListBucketsRequest listBucketsRequest) throws ServiceException, ClientException {
        return bucketOperation.listBuckets(listBucketsRequest);
    }

    @Override
    public BucketList listBuckets(String prefix, String marker, Integer maxKeys) throws ServiceException, ClientException {
        return bucketOperation.listBuckets(new ListBucketsRequest(prefix, marker, maxKeys));
    }

    @Override
    public VoidResult setBucketAcl(String bucketName, SetAclRequest setAclRequest)
            throws ServiceException, ClientException {
        setAclRequest.setBucketName(bucketName);
        return this.setBucketAcl(setAclRequest);
    }

    @Override
    public VoidResult setDefaultConfigBucketAcl(String bucketName) throws ServiceException, ClientException {
        // ACL权限容器
        AccessControlList accessControlList = new AccessControlList();
        // 所有人包括匿名用户有可读权限
        Grantee grantee = GroupGrantee.AllUsers;
        Permission permission = Permission.READ;
        accessControlList.grantPermission(grantee, permission);
        Owner owner = new Owner(this.credsProvider.getCredentials().getAccountId());
        accessControlList.setOwner(owner);
        SetAclRequest setBucketAclRequest = new SetAclRequest(bucketName, accessControlList);
        return this.setBucketAcl(bucketName, setBucketAclRequest);
    }

    @Override
    public VoidResult setBucketAcl(SetAclRequest setAclRequest) throws ServiceException, ClientException {
        return bucketOperation.setBucketAcl(setAclRequest);
    }

    @Override
    public AccessControlList getBucketAcl(String bucketName) throws ServiceException, ClientException {
        return this.getBucketAcl(new GenericRequest(bucketName));
    }

    @Override
    public AccessControlList getBucketAcl(GenericRequest genericRequest) throws ServiceException, ClientException {
        return bucketOperation.getBucketAcl(genericRequest);
    }

    @Override
    public boolean doesBucketExist(String bucketName) throws ServiceException, ClientException {
        return this.doesBucketExist(new GenericRequest(bucketName));
    }

    @Override
    public boolean doesBucketExist(GenericRequest genericRequest) throws ServiceException, ClientException {
        return bucketOperation.doesBucketExists(genericRequest);
    }

    /**
     * Deprecated. Please use {@link HOSClient#doesBucketExist(String)} instead.
     */
    @Deprecated
    public boolean isBucketExist(String bucketName) throws ServiceException, ClientException {
        return this.doesBucketExist(bucketName);
    }

    @Override
    public ObjectListing listObjects(String bucketName) throws ServiceException, ClientException {
        return listObjects(new ListObjectsRequest(bucketName, null, null, null));
    }

    @Override
    public ObjectListing listObjects(String bucketName, String prefix) throws ServiceException, ClientException {
        return listObjects(new ListObjectsRequest(bucketName, prefix, null, null));
    }

    @Override
    public ObjectListing listObjects(ListObjectsRequest listObjectsRequest) throws ServiceException, ClientException {
        return bucketOperation.listObjects(listObjectsRequest);
    }

    @Override
    public VersionListing listVersions(String bucketName, String prefix) throws ServiceException, ClientException {
        return listVersions(new ListVersionsRequest(bucketName, prefix, null, null, null));
    }

    @Override
    public VersionListing listVersions(String bucketName, String prefix, String startAfter, String versionIdMarker, Integer maxKeys) throws ServiceException, ClientException {
        ListVersionsRequest request = new ListVersionsRequest()
                .withBucketName(bucketName)
                .withPrefix(prefix)
                .withStartAfter(startAfter)
                .withVersionIdMarker(versionIdMarker)
                .withMaxKeys(maxKeys);
        return listVersions(request);
    }

    @Override
    public VersionListing listVersions(ListVersionsRequest listVersionsRequest) throws ServiceException, ClientException {
        return bucketOperation.listVersions(listVersionsRequest);
    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, InputStream input)
            throws ServiceException, ClientException {
        return putObject(bucketName, key, input, null);
    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata)
            throws ServiceException, ClientException {
        return putObject(new PutObjectRequest(bucketName, key, input, metadata));
    }

    @Override
    public PutObjectResult putObject(String bucketName, String key, File file, ObjectMetadata metadata)
            throws ServiceException, ClientException {
        return putObject(new PutObjectRequest(bucketName, key, file, metadata));
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
        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(sourceBucketName, sourceKey, destinationBucketName, destinationKey);
        copyObjectRequest.setNewObjectMetadata(new ObjectMetadata());
        return copyObject(copyObjectRequest);
    }

    @Override
    public CopyObjectResult copyObject(CopyObjectRequest copyObjectRequest) throws ServiceException, ClientException {
        return objectOperation.copyObject(copyObjectRequest);
    }

    @Override
    public HOSObject getObject(String bucketName, String key) throws ServiceException, ClientException {
        return this.getObject(new GetObjectRequest(bucketName, key));
    }


    @Override
    public HOSObject getObject(GetObjectRequest getObjectRequest) throws ServiceException, ClientException {
        return objectOperation.getObject(getObjectRequest);
    }

    @Override
    public SimplifiedObjectMeta getSimplifiedObjectMeta(String bucketName, String key)
            throws ServiceException, ClientException {
        return this.getSimplifiedObjectMeta(new GenericRequest(bucketName, key));
    }

    @Override
    public SimplifiedObjectMeta getSimplifiedObjectMeta(GenericRequest genericRequest)
            throws ServiceException, ClientException {
        return this.objectOperation.getSimplifiedObjectMeta(genericRequest);
    }

    @Override
    public ObjectMetadata getObjectMetadata(String bucketName, String key) throws ServiceException, ClientException {
        return this.getObjectMetadata(new GenericRequest(bucketName, key));
    }

    @Override
    public ObjectMetadata getObjectMetadata(GenericRequest genericRequest) throws ServiceException, ClientException {
        return objectOperation.getMetadata(genericRequest);
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
    public VoidResult deleteVersion(String bucketName, String key, String versionId) throws ServiceException, ClientException {
        return deleteVersion(new DeleteVersionRequest(bucketName, key, versionId));
    }

    @Override
    public VoidResult deleteVersion(DeleteVersionRequest deleteVersionRequest) throws ServiceException, ClientException {
        return objectOperation.deleteVersion(deleteVersionRequest);
    }

    @Override
    public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest)
            throws ServiceException, ClientException {
        return objectOperation.deleteObjects(deleteObjectsRequest);
    }

    @Override
    public DeleteVersionsResult deleteVersions(DeleteVersionsRequest deleteVersionsRequest)
            throws ServiceException, ClientException {
        return objectOperation.deleteVersions(deleteVersionsRequest);
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
    public VoidResult setObjectAcl(String bucketName, String key, SetAclRequest setAclRequest)
            throws ServiceException, ClientException {
        setAclRequest.setBucketName(bucketName);
        setAclRequest.setKey(key);
        return this.setObjectAcl(setAclRequest);
    }

    @Override
    public VoidResult setObjectAcl(SetAclRequest setAclRequest) throws ServiceException, ClientException {
        setAclRequest.setOwner(new Owner(this.credsProvider.getCredentials().getAccountId()));
        return objectOperation.setObjectAcl(setAclRequest);
    }

    @Override
    public AccessControlList getObjectAcl(String bucketName, String key) throws ServiceException, ClientException {
        return this.getObjectAcl(new GenericRequest(bucketName, key));
    }

    @Override
    public AccessControlList getObjectAcl(GenericRequest genericRequest) throws ServiceException, ClientException {
        return objectOperation.getObjectAcl(genericRequest);
    }

    @Override
    public RestoreObjectResult restoreObject(String bucketName, String key) throws ServiceException, ClientException {
        return this.restoreObject(new GenericRequest(bucketName, key));
    }

    @Override
    public RestoreObjectResult restoreObject(GenericRequest genericRequest) throws ServiceException, ClientException {
        return objectOperation.restoreObject(genericRequest);
    }

    @Override
    public RestoreObjectResult restoreObject(String bucketName, String key, RestoreConfiguration restoreConfiguration)
            throws ServiceException, ClientException {
        return this.restoreObject(new RestoreObjectRequest(bucketName, key, restoreConfiguration));
    }

    @Override
    public RestoreObjectResult restoreObject(RestoreObjectRequest restoreObjectRequest)
            throws ServiceException, ClientException {
        return objectOperation.restoreObject(restoreObjectRequest);
    }

    @Override
    public VoidResult setObjectTagging(String bucketName, String key, Map<String, String> tags)
            throws ServiceException, ClientException {
        return this.setObjectTagging(new SetObjectTaggingRequest(bucketName, key, tags));
    }

    @Override
    public VoidResult setObjectTagging(String bucketName, String key, TagSet tagSet) throws ServiceException, ClientException {
        return this.setObjectTagging(new SetObjectTaggingRequest(bucketName, key, tagSet));
    }

    @Override
    public VoidResult setObjectTagging(SetObjectTaggingRequest setObjectTaggingRequest) throws ServiceException, ClientException {
        return objectOperation.setObjectTagging(setObjectTaggingRequest);
    }

    @Override
    public TagSet getObjectTagging(String bucketName, String key) throws ServiceException, ClientException {
        return this.getObjectTagging(new GenericRequest(bucketName, key));
    }

    @Override
    public TagSet getObjectTagging(GenericRequest genericRequest) throws ServiceException, ClientException {
        return objectOperation.getObjectTagging(genericRequest);
    }

    @Override
    public VoidResult deleteObjectTagging(String bucketName, String key) throws ServiceException, ClientException {
        return this.deleteObjectTagging(new GenericRequest(bucketName, key));
    }

    @Override
    public VoidResult deleteObjectTagging(GenericRequest genericRequest) throws ServiceException, ClientException {
        return objectOperation.deleteObjectTagging(genericRequest);
    }


    @Override
    public VoidResult abortMultipartUpload(AbortMultipartUploadRequest request) throws ServiceException, ClientException {
        return multipartOperation.abortMultipartUpload(request);
    }

    @Override
    public CompleteMultipartUploadResult completeMultipartUpload(CompleteMultipartUploadRequest request)
            throws ServiceException, ClientException {
        return multipartOperation.completeMultipartUpload(request);
    }

    @Override
    public InitiateMultipartUploadResult initiateMultipartUpload(InitiateMultipartUploadRequest request)
            throws ServiceException, ClientException {
        return multipartOperation.initiateMultipartUpload(request);
    }

    @Override
    public MultipartUploadListing listMultipartUploads(ListMultipartUploadsRequest request)
            throws ServiceException, ClientException {
        return multipartOperation.listMultipartUploads(request);
    }

    @Override
    public PartListing listParts(ListPartsRequest request) throws ServiceException, ClientException {
        return multipartOperation.listParts(request);
    }

    @Override
    public UploadPartResult uploadPart(UploadPartRequest request) throws ServiceException, ClientException {
        if (request.getPartSize() < 1024 * 100) {
            request.setPartSize(1024 * 1024 * 4);
        }
        return multipartOperation.uploadPart(request);
    }

    @Override
    public BucketVersioningConfiguration getBucketVersioning(String bucketName) throws ServiceException, ClientException {
        return getBucketVersioning(new GenericRequest(bucketName));
    }

    @Override
    public BucketVersioningConfiguration getBucketVersioning(GenericRequest genericRequest)
            throws ServiceException, ClientException {
        return bucketOperation.getBucketVersioning(genericRequest);
    }

    @Override
    public VoidResult setBucketVersioning(SetBucketVersioningRequest setBucketVersioningRequest)
            throws ServiceException, ClientException {
        return bucketOperation.setBucketVersioning(setBucketVersioningRequest);
    }


    @Override
    public VoidResult setBucketLifecycle(SetBucketLifecycleRequest setBucketLifecycleRequest)
            throws ServiceException, ClientException {
        return bucketOperation.setBucketLifecycle(setBucketLifecycleRequest);
    }

    @Override
    public List<LifecycleRule> getBucketLifecycle(String bucketName) throws ServiceException, ClientException {
        return this.getBucketLifecycle(new GenericRequest(bucketName));
    }

    @Override
    public List<LifecycleRule> getBucketLifecycle(GenericRequest genericRequest) throws ServiceException, ClientException {
        return bucketOperation.getBucketLifecycle(genericRequest);
    }

    @Override
    public VoidResult deleteBucketLifecycle(String bucketName) throws ServiceException, ClientException {
        return this.deleteBucketLifecycle(new GenericRequest(bucketName));
    }

    @Override
    public VoidResult deleteBucketLifecycle(GenericRequest genericRequest) throws ServiceException, ClientException {
        return bucketOperation.deleteBucketLifecycle(genericRequest);
    }

    @Override
    public VoidResult setBucketTagging(String bucketName, Map<String, String> tags) throws ServiceException, ClientException {
        return this.setBucketTagging(new SetBucketTaggingRequest(bucketName, tags));
    }

    @Override
    public VoidResult setBucketTagging(String bucketName, TagSet tagSet) throws ServiceException, ClientException {
        return this.setBucketTagging(new SetBucketTaggingRequest(bucketName, tagSet));
    }

    @Override
    public VoidResult setBucketTagging(SetBucketTaggingRequest setBucketTaggingRequest) throws ServiceException, ClientException {
        return this.bucketOperation.setBucketTagging(setBucketTaggingRequest);
    }

    @Override
    public TagSet getBucketTagging(String bucketName) throws ServiceException, ClientException {
        return this.getBucketTagging(new GenericRequest(bucketName));
    }

    @Override
    public TagSet getBucketTagging(GenericRequest genericRequest) throws ServiceException, ClientException {
        return this.bucketOperation.getBucketTagging(genericRequest);
    }

    @Override
    public VoidResult deleteBucketTagging(String bucketName) throws ServiceException, ClientException {
        return this.deleteBucketTagging(new GenericRequest(bucketName));
    }

    @Override
    public VoidResult deleteBucketTagging(GenericRequest genericRequest) throws ServiceException, ClientException {
        return this.bucketOperation.deleteBucketTagging(genericRequest);
    }

    @Override
    public Bucket getBucketInfo(String bucketName) throws ServiceException, ClientException {
        return this.getBucketInfo(new GenericRequest(bucketName));
    }

    @Override
    public Bucket getBucketInfo(GenericRequest genericRequest) throws ServiceException, ClientException {
        return this.bucketOperation.getBucketInfo(genericRequest);
    }

    @Override
    public VoidResult setBucketPolicy(String bucketName, String policyText) throws ServiceException, ClientException {
        return this.setBucketPolicy(new SetBucketPolicyRequest(bucketName, policyText));
    }

    @Override
    public VoidResult setBucketPolicy(SetBucketPolicyRequest setBucketPolicyRequest) throws ServiceException, ClientException {
        return this.bucketOperation.setBucketPolicy(setBucketPolicyRequest);
    }

    @Override
    public GetBucketPolicyResult getBucketPolicy(GenericRequest genericRequest) throws ServiceException, ClientException {
        return this.bucketOperation.getBucketPolicy(genericRequest);
    }

    @Override
    public GetBucketPolicyResult getBucketPolicy(String bucketName) throws ServiceException, ClientException {
        return this.getBucketPolicy(new GenericRequest(bucketName));
    }

    @Override
    public VoidResult deleteBucketPolicy(GenericRequest genericRequest) throws ServiceException, ClientException {
        return this.bucketOperation.deleteBucketPolicy(genericRequest);
    }

    @Override
    public VoidResult deleteBucketPolicy(String bucketName) throws ServiceException, ClientException {
        return this.deleteBucketPolicy(new GenericRequest(bucketName));
    }

    @Override
    public CompleteMultipartUploadResult uploadFile(UploadObjectRequest uploadObjectRequest) throws Throwable {
        return this.uploadOperation.uploadFile(uploadObjectRequest);
    }

    @Override
    public DownloadFileResult downloadObject(DownloadFileRequest downloadFileRequest) throws Throwable {
        return downloadOperation.downloadFile(downloadFileRequest);
    }

    @Override
    public InputStream getObjectInputstream(String bucket, String key) throws Throwable {
        return  downloadOperation.getObjectInputstream(bucket,key);
    }

    @Override
    public AccountInfo getAccountInfo() throws ServiceException, ClientException {
        return this.accountOperation.getAccountInfo();
    }

    @Override
    public VoidResult setAccountQuota(SetAccountQuotaRequest setAccountQuotaRequest) throws ServiceException, ClientException {
        return this.accountOperation.setAccountQuota(setAccountQuotaRequest);
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
    public AccountInfo getAccountQuota() throws ServiceException, ClientException {
        return this.accountOperation.getAccountQuota();
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

