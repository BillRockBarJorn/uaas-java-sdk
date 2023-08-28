/*
 * MinIO Javascript Library for Amazon S3 Compatible Cloud Storage, (C) 2015 MinIO, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Note: YOUR-ACCESSKEYID, YOUR-SECRETACCESSKEY and my-bucketname are
// dummy values, please replace them with original values.

/**
 * 导入客户端变量
 */
var {s3Client,s3ClientV4} = require('./getS3Client')

// List all object paths in bucket my-bucketname.
var objectsStream = s3Client.listObjectsV2('ncoss-4js', '', true, '')
objectsStream.on('data', function (obj) {
  console.log(obj)
})
objectsStream.on('error', function (e) {
  console.log(e)
})
