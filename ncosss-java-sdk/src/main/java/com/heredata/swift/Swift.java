package com.heredata.swift;

import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import com.heredata.model.VoidResult;
import com.heredata.swift.model.*;
import com.heredata.swift.model.bucket.*;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * <p>Title: Swift提供的所有的ncoss功能接口</p>
 * <p>Description: swift提供的所有的ncoss功能接口</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/9/2 10:27
 *
 */
public interface Swift {

    /**
     * @Title: 关闭连接
     * @Description: 关闭实例（释放所有资源) 调用shutdown其他功能后不可用。
     * @params []
     * @return void
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:31
     */
    public void shutdown();

    /**
     * @Title: 创建桶
     * @Description: 创建 {@link Bucket}实例。指定的存储桶名称必须为全局唯一，并遵循以下命名规则
     *              长度不能超过255字符，桶名称不能包含斜杠（/）字符
     * @params [bucketName]
     * @return com.heredata.swift.model.bucket.Bucket
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:33
     */
    public VoidResult createBucket(String bucketName) throws ServiceException, ClientException;

    /**
     * @Title: 创建桶并指定桶的属性
     * @Description: 使用指定的CreateBucketRequest创建 {@link Bucket}实例信息。同时可以设置桶的配额、ACL、元数据
     * @params [createBucketRequest]
     * @return com.heredata.swift.model.bucket.Bucket
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:36
     */
    public VoidResult createBucket(CreateBucketRequest createBucketRequest) throws ServiceException, ClientException;

    /**
     * @Title: 删除桶
     * @Description: 删除{@link Bucket}实例。不能使用非空桶删除。
     * @params [bucketName]
     * @return com.heredata.swift.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:37
     */
    public VoidResult deleteBucket(String bucketName) throws ServiceException, ClientException;

    /**
     * @Title: 删除桶
     * @Description: 删除{@link Bucket}实例。不能使用非空桶删除。
     * @params [bucketName]
     * @return com.heredata.swift.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:37
     */
    public VoidResult deleteBucket(String bucketName, Boolean isForceDelete) throws ServiceException, ClientException;

    /**
     * @Title: 删除通
     * @Description: 删除 {@link Bucket}实例。
     * @params [genericRequest]
     * @return com.heredata.swift.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:39
     */
    public VoidResult deleteBucket(GenericRequest genericRequest) throws ServiceException, ClientException;

    /**
     * @Title: 列举桶
     * @Description: 查询当前帐户的所有｛@link Bucket｝实例。
     * @params []
     * @return java.util.List<com.heredata.swift.model.bucket.Bucket>
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:39
     */
    public List<Bucket> listBuckets() throws ServiceException, ClientException;

    /**
     * @Title: 条件查询列举桶列表
     * @Description: 查询出所有满足条件的桶
     * @params [prefix, marker, maxKeys]
     * @return com.heredata.swift.model.bucket.BucketList
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:40
     */
    public BukcetListResult listBuckets(String prefix, String marker, Integer maxKeys) throws ServiceException, ClientException;

    /**
     * @Title: 条件查询列举桶列表
     * @Description: 查询出所有满足实例 {@link BukcetListResult}条件的桶
     * @params [listBucketsRequest]
     * @return com.heredata.swift.model.bucket.BucketList
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:41
     */
    public BukcetListResult listBuckets(BucketListRequest bucketListRequest) throws ServiceException, ClientException;

    /**
     * @Title: 设置桶ACL
     * @Description: 给桶 {@link Bucket} 设置ACL(访问控制权限)
     * @params [bucketName, setBucketAclRequest]
     * @return com.heredata.swift.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:42
     */
    public VoidResult setBucketAcl(String bucketName, BucketAclRequest bucketAclRequest) throws ServiceException, ClientException;


    /**
     * @Title: 设置桶ACL
     * @Description: 给桶 {@link Bucket} 设置ACL(访问控制权限)
     * @params [setBucketAclRequest]
     * @return com.heredata.swift.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:44
     */
    public VoidResult setBucketAcl(BucketAclRequest bucketAclRequest) throws ServiceException, ClientException;

    /**
     * @Title: 查询对象列表
     * @Description: 查询对象列表
     * @params [bucketName]
     * @return com.heredata.swift.model.ObjectListing
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:21
     */
    public ObjectListing listObjects(String bucketName) throws ServiceException, ClientException;

