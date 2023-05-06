# 适用于 Amazon S3 兼容云存储的 Minio JavaScript Library [![Slack](https://slack.min.io/slack?type=svg)](https://slack.min.io)

[![NPM](https://nodei.co/npm/minio.png)](https://nodei.co/npm/minio/)

MinIO JavaScript Client SDK 提供简单的 API 来访问任何 Amazon S3 兼容的对象存储服务。

本快速入门指南将向您展示如何安装客户端 SDK 并执行示例 JavaScript 程序。有关 API 和示例的完整列表，请参阅[JavaScript 客户端 API 参考](https://min.io/docs/minio/linux/developers/javascript/API.html/javascript-client-api-reference)文档。

本文假设你已经安装了[nodejs](http://nodejs.org/) 。

## 使用 NPM 下载

```sh
npm install --save ncoss
```

## 初使化 NCOSS Client

你需要设置 5 个属性来链接 Minio 对象存储服务。

| 参数      | 描述                                                                                            |
| :-------- | :---------------------------------------------------------------------------------------------- |
| endPoint  | 对象存储服务的 ip                                                                               |
| port      | TCP/IP 端口号。可选值，如果是使用 HTTP 的话，默认值是`80`；如果使用 HTTPS 的话，默认值是`443`。 |
| path      | 对象存储服务接口统一前缀。                                                                      |
| accessKey | Access key 是唯一标识你的账户的用户 ID。                                                        |
| secretKey | Secret key 是你账户的密码。                                                                     |
| useSSL    | true 代表使用 HTTPS                                                                             |

```js
var NCOSS = require('ncoss')

var client = new NCOSS.Client({
	endPoint: '',
	port: 9000,
	path: 'v1',
	useSSL: true,
	accessKey: 'Q3AM3UQ867SPQQA43P2F',
	secretKey: 'zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG',
})
```

## 示例-文件上传

本示例连接到一个对象存储服务，创建一个存储桶并上传一个文件到存储桶中。

#### file-uploader.js

```js
var NCOSS = require('ncoss')

var client = new NCOSS.Client({
	endPoint: '',
	port: 9000,
	path: 'v1',
	useSSL: true,
	accessKey: 'Q3AM3UQ867SPQQA43P2F',
	secretKey: 'zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG',
})

// File that needs to be uploaded.
var file = '/tmp/photos-europe.tar'

// Make a bucket called europetrip.
client.makeBucket('europetrip', function (err) {
	if (err) return console.log(err)

	console.log('Bucket created successfully.')

	var metaData = {
		'Content-Type': 'application/octet-stream',
	}
	// Using fPutObject API upload your file to the bucket europetrip.
	client.fPutObject(
		'europetrip',
		'photos-europe.tar',
		file,
		metaData,
		function (err, etag) {
			if (err) return console.log(err)
			console.log('File uploaded successfully.')
		}
	)
})
```

#### 运行 file-uploader

```sh
node file-uploader.js
Bucket created successfully.

```

## API 文档

完整的 API 文档在这里。

-   [完整 API 文档](https://min.io/docs/minio/linux/developers/javascript/API.html)

### API 文档 : 操作存储桶

-   [`makeBucket`](https://min.io/docs/minio/linux/developers/javascript/API.html#makeBucket)
-   [`listBuckets`](https://min.io/docs/minio/linux/developers/javascript/API.html#listBuckets)
-   [`bucketExists`](https://min.io/docs/minio/linux/developers/javascript/API.html#bucketExists)
-   [`removeBucket`](https://min.io/docs/minio/linux/developers/javascript/API.html#removeBucket)
-   [`listObjects`](https://min.io/docs/minio/linux/developers/javascript/API.html#listObjects)
-   [`listObjectsV2`](https://min.io/docs/minio/linux/developers/javascript/API.html#listObjectsV2)
-   [`listIncompleteUploads`](https://min.io/docs/minio/linux/developers/javascript/API.html#listIncompleteUploads)

### API 文档 : 操作文件对象

-   [`fPutObject`](https://min.io/docs/minio/linux/developers/javascript/API.html#fPutObject)
-   [`fGetObject`](https://min.io/docs/minio/linux/developers/javascript/API.html#fGetObject)

### API 文档 : 操作对象

-   [`getObject`](https://min.io/docs/minio/linux/developers/javascript/API.html#getObject)
-   [`putObject`](https://min.io/docs/minio/linux/developers/javascript/API.html#putObject)
-   [`copyObject`](https://min.io/docs/minio/linux/developers/javascript/API.html#copyObject)
-   [`statObject`](https://min.io/docs/minio/linux/developers/javascript/API.html#statObject)
-   [`removeObject`](https://min.io/docs/minio/linux/developers/javascript/API.html#removeObject)
-   [`removeIncompleteUpload`](https://min.io/docs/minio/linux/developers/javascript/API.html#removeIncompleteUpload)

### API 文档 : 存储桶策略

-   [`getBucketPolicy`](https://min.io/docs/minio/linux/developers/javascript/API.html#getBucketPolicy)
-   [`setBucketPolicy`](https://min.io/docs/minio/linux/developers/javascript/API.html#setBucketPolicy)

## 完整示例

#### 完整示例 : 操作存储桶

-   [list-buckets.js](https://github.com/minio/minio-js/blob/master/examples/list-buckets.js)
-   [list-objects.js](https://github.com/minio/minio-js/blob/master/examples/list-objects.js)
-   [list-objects-v2.js](https://github.com/minio/minio-js/blob/master/examples/list-objects-v2.js)
-   [bucket-exists.js](https://github.com/minio/minio-js/blob/master/examples/bucket-exists.js)
-   [make-bucket.js](https://github.com/minio/minio-js/blob/master/examples/make-bucket.js)
-   [remove-bucket.js](https://github.com/minio/minio-js/blob/master/examples/remove-bucket.js)
-   [list-incomplete-uploads.js](https://github.com/minio/minio-js/blob/master/examples/list-incomplete-uploads.js)

#### 完整示例 : 操作文件对象

-   [fput-object.js](https://github.com/minio/minio-js/blob/master/examples/fput-object.js)
-   [fget-object.js](https://github.com/minio/minio-js/blob/master/examples/fget-object.js)

#### 完整示例 : 操作对象

-   [put-object.js](https://github.com/minio/minio-js/blob/master/examples/put-object.js)
-   [get-object.js](https://github.com/minio/minio-js/blob/master/examples/get-object.js)
-   [copy-object.js](https://github.com/minio/minio-js/blob/master/examples/copy-object.js)
-   [get-partialobject.js](https://github.com/minio/minio-js/blob/master/examples/get-partialobject.js)
-   [remove-object.js](https://github.com/minio/minio-js/blob/master/examples/remove-object.js)
-   [remove-incomplete-upload.js](https://github.com/minio/minio-js/blob/master/examples/remove-incomplete-upload.js)
-   [stat-object.js](https://github.com/minio/minio-js/blob/master/examples/stat-object.js)

#### 完整示例 : 存储桶策略

-   [get-bucket-policy.js](https://github.com/minio/minio-js/blob/master/examples/get-bucket-policy.js)
-   [set-bucket-policy.js](https://github.com/minio/minio-js/blob/master/examples/set-bucket-policy.js)

## 了解更多

-   [完整文档](<[https://docs.min.i](https://min.io/docs/minio/kubernetes/upstream/index.html)o>)
-   [MinIO JavaScript Client SDK API 文档](https://min.io/docs/minio/linux/developers/javascript/API.html)
-   [创建属于你的购物 APP-完整示例](https://github.com/minio/minio-js-store-app)

## 贡献

[贡献者指南](https://github.com/minio/minio-js/blob/master/CONTRIBUTING.md)

[![Build Status](https://travis-ci.org/minio/minio-js.svg)](https://travis-ci.org/minio/minio-js)
[![Build status](https://ci.appveyor.com/api/projects/status/1d05e6nvxcelmrak?svg=true)](https://ci.appveyor.com/project/harshavardhana/minio-js)
