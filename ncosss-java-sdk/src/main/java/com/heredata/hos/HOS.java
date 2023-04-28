package com.heredata.hos;


import com.heredata.exception.ClientException;
import com.heredata.exception.ServiceException;
import com.heredata.hos.model.*;
import com.heredata.hos.model.bucket.Bucket;
import com.heredata.hos.model.bucket.BucketList;
import com.heredata.hos.model.bucket.BucketQuotaResult;
import com.heredata.hos.model.bucket.BucketVersioningConfiguration;
import com.heredata.model.VoidResult;
import com.heredata.swift.model.DownloadFileRequest;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: HOS提供的所有的nchos功能接口</p>
 * <p>Description: HOS提供的所有的nchos功能接口</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/9/2 10:27
 */
public interface HOS {

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
     * @return com.heredata.hos.model.bucket.Bucket
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:33
     */
    public VoidResult createBucket(String bucketName) throws ServiceException, ClientException;

    /**
     * @Title: 创建桶
     * @Description: 使用指定的CreateBucketRequest创建 {@link Bucket}实例信息
     * @params [createBucketRequest]
     * @return com.heredata.hos.model.bucket.Bucket
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:36
     */
    public VoidResult createBucket(CreateBucketRequest createBucketRequest) throws ServiceException, ClientException;

    /**
     * @Title: 删除桶
     * @Description: 删除{@link Bucket}实例。不能使用非空桶删除。
     * @params [bucketName]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:37
     */
    public VoidResult deleteBucket(String bucketName) throws ServiceException, ClientException;

    /**
     * @Title: 删除通
     * @Description: 删除 {@link Bucket}实例。
     * @params [genericRequest]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:39
     */
    public VoidResult deleteBucket(GenericRequest genericRequest) throws ServiceException, ClientException;

    /**
     * @Title: 列举桶
     * @Description: 查询当前帐户的所有｛@link Bucket｝实例。
     * @params []
     * @return java.util.List<com.heredata.hos.model.bucket.Bucket>
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:39
     */
    public List<Bucket> listBuckets() throws ServiceException, ClientException;

    /**
     * @Title: 列举桶列表
     * @Description: 查询出所有满足条件的桶
     * @params [prefix, marker, maxKeys]
     * @return com.heredata.hos.model.bucket.BucketList
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:40
     */
    public BucketList listBuckets(String prefix, String marker, Integer maxKeys) throws ServiceException, ClientException;

    /**
     * @Title: 列举桶
     * @Description: 查询出所有满足实例 {@link ListBucketsRequest}条件的桶
     * @params [listBucketsRequest]
     * @return com.heredata.hos.model.bucket.BucketList
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:41
     */
    public BucketList listBuckets(ListBucketsRequest listBucketsRequest) throws ServiceException, ClientException;


    /**
     * @Title: 设置桶ACL
     * @Description: 给桶 {@link Bucket} 设置ACL(访问控制权限)
     * @params [bucketName, setBucketAclRequest]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:42
     */
    public VoidResult setBucketAcl(String bucketName, SetAclRequest setBucketAclRequest) throws ServiceException, ClientException;


    /**
     * @Title: 设置桶默认ACL    匿名访问（读） 的 默认配置
     * @Description: 给桶 {@link Bucket} 设置桶默认ACL
     * @params [bucketName]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:42
     */
    public VoidResult setDefaultConfigBucketAcl(String bucketName) throws ServiceException, ClientException;

    /**
     * @Title: 设置桶ACL
     * @Description: 给桶 {@link Bucket} 设置ACL(访问控制权限)
     * @params [setBucketAclRequest]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:44
     */
    public VoidResult setBucketAcl(SetAclRequest setBucketAclRequest) throws ServiceException, ClientException;

    /**
     * @Title: 查询桶的ACL
     * @Description: 查询指定桶的ACL
     * @params [bucketName]
     * @return com.heredata.hos.model.AccessControlList
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:48
     */
    public AccessControlList getBucketAcl(String bucketName) throws ServiceException, ClientException;

