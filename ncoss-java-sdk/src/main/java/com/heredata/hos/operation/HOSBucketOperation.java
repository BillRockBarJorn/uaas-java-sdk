package com.heredata.hos.operation;

import com.heredata.comm.HttpMethod;
import com.heredata.comm.ServiceClient;
import com.heredata.exception.ClientException;
import com.heredata.exception.ExceptionFactory;
import com.heredata.exception.ServiceException;
import com.heredata.hos.HOSErrorCode;
import com.heredata.auth.CredentialsProvider;
import com.heredata.hos.comm.HOSRequestMessage;
import com.heredata.hos.comm.HOSRequestParameters;
import com.heredata.hos.handler.HOSRequestMessageBuilder;
import com.heredata.hos.model.*;
import com.heredata.hos.model.bucket.Bucket;
import com.heredata.hos.model.bucket.BucketList;
import com.heredata.hos.model.bucket.BucketQuotaResult;
import com.heredata.hos.model.bucket.BucketVersioningConfiguration;
import com.heredata.model.VoidResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.heredata.HttpHeaders.CONTENT_TYPE;
import static com.heredata.comm.HttpConstants.APPLICATION_XML;
import static com.heredata.hos.comm.HOSRequestParameters.*;
import static com.heredata.hos.comm.HOSRequestParameters.LIST_TYPE;
import static com.heredata.hos.parser.RequestMarshallers.*;
import static com.heredata.hos.parser.ResponseParsers.*;
import static com.heredata.hos.utils.HOSUtils.ensureBucketNameCreationValid;
import static com.heredata.hos.utils.HOSUtils.ensureBucketNameValid;
import static com.heredata.utils.CodingUtils.assertParameterNotNull;
import static com.heredata.utils.ResourceUtils.urlEncode;
import static com.heredata.utils.ResourceUtils.urlEncodeKey;
import static com.heredata.utils.StringUtils.DEFAULT_ENCODING;
import static com.heredata.utils.StringUtils.stringToByteArray;

/**
 * <p>Title: HOSBucketOperation</p>
 * <p>Description: 桶操作类 </p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/27 16:11
 */
public class HOSBucketOperation extends HOSOperation {

    public HOSBucketOperation(ServiceClient client, CredentialsProvider credsProvider) {
        super(client, credsProvider);
    }

    /**
     * Create a bucket.
     */
    public VoidResult createBucket(CreateBucketRequest createBucketRequest) throws ServiceException, ClientException {

        assertParameterNotNull(createBucketRequest, "createBucketRequest");

        String bucketName = createBucketRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameCreationValid(bucketName);

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint(createBucketRequest))
                .setMethod(HttpMethod.PUT)
                .setBucket(bucketName)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setOriginalRequest(createBucketRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * Delete a bucket.
     */
    public VoidResult deleteBucket(GenericRequest genericRequest) throws ServiceException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.DELETE)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setBucket(bucketName)
                .setOriginalRequest(genericRequest).build();
        VoidResult result = doOperation(request, requestIdResponseParser, bucketName, null);

