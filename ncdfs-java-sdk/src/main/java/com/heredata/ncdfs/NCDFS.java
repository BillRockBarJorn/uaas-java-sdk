package com.heredata.ncdfs;

import com.heredata.ncdfs.exception.ClientException;
import com.heredata.ncdfs.exception.ServiceException;
import com.heredata.ncdfs.model.*;
import com.heredata.ncdfs.model.DirectoryResult.DirectoryListResult;
import com.heredata.ncdfs.model.FileSystemResult.FileSystemListResult;
import com.heredata.ncdfs.model.ShareFileSystem.ShareFileSystemList;

/**
 * <p>Title: ncdfs提供的所有的nchos功能接口</p>
 * <p>Description: ncdfs提供的所有的nchos功能接口</p>
 * <p>Copyright: Copyright (c) 2022</p>
 * <p>Company: Here-Data </p>
 * @author wuzz
 * @version 1.0.0
 * @createtime 2022/10/11 9:34
 *
 */
public interface NCDFS {

    /**
     * @Title: 关闭连接
     * @Description: 关闭实例（释放所有资源) 调用shutdown其他功能后不可用。
     * @params []
     * @return void
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 9:34
     */
    public void shutdown();

    /**
     * @Title: 创建编码策略
     * @Description: 在clusterName集群中创建一种编码策略。
     * @params [createCodePolicyRequest]
     * @return com.heredata.ncdfs.model.SetCodePolicyResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 9:34
     */
    public SetCodePolicyResult createCodePolicy(String clusterName, CreateCodePolicyRequest createCodePolicyRequest) throws ServiceException, ClientException;

    /**
     * @Title: 获取编码策略列表
     * @Description: 查看clusterName集群存在的所有编码策略信息。
     * @params [clusterName]
     * @return com.heredata.ncdfs.model.CodePolicyResult.CodePolicy2
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 9:34
     */
    public CodePolicyListResult listCodePolicy(String clusterName) throws ServiceException, ClientException;

    /**
     * @Title: 删除clusterName集群中policyId对应的编码策略。
     * @Description: TODO
     * @params [clusterNmae, policyId]
     * @return com.heredata.ncdfs.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 9:38
     */
    public VoidResult deleteCodePolicy(String clusterNmae, Integer policyId) throws ServiceException, ClientException;

    /**
     * @Title: 在clusterName集群中创建用户
     * @Description: 在clusterName集群中创建一个名为username的用户。
     * @params [clusterName, createUserRequest]
     * @return com.heredata.ncdfs.model.CreateUserResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 9:39
     */
    public CreateUserResult createUser(String clusterName, CreateUserRequest createUserRequest) throws ServiceException, ClientException;

    /**
     * @Title: 修改用户的属性信息
     * @Description: 修改用户的属性信息
     * @params [clusterName, createUserRequest]
     * @return com.heredata.ncdfs.model.CreateUserResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 9:39
     */
    public VoidResult updateUser(String clusterName, CreateUserRequest createUserRequest) throws ServiceException, ClientException;

    /**
     * @Title: 根据用户名称查询对应的用户信息
     * @Description: 根据用户名称查询对应的用户信息
     * @params [clusterName, userName]
     * @return com.heredata.ncdfs.model.UserResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 9:40
     */
    public UserResult getUser(String clusterName, String userName) throws ServiceException, ClientException;

    /**
     * @Title: 查询集群内用户列表
     * @Description: 查询集群内用户列表
     * @params [clusterName]
     * @return com.heredata.ncdfs.model.ListUserResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 9:40
     */
    public ListUserResult listUser(String clusterName) throws ServiceException, ClientException;

    /**
     * @Title: 根据用户名删除用户信息
     * @Description: 根据用户名删除用户信息
     * @params [clusterName, userName]
     * @return com.heredata.ncdfs.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 9:42
     */
    public VoidResult deleteUser(String clusterName, String userName) throws ServiceException, ClientException;

    /**
     * @Title: 根据用户名批量删除用户信息
     * @Description: 根据用户名删除用户信息
     * @params [clusterName, userName]
     * @return com.heredata.ncdfs.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 9:42
     */
    public DeleteUsersResult deleteUsers(String clusterName, String... userName) throws ServiceException, ClientException;

    /**
     * @Title: 用户与服务器系统用户绑定
     * @Description: 用户与服务器系统用户绑定
     * @params [clusterName, userName, systemUser]
     * @return com.heredata.ncdfs.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 9:41
     */
    public VoidResult grantSystemUser(String clusterName, String userName, String systemUser) throws ServiceException, ClientException;

    /**
     * @Title: 获取集群内与服务器系统用户绑定的关系列表
     * @Description: 获取集群内与服务器系统用户绑定的关系列表
     * @params [clusterName]
     * @return com.heredata.ncdfs.model.UserRelationshipResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 9:43
     */
    public UserRelationshipResult getUserRelationship(String clusterName) throws ServiceException, ClientException;