    /**
     * @Title: 查询指定桶的ACL
     * @Description: 根据 {@link GenericRequest}实例查询指定桶的ACL
     * @params [genericRequest]
     * @return com.heredata.hos.model.AccessControlList
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 10:56
     */
    public AccessControlList getBucketAcl(GenericRequest genericRequest) throws ServiceException, ClientException;

    /**
     * @Title: 设置桶标签
     * @Description: 设置桶标签。标签信息存放在tags里面
     * @params [bucketName, tags]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:13
     */
    public VoidResult setBucketTagging(String bucketName, Map<String, String> tags) throws ServiceException, ClientException;

    /**
     * @Title: 设置桶标签
     * @Description: 根据 {@link TagSet}实例设置桶标签
     * @params [bucketName, tagSet]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:14
     */
    public VoidResult setBucketTagging(String bucketName, TagSet tagSet) throws ServiceException, ClientException;

    /**
     * @Title: 设置桶标签
     * @Description: 根据 {@link SetBucketTaggingRequest}实例设置桶标签
     * @params [setBucketTaggingRequest]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:14
     */
    public VoidResult setBucketTagging(SetBucketTaggingRequest setBucketTaggingRequest) throws ServiceException, ClientException;

    /**
     * @Title: 查询桶标签
     * @Description: 查询桶标签
     * @params [bucketName]
     * @return com.heredata.hos.model.TagSet
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:15
     */
    public TagSet getBucketTagging(String bucketName) throws ServiceException, ClientException;

    /**
     * @Title: 查询桶标签
     * @Description: 根据 {@link GenericRequest}查询桶标签
     * @params [bucketName]
     * @return com.heredata.hos.model.TagSet
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:15
     */
    public TagSet getBucketTagging(GenericRequest genericRequest) throws ServiceException, ClientException;

    /**
     * @Title: 删除桶标签
     * @Description: 删除桶标签
     * @params [bucketName]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:15
     */
    public VoidResult deleteBucketTagging(String bucketName) throws ServiceException, ClientException;

    /**
     * @Title: 删除桶标签
     * @Description: 根据 {@link GenericRequest}实例删除桶标签
     * @params [genericRequest]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:16
     */
    public VoidResult deleteBucketTagging(GenericRequest genericRequest) throws ServiceException, ClientException;

    /**
     * @Title: 获取桶的版本信息
     * @Description:
     *      版本状态：
     *      {@link BucketVersioningConfiguration#ENABLED}
     *      {@link BucketVersioningConfiguration#SUSPENDED}
     * @params [bucketName]
     * @return com.heredata.hos.model.bucket.BucketVersioningConfiguration
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:17
     */
    public BucketVersioningConfiguration getBucketVersioning(String bucketName) throws ServiceException, ClientException;

    /**
     * @Title: 获取桶的版本信息
     * @Description:
     *      版本状态：
     *      {@link BucketVersioningConfiguration#ENABLED}
     *      {@link BucketVersioningConfiguration#SUSPENDED}
     * @params [bucketName]
     * @return com.heredata.hos.model.bucket.BucketVersioningConfiguration
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:17
     */
    public BucketVersioningConfiguration getBucketVersioning(GenericRequest genericRequest)
            throws ServiceException, ClientException;

    /**
     * @Title: 设置桶的版本状态
     * @Description: 根据 {@link SetBucketVersioningRequest}实例设置桶的版本状态
     * @params [setBucketVersioningRequest]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:18
     */
    public VoidResult setBucketVersioning(SetBucketVersioningRequest setBucketVersioningRequest)
            throws ServiceException, ClientException;

    /**
     * @Title: 检查桶是否存在
     * @Description: 检查桶是否存在
     * @params [bucketName]
     * @return boolean
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:20
     */
    public boolean doesBucketExist(String bucketName) throws ServiceException, ClientException;

    /**
     * @Title: 检查桶是否存在
     * @Description: 根据 {@link GenericRequest}检查桶是否存在
     * @params [bucketName]
     * @return boolean
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:20
     */
    public boolean doesBucketExist(GenericRequest genericRequest) throws ServiceException, ClientException;

