const ObjectMetadata = require('./ObjectMetadata');

export class GetObjectRequest {
    constructor(params) {
        // 约束列表，如果当前分片在列表中则下载，反之不下载     暂不支持
        this.matchingETagConstraints = [];
        //非约束列表，如果下载标签不在一下列表中，则下载，反之不下载     暂不支持
        this.nonmatchingEtagConstraints = [];
        //未修改的自约束   暂不支持
        this.unmodifiedSinceConstraint = undefined;
        //修改的自约束     暂不支持
        this.modifiedSinceConstraint = undefined;

        if (!params) {
            params = {};
        }

        // 设置桶名
        this.bucketName = params.bucketName;
        // 设置对象名
        this.objectName = params.objectName;
        // 获取对象流文件的范围(end,start]
        this.range = params.range;
        // 是否包含流，true为包含流，false不包含
        this.includeInputStream = params.includeInputStream ? true : false;
        // 下面三个属性是客户端加密使用到参数
        // 客户端加密，加密算法，目前仅支持AES256
        this.clientSideEncryptionAlgorithm = 'AES256';
        //客户端加密，加密秘钥，256长度字符串，再经过base64编码
        this.clientSideEncryptionKey = params.clientSideEncryptionKey;
        //客户端加密，clientSideEncryptionKey属性的MD5值
        this.clientSideEncryptionKeyMD5 = params.clientSideEncryptionKeyMD5;
        this.versionId = params.versionId;
    }

    getIncludeInputStream() {
        return this.includeInputStream;
    }

    getRange() {
        return this.range;
    }

}

export class HOSObject {
    constructor(params) {
        this.bucketName = params.bucketName;
        this.objectName = params.objectName;

        this.objectMetadata = params.objectMetadata ? params.objectMetadata : new ObjectMetadata();
        this.objectContent = params.objectContent;
        this.eTag = params.eTag;
        this.lastModified = params.lastModified;
        this.size = params.size;
        this.mimeType = params.mimeType;
    }

    getObjectContent() {
        return this.objectContent;
    }

}