    /**
     * @Title: 解除集群内与服务器系统用户绑定的关系
     * @Description: 解除集群内与服务器系统用户绑定的关系
     * @params [clusterName, userName, systemUser]
     * @return com.heredata.ncdfs.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 9:44
     */
    public VoidResult deleteSystemUser(String clusterName, String userName, String systemUser) throws ServiceException, ClientException;

    /**
     * @Title: 创建文件系统
     * @Description: 在集群下创建文件系统
     * @params [clusterName, fileSystemName, createFileSystemRequest]
     * @return com.heredata.ncdfs.model.CreateFileSystemResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 9:44
     */
    public CreateFileSystemResult createFileSystem(String clusterName, String fileSystemName, CreateFileSystemRequest createFileSystemRequest) throws ServiceException, ClientException;

    /**
     * @Title: 删除文件系统
     * @Description: 删除文件系统
     * @params [clusterName, fileSystemName]
     * @return com.heredata.ncdfs.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 9:48
     */
    public VoidResult deleteFileSystem(String clusterName, String fileSystemName) throws ServiceException, ClientException;

    /**
     * @Title: 修改文件系统属性
     * @Description: 修改文件系统属性
     * @params [clusterName, fileSystemName, createFileSystemRequest]
     * @return com.heredata.ncdfs.model.CreateFileSystemResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 9:51
     */
    public CreateFileSystemResult updateFileSystem(String clusterName, String fileSystemName, CreateFileSystemRequest createFileSystemRequest)
            throws ServiceException, ClientException;

    /**
     * @Title: 获取集群下的文件系统列表
     * @Description: 获取集群下的文件系统列表
     * @params [clusterName]
     * @return com.heredata.ncdfs.model.FileSystemResult.FileSystemListResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 9:51
     */
    public FileSystemListResult listFileSystem(String clusterName)
            throws ServiceException, ClientException;

    /**
     * @Title: 获取指定文件系统信息
     * @Description: 获取指定文件系统信息
     * @params [clusterName, fileSystemName]
     * @return com.heredata.ncdfs.model.FileSystemResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 9:54
     */
    public FileSystemResult getFileSystem(String clusterName, String fileSystemName) throws ServiceException, ClientException;

    /**
     * @Title: 为集群下指定文件系统挂载目录
     * @Description: 将clusterName中的FileSystemName文件系统与系统中的某个目录绑定挂载
     *               ，使得FileSystemName在本地文件系统中成为一个共享文件系统
     *               ，在使用上与普通文件系统无差别。
     * @params [clusterName, fileSystemName, localmountDir]
     * @return com.heredata.ncdfs.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 9:55
     */
    public VoidResult fuseMountedFileSystem(String clusterName, String fileSystemName, String localmountDir) throws ServiceException, ClientException;

    /**
     * @Title: 删除指定集群下文件系统的挂载目录
     * @Description: 将clusterName中的FileSystemName文件系统与系统中的某个目录解除绑定挂载
     *              ，解除绑定后在本地则无法共享分布式文件系统。
     * @params [clusterName, fileSystemName, localmountDir]
     * @return com.heredata.ncdfs.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 9:55
     */
    public VoidResult fuseDeletedFileSystem(String clusterName, String fileSystemName, String localMountDir) throws ServiceException, ClientException;

    /**
     * @Title: 获取挂载记录信息列表
     * @Description: 获取clusterName集群中所有基于FUSE创建的共享文件系统信息。
     * @params [clusterName]
     * @return com.heredata.ncdfs.model.ShareFileSystem.ShareFileSystemList
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 9:56
     */
    public ShareFileSystemList listFileSystemByFuse(String clusterName) throws ServiceException, ClientException;

    /**
     * @Title: 创建fuse共享目录
     * @Description: 将clusterName中的FileSystemName文件系统中的一个指定目录与系统中的某个目录绑定挂载，
     *              使得FileSystemName中的这个指定目录在挂载目录上成为一个共享文件系统，
     *              在使用上与普通目录无差别。
     * @params [clusterName, fileSystemName, shareDir, localMoutDir]
     * @return com.heredata.ncdfs.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 9:58
     */
    public VoidResult createFuseShareDir(String clusterName, String fileSystemName, String shareDir, String localMoutDir)
            throws ServiceException, ClientException;

    /**
     * @Title: 删除fuse共享目录
     * @Description: 将已经创建的共享目录删除。
     * @params [bucketName, key, input]
     * @return com.heredata.ncdfs.model.PutObjectResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:45
     */
    public VoidResult deleteFuseShareDir(String clusterName, String fileSystemName, String shareDir, String localMoutDir)
            throws ServiceException, ClientException;

    /**
     * @Title: 获取fuse共享目录列表
     * @Description: 获取指定文件系统中已创建的所有共享目录列表。
     * @params [bucketName, key, input]
     * @return com.heredata.ncdfs.model.PutObjectResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/9/2 11:45
     */
    public ShareDirResult.ShareDirListResult getFuseShareDirInFileSystem(String clusterName, String fileSystemName)
            throws ServiceException, ClientException;