    /**
     * @Title: 查询对象列表
     * @Description: 查询对象列表
     * @params [bucketName]
     * @return com.heredata.hos.model.ObjectListing
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:21
     */
    public ObjectListing listObjects(String bucketName) throws ServiceException, ClientException;

    /**
     * @Title: 查询对象列表
     * @Description: 根据桶名和前缀条件查询对象列表
     * @params [bucketName, prefix]
     * @return com.heredata.hos.model.ObjectListing
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:21
     */
    public ObjectListing listObjects(String bucketName, String prefix) throws ServiceException, ClientException;

    /**
     * @Title: 列举出对象列表
     * @Description: 根据 {@link ListObjectsRequest}实例列举出对象列表
     * @params [listObjectsRequest]
     * @return com.heredata.hos.model.ObjectListing
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:39
     */
    public ObjectListing listObjects(ListObjectsRequest listObjectsRequest) throws ServiceException, ClientException;

    /**
     * @Title: 列举出版本对象列表
     * @Description: 列举出指定前缀版本对象列表
     * @params [bucketName, prefix]
     * @return com.heredata.hos.model.VersionListing
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:43
     */
    public VersionListing listVersions(String bucketName, String prefix)
            throws ServiceException, ClientException;

    /**
     * @Title: 列举出版本对象列表
     * @Description: 列举出指定条件版本对象列表
     * @params [bucketName, prefix, startAfter, versionIdMarker, maxResults]
     * @return com.heredata.hos.model.VersionListing
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:44
     */
    public VersionListing listVersions(String bucketName, String prefix, String startAfter, String versionIdMarker, Integer maxKeys)
            throws ServiceException, ClientException;

    /**
     * @Title: 列举出版本对象列表
     * @Description: 列举出指定条件版本对象列表
     * @params [bucketName, prefix, keyMarker, versionIdMarker, maxResults]
     * @return com.heredata.hos.model.VersionListing
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:44
     */
    public VersionListing listVersions(ListVersionsRequest listVersionsRequest)
            throws ServiceException, ClientException;

    /**
     * @Title: 简单上传对象
     * @Description: 简单上传对象
     * @params [bucketName, key, input]
     * @return com.heredata.hos.model.PutObjectResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:45
     */
    public PutObjectResult putObject(String bucketName, String key, InputStream input)
            throws ServiceException, ClientException;

    /**
     * @Title: 简单上传对象
     * @Description: 简单上传对象
     * @params [bucketName, key, input]
     * @return com.heredata.hos.model.PutObjectResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:45
     */
    public PutObjectResult putObject(String bucketName, String key, InputStream input, ObjectMetadata metadata)
            throws ServiceException, ClientException;

    /**
     * @Title: 简单上传对象
     * @Description: 简单上传对象
     * @params [bucketName, key, input]
     * @return com.heredata.hos.model.PutObjectResult
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
     * @return com.heredata.hos.model.PutObjectResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:45
     */
    public PutObjectResult putObject(String bucketName, String key, File file) throws ServiceException, ClientException;

    /**
     * @Title: 简单上传对象
     * @Description: 根据 {@link PutObjectRequest}简单上传对象
     * @params [bucketName, key, input]
     * @return com.heredata.hos.model.PutObjectResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:45
     */
    public PutObjectResult putObject(PutObjectRequest putObjectRequest) throws ServiceException, ClientException;

    /**
     * @Title: 复制对象
     * @Description: 将hos的对象复制指定位置
     * @params [sourceBucketName, sourceKey, destinationBucketName, destinationKey]
     * @return com.heredata.hos.model.CopyObjectResult
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
     * @return com.heredata.hos.model.CopyObjectResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:50
     */
    public CopyObjectResult copyObject(CopyObjectRequest copyObjectRequest) throws ServiceException, ClientException;

    /**
     * @Title: 获取对象信息
     * @Description: 获取对象信息
     * @params [bucketName, key]
     * @return com.heredata.hos.model.HOSObject
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:51
     */
    public HOSObject getObject(String bucketName, String key) throws ServiceException, ClientException;