    /**
     * @Title: 多条件查询对象列表
     * @Description: 根据桶名和前缀条件查询对象列表
     * @params [bucketName, prefix]
     * @return com.heredata.swift.model.ObjectListing
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:21
     */
    public ObjectListing listObjects(String bucketName, String prefix, String startAfter, Integer maxKeys) throws ServiceException, ClientException;

    /**
     * @Title: 根据条件对象查询对象列表
     * @Description: 根据 {@link ListObjectsRequest}实例列举出对象列表
     * @params [listObjectsRequest]
     * @return com.heredata.swift.model.ObjectListing
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:39
     */
    public ObjectListing listObjects(ListObjectsRequest listObjectsRequest) throws ServiceException, ClientException;

    /**
     * @Title: 简单上传对象
     * @Description: 简单上传对象
     * @params [bucketName, key, input]
     * @return com.heredata.swift.model.PutObjectResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:45
     */
    public PutObjectResult putObject(String bucketName, String key, InputStream input)
            throws ServiceException, ClientException;

    /**
     * @Title: 简单上传对象并设置元数据
     * @Description: 简单上传对象并设置元数据
     * @params [bucketName, key, input,metadata]
     * @return com.heredata.swift.model.PutObjectResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:45
     */
    public PutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata)
            throws ServiceException, ClientException;

    /**
     * @Title: 简单上传对象并设置元数据
     * @Description: 简单上传对象并设置元数据
     * @params [bucketName, key, input]
     * @return com.heredata.swift.model.PutObjectResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:45
     */
    public PutObjectResult putObject(String bucketName, String key, File file, ObjectMetadata metadata)
            throws ServiceException, ClientException;

    /**
     * @Title: 简单上传对象
     * @Description: 简单上传对象
     * @params [bucketName, key, input]
     * @return com.heredata.swift.model.PutObjectResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:45
     */
    public PutObjectResult putObject(String bucketName, String key, File file) throws ServiceException, ClientException;

    /**
     * @Title: 根据上传对象实例上传对象
     * @Description: 根据 {@link PutObjectRequest}简单上传对象
     * @params [bucketName, key, input]
     * @return com.heredata.swift.model.PutObjectResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:45
     */
    public PutObjectResult putObject(PutObjectRequest putObjectRequest) throws ServiceException, ClientException;

    /**
     * @Title: 复制对象
     * @Description: 将hos的对象复制指定位置
     * @params [sourceBucketName, sourceKey, destinationBucketName, destinationKey]
     * @return com.heredata.swift.model.CopyObjectResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:50
     */
    public CopyObjectResult copyObject(String sourceBucketName, String sourceKey, String destinationBucketName,
                                       String destinationKey) throws ServiceException, ClientException;

    /**
     * @Title: 复制对象
     * @Description: 根据 {@link CopyObjectRequest}将hos的对象复制指定位置
     * @params [sourceBucketName, sourceKey, destinationBucketName, destinationKey]
     * @return com.heredata.swift.model.CopyObjectResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:50
     */
    public CopyObjectResult copyObject(CopyObjectRequest copyObjectRequest) throws ServiceException, ClientException;

    /**
     * @Title: 获取对象信息
     * @Description: 获取对象信息
     * @params [bucketName, key]
     * @return com.heredata.swift.model.SwiftObject
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:51
     */
    public SwiftObject getObject(String bucketName, String key) throws ServiceException, ClientException;

    /**
     * @Title: 获取对象信息
     * @Description: 根据 {@link GetObjectRequest}获取对象信息
     * @params [bucketName, key]
     * @return com.heredata.swift.model.SwiftObject
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:51
     */
    public SwiftObject getObject(GetObjectRequest getObjectRequest) throws ServiceException, ClientException;

    /**
     * @Title: 获取对象元数据
     * @Description: 获取对象元数据
     * @params [bucketName, key]
     * @return com.heredata.swift.model.SwiftObject
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:51
     */
    public ObjectMetadata getObjectMeta(String bucketName, String key) throws ServiceException, ClientException;

    /**
     * @Title: 获取对象元数据
     * @Description: 根据 {@link GenericRequest}实例获取对象元数据
     * @params [bucketName, key]
     * @return com.heredata.swift.model.SwiftObject
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:51
     */
    public ObjectMetadata getObjectMeta(GenericRequest genericRequest) throws ServiceException, ClientException;

    /**
     * @Title: 删除对象
     * @Description: 删除对象
     * @params [bucketName, key]
     * @return com.heredata.swift.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:58
     */
    public VoidResult deleteObject(String bucketName, String key) throws ServiceException, ClientException;

    /**
     * @Title: 删除对象
     * @Description: 根据 {@link GenericRequest}实例删除对象
     * @params [bucketName, key]
     * @return com.heredata.swift.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:58
     */
    public VoidResult deleteObject(GenericRequest genericRequest) throws ServiceException, ClientException;

    /**
     * @Title: 删除对象
     * @Description: 根据 {@link DeleteObjectsRequest}实例删除对象
     * @params [bucketName, key]
     * @return com.heredata.swift.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:58
     */
    public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest)
            throws ServiceException, ClientException;

    /**
     * @Title: 检测对象是否存在
     * @Description: 检测对象是否存在
     * @params [bucketName, key]
     * @return boolean
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 12:01
     */
    public boolean doesObjectExist(String bucketName, String key) throws ServiceException, ClientException;

    /**
     * @Title: 检测对象是否存在
     * @Description: 根据 {@link GenericRequest}实例检测对象是否存在
     * @params [bucketName, key]
     * @return boolean
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 12:01
     */
    public boolean doesObjectExist(GenericRequest genericRequest) throws ServiceException, ClientException;

    /**
     * @Title: 获取桶详情
     * @Description: 获取桶详情
     * @params [genericRequest]
     * @return com.heredata.swift.model.bucket.Bucket
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:58
     */
    public Bucket getBucket(String bucketName) throws ServiceException, ClientException;

    /**
     * @Title: 获取桶详情
     * @Description: 根据 {@link GenericRequest}实例获取桶详情
     * @params [genericRequest]
     * @return com.heredata.swift.model.bucket.Bucket
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:58
     */
    public Bucket getBucketInfo(GenericRequest genericRequest) throws ServiceException, ClientException;

    /**
     * @Title: 下载对象
     * @Description: 根据 {@link DownloadFileRequest} 实例下载对象
     * @params [downloadFileRequest]
     * @return com.heredata.swift.model.DownloadFileResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:47
     */
    public DownloadFileResult downloadObject(DownloadFileRequest downloadFileRequest) throws Throwable;

    /**
     * @Title: 获取桶配额
     * @Description: 获取桶配额
     * @params [bucket]
     * @return com.heredata.swift.model.BucketQuotaResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:43
     */
    BucketQuotaResult getBucketQuota(String bucket) throws ServiceException, ClientException;

    /**
     * @Title: 设置桶配额
     * @Description: 根据 {@link SetBucketQuotaRequest}设置桶配额
     * @params [setBucketQuotaRequest]
     * @return com.heredata.swift.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:43
     */
    VoidResult setBucketQuota(SetBucketQuotaRequest setBucketQuotaRequest) throws ServiceException, ClientException;

    /**
     * @Title: 获取账户元数据
     * @Description: 获取账户元数据：桶个数，对象个数，帐户存储在对象存储中的字节总数
     * @return com.heredata.swift.model.AccountInfoBukcetList
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:43
     */
    AccountInfo getAccount() throws ServiceException, ClientException;

    /**
     * @Title: 设置对象元数据
     * @Description: 设置对象元数据
     * @params [bucketName, key, objectMetadata]
     * @return com.heredata.swift.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:43
     */
    VoidResult setObjectMeta(String bucketName, String key, ObjectMetadata objectMetadata);

    /**
     * @Title: 删除桶配额
     * @Description: 删除桶配额
     * @params [bucketName, isRemoveCount]
     * @return com.heredata.swift.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:43
     */
    VoidResult deleteBucketQuota(String bucketName, Boolean isRemoveByte, Boolean isRemoveCount);

    /**
     * @Title: 设置桶元数据
     * @Description: 根据 {@link SetBucketMetaRequest}设置桶元数据
     * @params [setBucketMetaRequest]
     * @return com.heredata.swift.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:43
     */
    VoidResult setBucketMeta(SetBucketMetaRequest setBucketMetaRequest);

    /**
     * @Title: 删除桶指定的ACL权限
     * @Description: 删除桶指定ACL权限
     * @params [bucketName, removeRead, removeWrite]
     * @return com.heredata.swift.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:43
     */
    VoidResult deleteBukcetAcl(String bucketName, Boolean removeRead, Boolean removeWrite);

    /**
     * @Title: 删除桶元数据
     * @Description: 删除桶元数据
     * @params [bucketName, needRemoveMeta]
     * @return com.heredata.swift.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:43
     */
    VoidResult deleteBucketMeta(String bucketName, List<String> needRemoveMeta);
}