        return result;
    }

    /**
     * List all my buckets.
     */
    public List<Bucket> listBuckets() throws ServiceException, ClientException {
        BucketList bucketList = listBuckets(new ListBucketsRequest(null, null, null));
        List<Bucket> buckets = bucketList.getBuckets();
        while (bucketList.isTruncated()) {
            bucketList = listBuckets(new ListBucketsRequest(null, bucketList.getStartAfter(), null));
            buckets.addAll(bucketList.getBuckets());
        }
        return buckets;
    }

    /**
     * List all my buckets.
     */
    public BucketList listBuckets(ListBucketsRequest listBucketRequest) throws ServiceException, ClientException {

        assertParameterNotNull(listBucketRequest, "listBucketRequest");

        Map<String, String> params = new LinkedHashMap<String, String>();
        if (listBucketRequest.getPrefix() != null) {
            params.put(PREFIX, listBucketRequest.getPrefix());
        }
        if (listBucketRequest.getStartAfter() != null) {
            params.put(START_AFTER, listBucketRequest.getStartAfter());
        }
        if (listBucketRequest.getMaxKeys() != null) {
            params.put(MAX_KEYS, Integer.toString(listBucketRequest.getMaxKeys()));
        }

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint(listBucketRequest))
                .setMethod(HttpMethod.GET)
                .setParameters(params)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setOriginalRequest(listBucketRequest).build();
        BucketList bucketList = doOperation(request, listBucketResponseParser, null, null, true);
        bucketList.setPrefix(listBucketRequest.getPrefix());
        bucketList.setMaxKeys(listBucketRequest.getMaxKeys());
        bucketList.setStartAfter(listBucketRequest.getStartAfter());
        return bucketList;
    }

    /**
     * Set bucket's canned ACL.
     */
    public VoidResult setBucketAcl(SetAclRequest setAclRequest) throws ServiceException, ClientException {

        assertParameterNotNull(setAclRequest, "setBucketAclRequest");

        String bucketName = setAclRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_ACL, null);
        byte[] marshall = setAclRequestMarshaller.marshall(setAclRequest);
        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setAclRequest))
                .setMethod(HttpMethod.PUT)
                .setBucket(bucketName)
                .setParameters(params)
                .setInputStream(new ByteArrayInputStream(marshall)).setInputSize(marshall.length)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setOriginalRequest(setAclRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * Get bucket's ACL.
     */
    public AccessControlList getBucketAcl(GenericRequest genericRequest) throws ServiceException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_ACL, null);

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params).setAccount(credsProvider.getCredentials().getAccount())
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getAclResponseParser, bucketName, null, true);
    }

    /**
     * Determine whether a bucket exists or not.
     */
    public boolean doesBucketExists(GenericRequest genericRequest) throws ServiceException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        try {
            getBucketInfo(new GenericRequest(bucketName));
        } catch (ServiceException oe) {
            if (oe.getErrorMessage().contains(HOSErrorCode.NO_SUCH_BUCKET)) {
                return false;
            }
        }
        return true;
    }

    /**
     * List objects under the specified bucket.
     */
    public ObjectListing listObjects(ListObjectsRequest listObjectsRequest) throws ServiceException, ClientException {

        assertParameterNotNull(listObjectsRequest, "listObjectsRequest");

        String bucketName = listObjectsRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new LinkedHashMap<String, String>();
        populateListObjectsRequestParameters(listObjectsRequest, params);

        Map<String, String> headers = new HashMap<String, String>();

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint(listObjectsRequest))
                .setMethod(HttpMethod.GET)
                .setBucket(bucketName)
                .setHeaders(headers)
                .setParameters(params)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setOriginalRequest(listObjectsRequest).build();

        return doOperation(request, new ListObjectsReponseParser(bucketName), bucketName, null, true);
    }

    /**
     * List versions under the specified bucket.
     */
    public VersionListing listVersions(ListVersionsRequest listVersionsRequest) throws ServiceException, ClientException {

        assertParameterNotNull(listVersionsRequest, "listVersionsRequest");

        String bucketName = listVersionsRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new LinkedHashMap<String, String>();
        populateListVersionsRequestParameters(listVersionsRequest, params);

        Map<String, String> headers = new HashMap<String, String>();
//        headers.put(VERSIONS, null);

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint(listVersionsRequest)).setMethod(HttpMethod.GET)
                .setBucket(bucketName).setHeaders(headers).setParameters(params)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setOriginalRequest(listVersionsRequest).build();

        return doOperation(request, listVersionsReponseParser, bucketName, null, true);
    }

    /**
     * Set bucket lifecycle.
     */
    public VoidResult setBucketLifecycle(SetBucketLifecycleRequest setBucketLifecycleRequest)
            throws ServiceException, ClientException {

        assertParameterNotNull(setBucketLifecycleRequest, "setBucketLifecycleRequest");

        String bucketName = setBucketLifecycleRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LIFECYCLE, null);

        byte[] marshall = setBucketLifecycleRequestMarshaller.marshall(setBucketLifecycleRequest);
        if (marshall.length > 1024 * 1024 * 2) {
            throw new ServiceException("lifeRule length too long. ");
        }
        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setBucketLifecycleRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params).setAccount(credsProvider.getCredentials().getAccount())
                .setInputStream(new ByteArrayInputStream(marshall)).setInputSize(marshall.length)
                .setOriginalRequest(setBucketLifecycleRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * Get bucket lifecycle.
     */
    public List<LifecycleRule> getBucketLifecycle(GenericRequest genericRequest) throws ServiceException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LIFECYCLE, null);

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET)
                .setBucket(bucketName)
                .setParameters(params)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketLifecycleResponseParser, bucketName, null, true);
    }

    /**
     * Delete bucket lifecycle.
     */
    public VoidResult deleteBucketLifecycle(GenericRequest genericRequest) throws ServiceException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_LIFECYCLE, null);

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.DELETE)
                .setBucket(bucketName)
                .setParameters(params)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * Set bucket tagging.
     */
    public VoidResult setBucketTagging(SetBucketTaggingRequest setBucketTaggingRequest) throws ServiceException, ClientException {

        assertParameterNotNull(setBucketTaggingRequest, "setBucketTaggingRequest");

        String bucketName = setBucketTaggingRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_TAGGING, null);
        byte[] marshall = setBucketTaggingRequestMarshaller.marshall(setBucketTaggingRequest);
        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setBucketTaggingRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setInputStream(new ByteArrayInputStream(marshall)).setInputSize(marshall.length)
                .setOriginalRequest(setBucketTaggingRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * Get bucket tagging.
     */
    public TagSet getBucketTagging(GenericRequest genericRequest) throws ServiceException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_TAGGING, null);

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getTaggingResponseParser, bucketName, null, true);
    }

    /**
     * Delete bucket tagging.
     */
    public VoidResult deleteBucketTagging(GenericRequest genericRequest) throws ServiceException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_TAGGING, null);

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.DELETE).setBucket(bucketName)
                .setParameters(params)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * Get bucket versioning.
     */
    public BucketVersioningConfiguration getBucketVersioning(GenericRequest genericRequest)
            throws ServiceException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(HOSRequestParameters.SUBRESOURCE_VRESIONING, null);

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET).setBucket(bucketName).setParameters(params)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, getBucketVersioningResponseParser, bucketName, null, true);
    }

    /**
     * Set bucket versioning.
     */
    public VoidResult setBucketVersioning(SetBucketVersioningRequest setBucketVersioningRequest)
            throws ServiceException, ClientException {
        assertParameterNotNull(setBucketVersioningRequest, "setBucketVersioningRequest");
        assertParameterNotNull(setBucketVersioningRequest.getVersioningConfiguration(), "versioningConfiguration");

        String bucketName = setBucketVersioningRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(HOSRequestParameters.SUBRESOURCE_VRESIONING, null);

        byte[] rawContent = setBucketVersioningRequestMarshaller.marshall(setBucketVersioningRequest);
        Map<String, String> headers = new HashMap<String, String>();
//        addRequestRequiredHeaders(headers, rawContent);

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient()).setEndpoint(getEndpoint(setBucketVersioningRequest))
                .setMethod(HttpMethod.PUT).setBucket(bucketName).setParameters(params).setHeaders(headers)
                .setInputSize(rawContent.length).setInputStream(new ByteArrayInputStream(rawContent))
                .setAccount(credsProvider.getCredentials().getAccount())
                .setOriginalRequest(setBucketVersioningRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public Bucket getBucketInfo(GenericRequest genericRequest) throws ServiceException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
//        params.put(RequestParameters.SUBRESOURCE_BUCKET_INFO, null);

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.HEAD)
                .setBucket(bucketName)
                .setParameters(params)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setOriginalRequest(genericRequest).build();
        Bucket bucket = doOperation(request, getBucketInfoResponseParser, bucketName, null, true);
        bucket.setBucketName(bucketName);
        return bucket;
    }

    public VoidResult setBucketPolicy(SetBucketPolicyRequest setBucketPolicyRequest) throws ServiceException, ClientException {

        assertParameterNotNull(setBucketPolicyRequest, "setBucketPolicyRequest");

        String bucketName = setBucketPolicyRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_POLICY, null);

        byte[] rawContent = stringToByteArray(setBucketPolicyRequest.getPolicyText());

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint(setBucketPolicyRequest))
                .setMethod(HttpMethod.PUT)
                .setBucket(bucketName)
                .setParameters(params)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setInputSize(rawContent.length)
                .setInputStream(new ByteArrayInputStream(rawContent))
                .setOriginalRequest(setBucketPolicyRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    public GetBucketPolicyResult getBucketPolicy(GenericRequest genericRequest) throws ServiceException, ClientException {
        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);
        Map<String, String> params = new HashMap<>();
        params.put(SUBRESOURCE_POLICY, null);
        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.GET)
                .setBucket(bucketName)
                .setParameters(params)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setOriginalRequest(genericRequest).build();
        return doOperation(request, getBucketPolicyResponseParser, bucketName, null, true);
    }

    public VoidResult deleteBucketPolicy(GenericRequest genericRequest) throws ServiceException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SUBRESOURCE_POLICY, null);

        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.DELETE)
                .setBucket(bucketName)
                .setParameters(params)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setOriginalRequest(genericRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    private static void populateListObjectsRequestParameters(ListObjectsRequest listObjectsRequest,
                                                             Map<String, String> params) {

        if (listObjectsRequest.getPrefix() != null) {
            params.put(PREFIX, urlEncodeKey(listObjectsRequest.getPrefix()));
        }

        if (listObjectsRequest.getStartAfter() != null) {
            params.put(START_AFTER, urlEncodeKey(listObjectsRequest.getStartAfter()));
        }
        if (listObjectsRequest.getMaxKeys() != null) {
            params.put(MAX_KEYS, Integer.toString(listObjectsRequest.getMaxKeys()));
        }

        if (listObjectsRequest.isVersion()) {
            params.put(VERSIONS, listObjectsRequest.getVersionId());
        }

        params.put(LIST_TYPE, "2");
    }

    private static void populateListVersionsRequestParameters(ListVersionsRequest listVersionsRequest,
                                                              Map<String, String> params) {

        params.put(SUBRESOURCE_VRESIONS, null);

        if (listVersionsRequest.getPrefix() != null) {
            params.put(PREFIX, urlEncodeKey(listVersionsRequest.getPrefix()));
        }

        if (listVersionsRequest.getStartAfter() != null) {
            params.put(KEY_MARKER, urlEncodeKey(listVersionsRequest.getStartAfter()));
//            params.put(LIST_TYPE, "2");
        }

        if (listVersionsRequest.getMaxKeys() != null) {
            params.put(MAX_KEYS, Integer.toString(listVersionsRequest.getMaxKeys()));
        }

        if (listVersionsRequest.getVersionIdMarker() != null) {
            params.put(VERSION_ID_MARKER, listVersionsRequest.getVersionIdMarker());
        }
    }

    public BucketQuotaResult getBucketQuota(String bucket) {
        assertParameterNotNull(bucket, "bucketName");
        ensureBucketNameValid(bucket);

        Map<String, String> map = new HashMap<>();
        map.put(QUOTA, null);
        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint()).setParameters(map).setBucket(bucket)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setMethod(HttpMethod.GET).build();

        return doOperation(request, getQuotaBucketParser, bucket, null, true);
    }

    public VoidResult setBucketQuota(SetBucketQuotaRequest quotaBucket) {

        assertParameterNotNull(quotaBucket, "quotaBucket");

        String bucketName = quotaBucket.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameValid(bucketName);

        Map<String, String> map = new HashMap<>();
        map.put(QUOTA, null);

        Map<String, String> map1 = new HashMap<>();
        map1.put(CONTENT_TYPE, APPLICATION_XML);

        byte[] marshall = setBucketQuotaRequestMarshaller.marshall(quotaBucket);
        HOSRequestMessage request = new HOSRequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint()).setParameters(map).setBucket(quotaBucket.getBucketName())
                .setAccount(credsProvider.getCredentials().getAccount()).setHeaders(map1)
                .setInputStream(new ByteArrayInputStream(marshall)).setInputSize(marshall.length)
                .setMethod(HttpMethod.PUT).build();

        return doOperation(request, requestIdResponseParser, quotaBucket.getBucketName(), null, true);
    }
}