    /**
     * @Title: 获取对象信息
     * @Description: 根据 {@link GetObjectRequest}获取对象信息
     * @params [bucketName, key]
     * @return com.heredata.hos.model.HOSObject
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:51
     */
    public HOSObject getObject(GetObjectRequest getObjectRequest) throws ServiceException, ClientException;

    /**
     * @Title: 获取简化的对象元数据
     * @Description: 获取简化的对象元数据
     * @params [bucketName, key]
     * @return com.heredata.hos.model.HOSObject
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:51
     */
    public SimplifiedObjectMeta getSimplifiedObjectMeta(String bucketName, String key)
            throws ServiceException, ClientException;

    /**
     * @Title: 获取简化的对象元数据
     * @Description: 根据 {@link GenericRequest}实例获取简化的对象元数据
     * @params [bucketName, key]
     * @return com.heredata.hos.model.HOSObject
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:51
     */
    public SimplifiedObjectMeta getSimplifiedObjectMeta(GenericRequest genericRequest)
            throws ServiceException, ClientException;

    /**
     * @Title: 获取对象元数据
     * @Description: 获取对象元数据
     * @params [bucketName, key]
     * @return com.heredata.hos.model.HOSObject
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:51
     */
    public ObjectMetadata getObjectMetadata(String bucketName, String key) throws ServiceException, ClientException;

    /**
     * @Title: 获取对象元数据
     * @Description: 根据 {@link GenericRequest}实例获取对象元数据
     * @params [bucketName, key]
     * @return com.heredata.hos.model.HOSObject
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:51
     */
    public ObjectMetadata getObjectMetadata(GenericRequest genericRequest) throws ServiceException, ClientException;

    /**
     * @Title: 删除对象
     * @Description: 删除对象
     * @params [bucketName, key]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:58
     */
    public VoidResult deleteObject(String bucketName, String key) throws ServiceException, ClientException;

    /**
     * @Title: 删除对象
     * @Description: 根据 {@link GenericRequest}实例删除对象
     * @params [bucketName, key]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:58
     */
    public VoidResult deleteObject(GenericRequest genericRequest) throws ServiceException, ClientException;

