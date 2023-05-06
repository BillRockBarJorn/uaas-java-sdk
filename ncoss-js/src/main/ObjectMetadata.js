import * as errors from './errors.js'

class ObjectMetadata {
  constructor() {
    /**
     * 自定义元数据对象,前缀为 x-hos-meta-*.
     */
    this.userObjectMetadata = {};
    /**
     * HOS系统特定含义元数据
     */
    this.metadata = {};

    Object.setPrototypeOf(this,ObjectMetadata.prototype);
  }

  /**
   * 获取自定义元数据map集合
   */
  getUserMetadata() {
    return this.userObjectMetadata;
  }


  /**
   * 获取HOS系统特定含义元数据
   */
  getMetadata() {
    return this.metadata;
  }

  /**
   * 设置用户自定义元数据
   */
  setUserMetadata(userMetadata) {
    this.userObjectMetadata = userMetadata;
  }

  /**
   * 设置头信息
   */
  setHeader(key, value) {
    metadata[key] = value;
  }

  /**
   * 根据键移除特定的头数据
   */
  removeHeader(key) {
    metadata.remove(key);
  }

  /**
   * 添加用户自定义元数据
   */
  addUserMetadata(key, value) {
    this.userObjectMetadata[key] = value;
  }

  /**
   * 获取最后的修改时间
   */
  getLastModified() {
    return this.metadata['Last-Modified'];
  }

  /**
   * 设置最新的修改时间
   */
  setLastModified(lastModified) {
    this.metadata['Last-Modified'] = lastModified;
  }

  /**
   * 设置到期时间
   */
  setExpirationTime(expirationTime) {
    metadata.put('Expires', expirationTime);
  }

  /**
   * 获取http请求协议body的content-length
   */
  getContentLength() {
    let contentLength = metadata['Content-Length'];
    return contentLength ? 0 : contentLength.longValue();
  }

  /**
   * 设置http请求协议中body的长度
   */
  setContentLength(contentLength) {
    this.metadata['Content-Length'] = contentLength;
  }

  /**
   * 获取http请求协议中body的content-type
   */
  getContentType() {
    return this.metadata['Content-Type'];
  }

  /**
   * 获取http请求协议中body的content-type
   */
  setContentType(contentType) {
    this.metadata['Content-Type'] = contentType;
  }

  /**
   * 获取http请求协议中的body中的MD5值
   */
  getContentMD5() {
    return this.metadata['Content-MD5'];
  }

  /**
   * 设置http请求协议中的body中的MD5值
   */
  setContentMD5(contentMD5) {
    metadata['Content-MD5'] = contentMD5;
  }

  /**
   * 获取http请求协议中的body中的Encode值
   * @return
   */
  getContentEncoding() {
    return this.metadata['Content-Encoding'];
  }

  /**
   * 设置http请求协议中的body中的Encode值
   */
  setContentEncoding(encoding) {
    metadata['Content-Encoding'] = encoding;
  }

  /**
   * 获取请求头中的Cache-Control
   * 用于指定所有缓存机制在整个请求/响应链中必须服从的指令。
   * 这些指令指定用于阻止缓存对请求或响应造成不利干扰的行为。
   * 这些指令通常覆盖默认缓存算法。
   * 缓存指令是单向的，即请求中存在一个指令并不意味着响应中将存在同一个指令。
   * 网页的缓存是由HTTP消息头中的“Cache-Control”来控制的，
   * 常见的取值有private、no-cache、max-age、must-revalidate等，默认为private。
   */
  getCacheControl() {
    return this.metadata['Cache-Control'];
  }

  /**
   * 设置请求头中的Cache-Control
   * 用于指定所有缓存机制在整个请求/响应链中必须服从的指令。
   * 这些指令指定用于阻止缓存对请求或响应造成不利干扰的行为。
   * 这些指令通常覆盖默认缓存算法。
   * 缓存指令是单向的，即请求中存在一个指令并不意味着响应中将存在同一个指令。
   * 网页的缓存是由HTTP消息头中的“Cache-Control”来控制的，
   * 常见的取值有private、no-cache、max-age、must-revalidate等，默认为private。
   */
  setCacheControl(cacheControl) {
    metadata['Cache-Control'] = cacheControl;
  }

  /**
   * 设置请求头中的Content-Disposition
   * MIME 协议的扩展，MIME 协议指示 MIME 用户代理如何显示附加的文件。
   * Content-disposition其实可以控制用户请求所得的内容存为一个文件的时候提供一个默认的文件名，
   * 文件直接在浏览器上显示或者在访问时弹出文件下载对话框
   */
  getContentDisposition() {
    return this.metadata['Content-Disposition'];
  }

  /**
   * 设置请求头中的Content-Disposition
   * MIME 协议的扩展，MIME 协议指示 MIME 用户代理如何显示附加的文件。
   * Content-disposition其实可以控制用户请求所得的内容存为一个文件的时候提供一个默认的文件名，
   * 文件直接在浏览器上显示或者在访问时弹出文件下载对话框
   */
  setContentDisposition(disposition) {
    metadata['Content-Disposition'] = disposition;
  }

