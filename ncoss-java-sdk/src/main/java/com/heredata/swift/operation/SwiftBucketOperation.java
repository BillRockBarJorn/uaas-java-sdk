package com.heredata.swift.operation;

import com.heredata.comm.HttpMethod;
import com.heredata.comm.ServiceClient;
import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import com.heredata.model.VoidResult;
import com.heredata.auth.CredentialsProvider;
import com.heredata.swift.comm.SWIFTRequestMessage;
import com.heredata.swift.internal.RequestMessageBuilder;
import com.heredata.swift.model.*;
import com.heredata.swift.model.bucket.*;
import com.heredata.utils.HttpUtil;
import com.heredata.utils.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.heredata.comm.HttpConstants.DEFAULT_CHARSET_NAME;
import static com.heredata.hos.comm.HOSRequestParameters.*;
import static com.heredata.swift.parser.ResponseParsers.*;
import static com.heredata.swift.utils.SwiftUtils.ensureBucketNameCreationValid;
import static com.heredata.utils.CodingUtils.assertParameterNotNull;
import static com.heredata.utils.ResourceUtils.urlEncodeKey;


/**
 * Bucket operation.
 */
public class SwiftBucketOperation extends SwiftOperation {

    public SwiftBucketOperation(ServiceClient client, CredentialsProvider credsProvider) {
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

        // 处理头元素
        Map<String, String> head = new HashMap<>();
        /**
         * X-Container-Read头元素处理：
         * 设置允许读取访问的桶访问控制列表（ACL）。访问的范围是特定于桶的。ACL允许对桶中的对象执行GET或HEAD操作， 或者对桶本身执行GET或HEAD操作
         * X-Container-Write头元素处理：
         * 设置允许写入访问的桶访问控制列表（ACL）。访问的范围是特定于桶的。ACL授予执行桶内对象的PUT、POST和DELETE操作的能力。
         * ACL不授予对桶本身元数据的写入访问权限，
         */
        if (createBucketRequest.getBucketAclRequest() != null) {
            StringBuffer readStr = new StringBuffer();
            BucketAclRequest bucketAclRequest = createBucketRequest.getBucketAclRequest();
            if (bucketAclRequest.getAllUserReadObject()) {
                readStr.append(".r:*").append(",");
            }
            if (bucketAclRequest.getHeadOrGetBukcet()) {
                readStr.append(".rlistings").append(",");
            }
            if (bucketAclRequest.getTokenRead() != null && !bucketAclRequest.getTokenRead().isEmpty()) {
                bucketAclRequest.getTokenRead().forEach(item -> {
                    readStr.append(item.getAccountId() + ":" + item.getUserId()).append(",");
                });
            }

            if (',' == readStr.toString().charAt(readStr.length() - 1)) {
                head.put("X-Container-Read", readStr.toString().substring(0, readStr.length() - 1));
            }

            StringBuffer writeStr = new StringBuffer();
            if (bucketAclRequest.getTokenWrite() != null && !bucketAclRequest.getTokenWrite().isEmpty()) {
                bucketAclRequest.getTokenWrite().forEach(item -> {
                    writeStr.append(item.getAccountId() + ":" + item.getUserId()).append(",");
                });
            }

            if (',' == writeStr.toString().charAt(writeStr.length() - 1)) {
                head.put("X-Container-Write", writeStr.toString().substring(0, writeStr.length() - 1));
            }
        }

        /**
         * 桶配额设置
         */
        if (createBucketRequest.getQuotaByte() != null) {
            head.put("X-Container-Meta-Quota-Bytes", createBucketRequest.getQuotaByte() + "");
        }
        if (createBucketRequest.getObjCount() != null) {
            head.put("X-Container-Meta-Quota-Count", createBucketRequest.getObjCount() + "");
        }

        SWIFTRequestMessage request = new RequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint(createBucketRequest))
                .setMethod(HttpMethod.PUT)
                .setBucket(bucketName)
                .setHeaders(head)
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
        ensureBucketNameCreationValid(bucketName);