    /**
     * @Title: 删除对象
     * @Description: 根据 {@link DeleteObjectsRequest}实例删除对象
     * @params [bucketName, key]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:58
     */
    public DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest)
            throws ServiceException, ClientException;

    /**
     * @Title: 删除指定版本对象
     * @Description: 删除指定版本对象
     * @params [bucketName, key]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:58
     */
    public VoidResult deleteVersion(String bucketName, String key, String versionId) throws ServiceException, ClientException;

    /**
     * @Title: 删除对象
     * @Description: 根据 {@link DeleteVersionRequest}实例删除对象
     * @params [bucketName, key]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:58
     */
    public VoidResult deleteVersion(DeleteVersionRequest deleteVersionRequest) throws ServiceException, ClientException;

    /**
     * @Title: 删除对象
     * @Description: 根据 {@link DeleteVersionsRequest}实例删除对象
     * @params [bucketName, key]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:58
     */
    public DeleteVersionsResult deleteVersions(DeleteVersionsRequest deleteVersionsRequest)
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
     * @Title: 设置对象的acl
     * @Description: 设置对象的acl
     * @params [bucketName, key, setAclRequest]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:35
     */
    public VoidResult setObjectAcl(String bucketName, String key, SetAclRequest setAclRequest)
            throws ServiceException, ClientException;

    /**
     * @Title: 设置对象的acl
     * @Description: 根据 {@link SetAclRequest}实例设置对象的acl
     * @params [setObjectAclRequest]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:35
     */
    public VoidResult setObjectAcl(SetAclRequest setObjectAclRequest) throws ServiceException, ClientException;

    /**
     * @Title: 获取对象的acl
     * @Description: 获取对象的acl
     * @params [setObjectAclRequest]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:35
     */
    public AccessControlList getObjectAcl(String bucketName, String key) throws ServiceException, ClientException;

    /**
     * @Title: 获取对象的acl
     * @Description: 根据 {@link GenericRequest}实例获取对象的acl
     * @params [setObjectAclRequest]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:35
     */
    public AccessControlList getObjectAcl(GenericRequest genericRequest) throws ServiceException, ClientException;

    /**
     * @Title: 解冻/恢复对象
     * @Description: 解冻/恢复对象
     * @params [bucketName, key]
     * @return com.heredata.hos.model.RestoreObjectResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:37
     */
    public RestoreObjectResult restoreObject(String bucketName, String key) throws ServiceException, ClientException;

    /**
     * @Title: 解冻/恢复对象
     * @Description: 根据 {@link GenericRequest}解冻/恢复对象
     * @params [bucketName, key]
     * @return com.heredata.hos.model.RestoreObjectResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:37
     */
    public RestoreObjectResult restoreObject(GenericRequest genericRequest) throws ServiceException, ClientException;


    /**
     * @Title: 解冻/恢复对象
     * @Description: 根据 {@link RestoreConfiguration}解冻/恢复对象
     * @params [bucketName, key]
     * @return com.heredata.hos.model.RestoreObjectResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:37
     */
    public RestoreObjectResult restoreObject(String bucketName, String key, RestoreConfiguration restoreConfiguration)
            throws ServiceException, ClientException;


    /**
     * @Title: 解冻/恢复对象
     * @Description: 根据 {@link RestoreObjectRequest}实例进行对象的解冻/恢复
     * @params [restoreObjectRequest]
     * @return com.heredata.hos.model.RestoreObjectResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 14:09
     */
    public RestoreObjectResult restoreObject(RestoreObjectRequest restoreObjectRequest) throws ServiceException, ClientException;


    /**
     * @Title: 设置对象标签
     * @Description: 设置对象标签  标签最多支持40对
     * @params [bucketName, key, tags]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 14:10
     */
    public VoidResult setObjectTagging(String bucketName, String key, Map<String, String> tags) throws ServiceException, ClientException;

    /**
     * @Title: 设置对象标签
     * @Description: 根据 {@link TagSet}实例设置对象标签  标签最多支持40对
     * @params [bucketName, key, tags]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 14:10
     */
    public VoidResult setObjectTagging(String bucketName, String key, TagSet tagSet) throws ServiceException, ClientException;

    /**
     * @Title: 设置对象标签
     * @Description: 根据 {@link SetObjectTaggingRequest}实例设置对象标签  标签最多支持40对
     * @params [bucketName, key, tags]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 14:10
     */
    public VoidResult setObjectTagging(SetObjectTaggingRequest setObjectTaggingRequest) throws ServiceException, ClientException;

    /**
     * @Title: 获取对象标签
     * @Description: 获取对象标签
     * @params [bucketName, key]
     * @return com.heredata.hos.model.TagSet
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 14:11
     */
    public TagSet getObjectTagging(String bucketName, String key) throws ServiceException, ClientException;

    /**
     * @Title: 获取对象标签
     * @Description: 根据 {@link GenericRequest}实例获取标签
     * @params [genericRequest]
     * @return com.heredata.hos.model.TagSet
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 14:15
     */
    public TagSet getObjectTagging(GenericRequest genericRequest) throws ServiceException, ClientException;

    /**
     * @Title: 删除对象标签
     * @Description: 删除对象标签
     * @params [bucketName, key]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 14:16
     */
    public VoidResult deleteObjectTagging(String bucketName, String key) throws ServiceException, ClientException;

    /**
     * @Title: 删除对象标签
     * @Description: 根据 {@link GenericRequest}实例删除对象标签
     * @params [genericRequest]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 14:16
     */
    public VoidResult deleteObjectTagging(GenericRequest genericRequest) throws ServiceException, ClientException;

    /**
     * @Title: 初始化任务分片上传
     * @Description: 初始化任务分片上传
     *              分片上传的步骤： 初始化分片任务上传---> 分片上传  ---> 完成上传
     * @params [request]
     * @return com.heredata.hos.model.InitiateMultipartUploadResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 14:17
     */
    public InitiateMultipartUploadResult initiateMultipartUpload(InitiateMultipartUploadRequest request)
            throws ServiceException, ClientException;

    /**
     * @Title: 列举出已经初始化分片上传的任务列表
     * @Description: 列举出已经初始化分片上传的任务列表
     * @params [request]
     * @return com.heredata.hos.model.MultipartUploadListing
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 14:18
     */
    public MultipartUploadListing listMultipartUploads(ListMultipartUploadsRequest request)
            throws ServiceException, ClientException;

    /**
     * @Title: 列举出已经上传的分片
     * @Description:
     * @params [request]
     * @return com.heredata.hos.model.PartListing
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 14:08
     */
    public PartListing listParts(ListPartsRequest request) throws ServiceException, ClientException;

    /**
     * @Title: 分片上传
     * @Description: 根据 {@link UploadPartRequest} 实例进行上传分片
     *              分片上传的步骤： 初始化分片任务上传---> 分片上传  ---> 完成上传
     * @params [request]
     * @return com.heredata.hos.model.UploadPartResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 14:06
     */
    public UploadPartResult uploadPart(UploadPartRequest request) throws ServiceException, ClientException;

    /**
     * @Title: 终止上传(仅适用于分片上传的场景)
     * @Description: 进行初始化分片上传，但是未完成合并文件的任务进行删除
     * @params [request]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 14:06
     */
    public VoidResult abortMultipartUpload(AbortMultipartUploadRequest request) throws ServiceException, ClientException;

    /**
     * @Title: 完成文件上传
     * @Description: 根据 {@link CompleteMultipartUploadRequest}实例完成文件上传(合并文件)
     *          分片上传的步骤： 初始化分片任务上传---> 分片上传  ---> 完成上传
     * @params [request]
     * @return com.heredata.hos.model.CompleteMultipartUploadResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 14:02
     */
    public CompleteMultipartUploadResult completeMultipartUpload(CompleteMultipartUploadRequest request)
            throws ServiceException, ClientException;

    /**
     * @Title: 设置桶生命周期
     * @Description: 根据 {@link SetBucketLifecycleRequest}实例设置桶生命周期
     * @params [setBucketLifecycleRequest]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 14:01
     */
    public VoidResult setBucketLifecycle(SetBucketLifecycleRequest setBucketLifecycleRequest)
            throws ServiceException, ClientException;

    /**
     * @Title: 获取桶生命周期
     * @Description: 获取桶的生命周期
     * @params [genericRequest]
     * @return java.util.List<com.heredata.hos.model.LifecycleRule>
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 14:01
     */
    public List<LifecycleRule> getBucketLifecycle(String bucketName) throws ServiceException, ClientException;

    /**
     * @Title: 获取桶生命周期
     * @Description: 根据 {@link GenericRequest}获取桶的生命周期
     * @params [genericRequest]
     * @return java.util.List<com.heredata.hos.model.LifecycleRule>
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 14:01
     */
    public List<LifecycleRule> getBucketLifecycle(GenericRequest genericRequest) throws ServiceException, ClientException;

    /**
     * @Title: 删除桶生命周期
     * @Description: 删除桶生命周期
     * @params [bucketName]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 14:00
     */
    public VoidResult deleteBucketLifecycle(String bucketName) throws ServiceException, ClientException;

    /**
     * @Title: 删除桶的生命周期
     * @Description: 根据 {@link GenericRequest}实例删除桶的生命周期
     * @params [genericRequest]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:59
     */
    public VoidResult deleteBucketLifecycle(GenericRequest genericRequest) throws ServiceException, ClientException;

    /**
     * @Title: 获取桶详情
     * @Description: 获取桶详情
     * @params [genericRequest]
     * @return com.heredata.hos.model.bucket.Bucket
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:58
     */
    public Bucket getBucketInfo(String bucketName) throws ServiceException, ClientException;

    /**
     * @Title: 获取桶详情
     * @Description: 根据 {@link GenericRequest}实例获取桶详情
     * @params [genericRequest]
     * @return com.heredata.hos.model.bucket.Bucket
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:58
     */
    public Bucket getBucketInfo(GenericRequest genericRequest) throws ServiceException, ClientException;

    /**
     * @Title: 设置桶策略
     * @Description: 设置桶策略
     * @params [setBucketPolicyRequest]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:57
     */
    public VoidResult setBucketPolicy(String bucketName, String policyText) throws ServiceException, ClientException;

    /**
     * @Title: 设置桶策略
     * @Description: 根据 {@link SetBucketPolicyRequest}设置桶策略
     * @params [setBucketPolicyRequest]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:57
     */
    public VoidResult setBucketPolicy(SetBucketPolicyRequest setBucketPolicyRequest) throws ServiceException, ClientException;

    /**
     * @Title: 获取桶策略
     * @Description: 获取桶策略
     * @params [genericRequest]
     * @return com.heredata.hos.model.GetBucketPolicyResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:50
     */
    public GetBucketPolicyResult getBucketPolicy(GenericRequest genericRequest) throws ServiceException, ClientException;

    /**
     * @Title: 获取桶策略
     * @Description: 获取桶策略
     * @params [bucketName]
     * @return com.heredata.hos.model.GetBucketPolicyResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:50
     */
    public GetBucketPolicyResult getBucketPolicy(String bucketName) throws ServiceException, ClientException;

    /**
     * @Title: 删除桶策略
     * @Description: 根据 {@link GenericRequest}删除桶策略
     * @params [bucketName]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:48
     */
    public VoidResult deleteBucketPolicy(GenericRequest genericRequest) throws ServiceException, ClientException;

    /**
     * @Title: 删除桶策略
     * @Description: 删除桶策略
     * @params [bucketName]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:48
     */
    public VoidResult deleteBucketPolicy(String bucketName) throws ServiceException, ClientException;

    /**
     * @Title: 上传文件
     * @Description: 根据 {@link CompleteMultipartUploadRequest}实例上传对象
     * @params [uploadFileRequest]
     * @return com.heredata.hos.model.UploadFileResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:48
     */
    public CompleteMultipartUploadResult uploadFile(UploadObjectRequest uploadObjectRequest) throws Throwable;

    /**
     * @Title: 下载对象
     * @Description: 根据 {@link DownloadFileRequest} 实例下载对象
     * @params [downloadFileRequest]
     * @return com.heredata.hos.model.DownloadFileResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:47
     */
    public DownloadFileResult downloadObject(DownloadFileRequest downloadFileRequest) throws Throwable;

    /**
     * @Title: 获取对象数据流
     * @Description: 200MB以下对象可以使用，200MB以上强烈建议不能使用，会耗内存
     * @params [bucket, key]
     * @return java.io.InputStream
     * @author wuzz
     * @version 1.0.0
     * @createtime 2023/4/17 13:47
     */
    public InputStream getObjectInputstream(String bucket, String key) throws Throwable;


    /**
     * @Title: 获取账户详情
     * @Description: 获取账户详情
     * @params []
     * @return com.heredata.hos.model.AccountInfo
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:47
     */
    AccountInfo getAccountInfo() throws ServiceException, ClientException;

    /**
     * @Title: 设置账户配额
     * @Description: 根据 {@link SetAccountQuotaRequest}设置账户配额
     * @params [setBucketQuotaRequest]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:43
     */
    VoidResult setAccountQuota(SetAccountQuotaRequest setAccountQuotaRequest) throws ServiceException, ClientException;


    BucketQuotaResult getBucketQuota(String bucket) throws ServiceException, ClientException;

    /**
     * @Title: 设置桶配额
     * @Description: 根据 {@link SetBucketQuotaRequest}设置桶配额
     * @params [setBucketQuotaRequest]
     * @return com.heredata.hos.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:43
     */
    VoidResult setBucketQuota(SetBucketQuotaRequest setBucketQuotaRequest) throws ServiceException, ClientException;

    /**
     * @Title: 获取账户配额
     * @Description: 获取账户配额
     * @params []
     * @return com.heredata.hos.model.AccountInfo
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 13:39
     */
    AccountInfo getAccountQuota() throws ServiceException, ClientException;
}