  /**
   * 获取标签信息
   * @return
   */
  getETag() {
    return this.metadata['ETag'];
  }

  /**
   * 服务端加密，获取加密有效值，目前仅支持here:kms
   * @return
   */
  getServerSideEncryption() {
    return this.metadata['x-hos-server-side-encryption'];
  }

  /**
   * 服务端加密，设置加密有效值，目前仅支持here:kms
   */
  setServerSideEncryption(serverSideEncryption) {
    metadata['x-hos-server-side-encryption'] = serverSideEncryption;
  }

  /**
   * 服务端加密，获取HKMS服务返回的secretId值
   */
  getServerSideEncryptionKeyId() {
    return this.metadata['x-hos-server-side-encryption-here-kms-key-id'];
  }

  /**
   * 服务端加密，设置HKMS服务返回的secretId值
   */
  setServerSideEncryptionKeyId(serverSideEncryptionKeyId) {
    metadata['x-hos-server-side-encryption-here-kms-key-id'] = serverSideEncryptionKeyId;
  }

  /**
   * 客户端加密，设置加密算法  {@link AlgorithmEnum}
   * 目前仅支持AES256
   * @param algorithmEnum
   */
  setClientSideEncryptionAlgorithm(algorithmEnum) {
    if (algorithmEnum === 'AES256') {
      throw new errors.AlgorithmError('algorithmEnum\'s value is invalida. This param is onlu support AES256');
    }
    metadata['x-hos-server-side-encryption-customer-algorithm'] = algorithmEnum;
  }

  /**
   * 客户端加密，设置加密算法
   */
  getClientSideEncryptionAlgorithm() {
    return metadata['x-hos-server-side-encryption-customer-algorithm'];
  }

  // /**
  //  * 客户端加密，设置秘钥的BASE64值
  //  */
  // setClientSideEncryptionKey(key) {
  //   if (key == null && key.length() != 32) {
  //     throw new ServiceException("非法的字符串长度");
  //   }
  //   let s = Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.UTF_8));
  //   metadata['x-hos-server-side-encryption-customer-key']= s;
  //   byte[] bytes = StringUtils.encrypByMD5Arr(key);
  //   metadata['x-hos-server-side-encryption-customer-key-MD5']= Base64.getEncoder().encodeToString(bytes));
  // }

  /**
   * 客户端加密，获取秘钥的BASE64值
   */
  getClientSideEncryptionKey() {
    return this.metadata['x-hos-server-side-encryption-customer-key'];
  }

  /**
   * 客户端加密，获取秘钥的MD5值
   */
  getClientSideEncryptionKeyMD5() {
    return this.metadata['x-hos-server-side-encryption-customer-key-MD5'];
  }


  // /**
  //  * 设置对象的ACL授权人权限
  //  * @param permission
  //  */
  // @Deprecated
  // public void setObjectAcl(Permission permission) {
  //   metadata.put(HOSHeaders.HOS_OBJECT_ACL, permission != null ? permission.toString() : "");
  // }


  /**
   * 获取请求ID
   */
  getRequestId() {
    return this.metadata['x-hos-request-id'];
  }

  /**
   * 设置请求ID
   */
  setRequestId(requestId) {
    return this.metadata['x-hos-request-id'] = requestId;
  }

  /**
   * 获取对象版本信息
   */
  getVersionId() {
    return this.metadata['x-hos-version-id'];
  }

  /**
   * 获取冗余校验值
   * @return
   */
  getServerCRC() {
    let crc64ecma = this.metadata['x-hos-hash-crc64ecma'];

    if (crc64ecma != null) {
      return bi;
    }
    return null;
  }


  /**
   * 复制对象时，设置目标对象元数据的类型
   * 标识新对象的元数据是从源对象中复制，还是用请求中的元数据替换或更新
   */
  setObjectDirective(directive) {
    const objectDirectiveArr = ['COPY', 'REPLACE', 'REPLACE_NEW'];
    if (objectDirectiveArr.indexOf(directive) === -1) {
      throw new errors.DirectiveError('directive\'s value is invalida. Please ensure its value is one of [COPY, REPLACE, REPLACE_NEW]');
    }
    this.metadata['x-hos-metadata-directive'] = directive;
  }

  /**
   * 设置对象的存储类型  
   */
  setObjectStorageClass(storageClass) {
    const storageClaasArr = ['STANDARD', 'IA', 'ARCHIVE'];
    if (storageClaasArr.indexOf(storageClass) === -1) {
      throw new errors.StorageClassError('storageClass\'s value is invalida. Please ensure its value is one of [STANDARD,IA,ARCHIVE]');
    }
    this.metadata['x-hos-storage-class'] = storageClass;
  }

  /**
   * 获取对象的存储类型
   */
  getObjectStorageClass() {
    let storageClassString = this.metadata['x-hos-storage-class'];
    if (!storageClassString) return 'STANDARD';
  }

  /**
   * 获取Archive类型对象的解冻状态
   */
  getObjectRestoreStatus() {
    return this.metadata['x-hos-restore'];
  }
}

module.exports = ObjectMetadata;