        SWIFTRequestMessage request = new RequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint(genericRequest))
                .setMethod(HttpMethod.DELETE)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setBucket(bucketName)
                .setOriginalRequest(genericRequest).build();
        VoidResult result = doOperation(request, requestIdResponseParser, bucketName, null);

        return result;
    }

    public VoidResult deleteBukcetAcl(String bucketName, Boolean removeRead, Boolean removeWrite) {
        if (StringUtils.isNullOrEmpty(bucketName)) {
            throw new ClientException("bucketName requires not null and not empty!");
        }

        Map<String, String> head = new HashMap<>();

        /**
         * 删除桶ACL判断
         */
        if (removeRead != null && removeRead) {
            head.put("X-Remove-Container-Read", "x");
        }
        if (removeWrite != null && removeWrite) {
            head.put("X-Remove-Container-Write", "x");
        }

        SWIFTRequestMessage request = new RequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint())
                .setMethod(HttpMethod.POST)
                .setBucket(bucketName)
                .setHeaders(head)
                .setAccount(credsProvider.getCredentials().getAccount())
                .build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * List all my buckets.
     */
    public List<Bucket> listBuckets() throws ServiceException, ClientException {
        BukcetListResult bucketList = listBuckets(new BucketListRequest(null, null, null));
        List<Bucket> buckets = bucketList.getBucketList();
        while (bucketList.getTruncated()) {
            bucketList = listBuckets(new BucketListRequest(null, bucketList.getStartAfter(), null));
            buckets.addAll(bucketList.getBucketList());
        }
        return buckets;
    }

    /**
     * List all my buckets.
     */
    public BukcetListResult listBuckets(BucketListRequest bucketListRequest) throws ServiceException, ClientException {
        assertParameterNotNull(bucketListRequest, "bucketListRequest");

        Map<String, String> params = new LinkedHashMap<String, String>();
        if (bucketListRequest.getPrefix() != null) {
            params.put(PREFIX, bucketListRequest.getPrefix());
        }
        if (bucketListRequest.getStartAfter() != null) {
            params.put(START_AFTER, bucketListRequest.getStartAfter());
        }
        if (bucketListRequest.getLimit() != null) {
            params.put(MAX_KEYS, Integer.toString(bucketListRequest.getLimit()));
        }

        SWIFTRequestMessage request = new RequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint(bucketListRequest))
                .setMethod(HttpMethod.GET)
                .setParameters(params)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setOriginalRequest(bucketListRequest).build();
        BukcetListResult bucketList = doOperation(request, listBucketResponseParser, null, null, true);
        bucketList.setPrefix(bucketListRequest.getPrefix());
        bucketList.setLimit(bucketListRequest.getLimit());
        bucketList.setStartAfter(bucketListRequest.getStartAfter());
        return bucketList;
    }

    /**
     * Set bucket's canned ACL.
     */
    public VoidResult bucketAcl(BucketAclRequest bucketAclRequest) throws ServiceException, ClientException {

        assertParameterNotNull(bucketAclRequest, "setBucketAclRequest");

        String bucketName = bucketAclRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameCreationValid(bucketName);

        // 处理头元素
        Map<String, String> head = new HashMap<>();
        /**
         * X-Container-Read头元素处理：
         * 设置允许读取访问的桶访问控制列表（ACL）。访问的范围是特定于桶的。ACL允许对桶中的对象执行GET或HEAD操作， 或者对桶本身执行GET或HEAD操作
         * X-Container-Write头元素处理：
         * 设置允许写入访问的桶访问控制列表（ACL）。访问的范围是特定于桶的。ACL授予执行桶内对象的PUT、POST和DELETE操作的能力。
         * ACL不授予对桶本身元数据的写入访问权限，
         */
        if (bucketAclRequest != null) {
            StringBuffer readStr = new StringBuffer();
            if (bucketAclRequest.getAllUserReadObject() != null && bucketAclRequest.getAllUserReadObject()) {
                readStr.append(".r:*").append(",");
            }
            if (bucketAclRequest.getHeadOrGetBukcet() != null && bucketAclRequest.getHeadOrGetBukcet()) {
                readStr.append(".rlistings").append(",");
            }
            if (bucketAclRequest.getTokenRead() != null && !bucketAclRequest.getTokenRead().isEmpty()) {
                bucketAclRequest.getTokenRead().forEach(item -> {
                    readStr.append(item.getAccountId() + ":" + item.getUserId()).append(",");
                });
            }

            if (',' == readStr.toString().charAt(readStr.length() - 1)) {
                head.put("X-Container-Read", readStr.toString().substring(0, readStr.length() - 1));
            }

            StringBuffer writeStr = new StringBuffer();
            if (bucketAclRequest.getTokenWrite() != null && !bucketAclRequest.getTokenWrite().isEmpty()) {
                bucketAclRequest.getTokenWrite().forEach(item -> {
                    writeStr.append(item.getAccountId() + ":" + item.getUserId()).append(",");
                });
            }

            if (!StringUtils.isNullOrEmpty(writeStr.toString()) && ',' == writeStr.toString().charAt(writeStr.length() - 1)) {
                head.put("X-Container-Write", writeStr.toString().substring(0, writeStr.length() - 1));
            }
        }

        SWIFTRequestMessage request = new RequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint(bucketAclRequest))
                .setMethod(HttpMethod.PUT)
                .setBucket(bucketName)
                .setHeaders(head)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setOriginalRequest(bucketAclRequest).build();

        return doOperation(request, requestIdResponseParser, bucketName, null);
    }

    /**
     * List objects under the specified bucket.
     */
    public ObjectListing listObjects(ListObjectsRequest listObjectsRequest) throws ServiceException, ClientException {

        assertParameterNotNull(listObjectsRequest, "listObjectsRequest");

        String bucketName = listObjectsRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameCreationValid(bucketName);

        Map<String, String> params = new LinkedHashMap<String, String>();
        populateListObjectsRequestParameters(listObjectsRequest, params);

        Map<String, String> headers = new HashMap<String, String>();

        SWIFTRequestMessage request = new RequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint(listObjectsRequest))
                .setMethod(HttpMethod.GET)
                .setBucket(bucketName)
                .setHeaders(headers)
                .setParameters(params)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setOriginalRequest(listObjectsRequest).build();
        ObjectListing objectListing = doOperation(request, new ListObjectsReponseParser(bucketName), bucketName, null, true);
        objectListing.setBucketName(bucketName);
        objectListing.setKeyCounts(objectListing.getObjectSummaries().size());
        objectListing.setPrefix(listObjectsRequest.getPrefix());
        objectListing.setMaxKeys(listObjectsRequest.getMaxKeys());
        return objectListing;
    }

    public Bucket getBucketInfo(GenericRequest genericRequest) throws ServiceException, ClientException {

        assertParameterNotNull(genericRequest, "genericRequest");

        String bucketName = genericRequest.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameCreationValid(bucketName);

        Map<String, String> params = new HashMap<String, String>();

        SWIFTRequestMessage request = new RequestMessageBuilder(getInnerClient())
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

    private static void populateListObjectsRequestParameters(ListObjectsRequest listObjectsRequest,
                                                             Map<String, String> params) {

        if (listObjectsRequest.getPrefix() != null) {
            params.put(PREFIX, listObjectsRequest.getPrefix());
        }

        if (listObjectsRequest.getStartAfter() != null) {
            params.put(MARKER, listObjectsRequest.getStartAfter());
        }

        if (listObjectsRequest.getMaxKeys() != null) {
            params.put(LIMIT, Integer.toString(listObjectsRequest.getMaxKeys()));
        }

        params.put("format", "json");
    }

    public BucketQuotaResult getBucketQuota(String bucket) {
        assertParameterNotNull(bucket, "bucketName");
        ensureBucketNameCreationValid(bucket);

        Map<String, String> map = new HashMap<>();
        map.put(QUOTA, null);
        SWIFTRequestMessage request = new RequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint()).setParameters(map).setBucket(bucket)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setMethod(HttpMethod.GET).build();

        return doOperation(request, getQuotaBucketParser, null, null, true);
    }

    public VoidResult setBucketQuota(SetBucketQuotaRequest quotaBucket) {

        assertParameterNotNull(quotaBucket, "quotaBucket");

        String bucketName = quotaBucket.getBucketName();
        assertParameterNotNull(bucketName, "bucketName");
        ensureBucketNameCreationValid(bucketName);

        // 添加桶配额在请求头中
        Map<String, String> headers = new HashMap<>();
        if (quotaBucket.getQuotaByte() != null) {
            headers.put("X-Container-Meta-Quota-Bytes", quotaBucket.getQuotaByte() + "");
        }
        if (quotaBucket.getObjCount() != null) {
            headers.put("X-Container-Meta-Quota-Count", quotaBucket.getObjCount() + "");
        }
        SWIFTRequestMessage request = new RequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint()).setBucket(quotaBucket.getBucketName())
                .setAccount(credsProvider.getCredentials().getAccount()).setHeaders(headers)
                .setMethod(HttpMethod.PUT).build();

        return doOperation(request, requestIdResponseParser, null, null, true);
    }

    public VoidResult deleteBucketQuota(String bucketName, Boolean isRemoveByte, Boolean isRemoveCount) {
        if (StringUtils.isNullOrEmpty(bucketName)) {
            throw new ClientException("bucketName requires not null and not empty!");
        }
        Map<String, String> headers = new HashMap<>();
        if (isRemoveByte != null && isRemoveByte) {
            headers.put("X-Remove-Container-Meta-Quota-Bytes", isRemoveByte + "");
        }
        if (isRemoveCount != null && isRemoveCount) {
            headers.put("X-Remove-Container-Meta-Quota-Count", isRemoveCount + "");
        }
        SWIFTRequestMessage request = new RequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint()).setBucket(bucketName).setHeaders(headers)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setMethod(HttpMethod.PUT).build();
        return doOperation(request, requestIdResponseParser, null, null, true);
    }

    public VoidResult setBucketMeta(SetBucketMetaRequest setBucketMetaRequest) {
        String bucketName = setBucketMetaRequest.getBucketName();
        if (StringUtils.isNullOrEmpty(bucketName)) {
            throw new ClientException("bucketName requires not null and not empty!");
        }
        Map<String, String> headers = new HashMap<>();

        BucketAclRequest bucketAclRequest = setBucketMetaRequest.getBucketAclRequest();
        /**
         * X-Container-Read头元素处理：
         * 设置允许读取访问的桶访问控制列表（ACL）。访问的范围是特定于桶的。ACL允许对桶中的对象执行GET或HEAD操作， 或者对桶本身执行GET或HEAD操作
         * X-Container-Write头元素处理：
         * 设置允许写入访问的桶访问控制列表（ACL）。访问的范围是特定于桶的。ACL授予执行桶内对象的PUT、POST和DELETE操作的能力。
         * ACL不授予对桶本身元数据的写入访问权限，
         */
        if (bucketAclRequest != null) {
            StringBuffer readStr = new StringBuffer();
            if (bucketAclRequest.getAllUserReadObject()) {
                readStr.append(".r:*").append(",");
            }
            if (bucketAclRequest.getHeadOrGetBukcet()) {
                readStr.append(".rlistings").append(",");
            }
            if (bucketAclRequest.getTokenRead() != null && !bucketAclRequest.getTokenRead().isEmpty()) {
                bucketAclRequest.getTokenRead().forEach(item -> {
                    readStr.append(item.getAccountId() + ":" + item.getUserId()).append(",");
                });
            }

            if (',' == readStr.toString().charAt(readStr.length() - 1)) {
                headers.put("X-Container-Read", readStr.toString().substring(0, readStr.length() - 1));
            }

            StringBuffer writeStr = new StringBuffer();
            if (bucketAclRequest.getTokenWrite() != null && !bucketAclRequest.getTokenWrite().isEmpty()) {
                bucketAclRequest.getTokenWrite().forEach(item -> {
                    writeStr.append(item.getAccountId() + ":" + item.getUserId()).append(",");
                });
            }

            if (',' == writeStr.toString().charAt(writeStr.length() - 1)) {
                headers.put("X-Container-Write", writeStr.toString().substring(0, writeStr.length() - 1));
            }
        }

        SetBucketQuotaRequest setBucketQuotaRequest = setBucketMetaRequest.getSetBucketQuotaRequest();
        /**
         * 桶配额设置
         */
        if (setBucketQuotaRequest != null && setBucketQuotaRequest.getQuotaByte() != null) {
            headers.put("X-Container-Meta-Quota-Bytes", setBucketQuotaRequest.getQuotaByte() + "");
        }
        if (setBucketQuotaRequest != null && setBucketQuotaRequest.getObjCount() != null) {
            headers.put("X-Container-Meta-Quota-Count", setBucketQuotaRequest.getObjCount() + "");
        }

        /**
         * 自定义元数据设置
         */
        Map<String, String> userMeta = setBucketMetaRequest.getUserMeta();
        if (userMeta != null && !userMeta.isEmpty()) {
            userMeta.forEach((k, v) -> {
                headers.put("X-Container-Meta-" + k.substring(0, 1).toUpperCase() + k.substring(1), v);
            });
        }

        /**
         * 需要删除的元数据
         */
        List<String> needRemoveMeta = setBucketMetaRequest.getNeedRemoveMeta();
        if (needRemoveMeta != null && !needRemoveMeta.isEmpty()) {
            needRemoveMeta.forEach(item -> {
                String s = item.substring(0, 1).toUpperCase() + item.substring(1);
                headers.put("X-Remove-Container-Meta-" + s, "x");
            });
        }
        SWIFTRequestMessage request = new RequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint()).setBucket(bucketName).setHeaders(headers)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setMethod(HttpMethod.POST).build();
        return doOperation(request, requestIdResponseParser, null, null, true);
    }

    public VoidResult deleteBucketMeta(String bucketName, List<String> needRemoveMeta) {
        if (StringUtils.isNullOrEmpty(bucketName)) {
            throw new ClientException("bucketName requires not null and not empty!");
        }

        Map<String, String> headers = new HashMap<>();

        /**
         * 需要删除的元数据
         */
        if (needRemoveMeta != null && !needRemoveMeta.isEmpty()) {
            needRemoveMeta.forEach(item -> {
                String s = item.substring(0, 1).toUpperCase() + item.substring(1);
                headers.put("X-Remove-Container-Meta-" + s, "x");
            });
        }
        SWIFTRequestMessage request = new RequestMessageBuilder(getInnerClient())
                .setEndpoint(getEndpoint()).setBucket(bucketName).setHeaders(headers)
                .setAccount(credsProvider.getCredentials().getAccount())
                .setMethod(HttpMethod.POST).build();
        return doOperation(request, requestIdResponseParser, null, null, true);
    }
}