    /**
     * @Title: 获取指定fuse共享目录信息
     * @Description: 获取指定fuse共享目录信息
     * @params [clusterName, fileSystemName, shareDir, localMoutDir]
     * @return com.heredata.ncdfs.model.FuseShareDirResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 10:00
     */
    public ShareDirResult getFuseShareDir(String clusterName, String fileSystemName, String shareDir, String localMoutDir)
            throws ServiceException, ClientException;

    /**
     * @Title: 创建nfs共享目录
     * @Description: 将clusterName中的FileSystemName文件系统中的一个指定目录设置成nfs服务端目录，
     *              自动计算本地绝对路径，写入nfs服务端配置表中，
     *              使得nfs客户端可以访问此nfs服务端。
     * @params [clusterName, fileSystemName, shareDir, localMoutDir]
     * @return com.heredata.ncdfs.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 10:02
     */
    public VoidResult createNFSDir(String clusterName, String fileSystemName, String shareDir, String localMoutDir) throws ServiceException, ClientException;

    /**
     * @Title: 删除nfs共享目录
     * @Description: 将已经创建的nfs共享目录删除。
     * @params [clusterName, fileSystemName, shareDir, localMoutDir]
     * @return com.heredata.ncdfs.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 10:02
     */
    public VoidResult deleteNFSDir(String clusterName, String fileSystemName, String shareDir, String localMoutDir) throws ServiceException, ClientException;

    /**
     * @Title: 获取nfs共享目录列表
     * @Description: 获取指定文件系统中已创建的所有nfs共享目录列表。
     * @params [clusterName, fileSystemName]
     * @return com.heredata.ncdfs.model.FuseShareDirResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 10:04
     */
    public ShareDirResult.ShareDirListResult getNFSShareDirInFileSystem(String clusterName, String fileSystemName) throws ServiceException, ClientException;

    /**
     * @Title: 获取指定nfs共享目录详细信息
     * @Description: 获取指定集群中已创建的nfs共享目录列表。
     * @params [clusterName, fileSystemName, shareDir, localMoutDir]
     * @return com.heredata.ncdfs.model.FuseShareDirResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 10:05
     */
    public ShareDir getNFSShareDir(String clusterName, String fileSystemName, String shareDir, String localMoutDir) throws ServiceException, ClientException;

    /**
     * @Title: 在指定文件系统中创建一个目录。
     * @Description: 在指定文件系统中创建一个目录
     * @params [clusterName, fileSystemName, userName, createDirRequest]
     * @return com.heredata.ncdfs.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 10:05
     */
    public SetDirectoryResult createDir(String clusterName, String fileSystemName, String userName, CreateDirRequest createDirRequest) throws ServiceException, ClientException;

    /**
     * @Title: 修改目录
     * @Description: 修改指定集群下的指定文件系统的目录的属性
     * @params [clusterName, fileSystemName, userName, updateDirRequest]
     * @return com.heredata.ncdfs.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 10:06
     */
    public SetDirectoryResult updateDir(String clusterName, String fileSystemName, String userName, UpdateDirRequest updateDirRequest) throws ServiceException, ClientException;

    /**
     * @Title: 删除目录信息
     * @Description: 删除指定集群下的指定文件系统的目录
     * @params [clusterName, fileSystemName, userName, path]
     * @return com.heredata.ncdfs.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 10:07
     */
    public VoidResult deleteDir(String clusterName, String fileSystemName, String userName, String path)
            throws ServiceException, ClientException;

    /**
     * @Title: 检查目录是否存在
     * @Description: 检查目录是否存在
     * @params [clusterName, fileSystem, user, path]
     * @return com.heredata.ncdfs.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 10:08
     */
    public VoidResult isExistDir(String clusterName, String fileSystemName, String userName, String path)
            throws ServiceException, ClientException;

    /**
     * @Title: 检查文件是否存在
     * @Description: 检查文件是否存在
     * @params [clusterName, fileSystem, user, path]
     * @return com.heredata.ncdfs.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 10:08
     */
    public VoidResult isExistFile(String clusterName, String fileSystemName, String userName, String path) throws ServiceException, ClientException;

    /**
     * @Title: 获取文件夹的属性信息
     * @Description: 获取文件夹的属性信息
     * @params [clusterName, fileSystem, user, path]
     * @return com.heredata.ncdfs.model.DirectoryResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 10:09
     */
    public DirectoryResult getDirProperties(String clusterName, String fileSystemName, String userName, String path) throws ServiceException, ClientException;

    /**
     * @Title: 获取文件的属性信息
     * @Description: 获取文件的属性信息
     * @params [bucketName]
     * @return com.heredata.ncdfs.model.VoidResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 10:10
     */
    public VoidResult getFileProperties(String clusterName, String fileSystemName, String userName, String path) throws ServiceException, ClientException;

    /**
     * @Title: 获取某目录的目录列表
     * @Description: 获取某目录的目录列表
     * @params [clusterName, fileSystem, user, path]
     * @return com.heredata.ncdfs.model.DirectoryResult.DirectoryListResult
     * @author wuzz
     * @version 1.0.0
     * @createtime 2022/10/11 10:10
     */
    public DirectoryListResult listDir(String clusterName, String fileSystemName, String userName, String path) throws ServiceException, ClientException;
}
