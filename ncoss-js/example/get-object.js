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

// Note: YOUR-ACCESSKEYID, YOUR-SECRETACCESSKEY, my-bucketname and my-objectname
// are dummy values, please replace them with original values.

/**
 * 导入客户端变量
 */
var {s3Client,s3ClientV4} = require('./getS3Client')
var ObjectDomain = require('../dist/main/ObjectDomain')

/**
 * 仅查询元数据信息，不包括流信息
 */
s3ClientV4.statObject('nodejs', '2023/04/23/a.avi',{versionId:"beff39a2457111eeb3a4fa163e2fcf06"}, function (e, dataStream) {
  console.log('最终结果',dataStream);
})


// /**
//  * 查询元数据信息及对象内容
//  */
// var getObjectRequest = new ObjectDomain.GetObjectRequest({
//   includeInputStream:true,
//   versionId:"e209392a457111eeb3a4fa163e2fcf06"
// });
//
// var size = 0
// // Get a full object.
// s3ClientV4.getObject('nodejs', '2023/04/23/a.avi', getObjectRequest, function (e, dataStream) {
//   let aa = Buffer.from(dataStream.getObjectContent()).toString('utf8');
//   console.log('数据流：：'+aa);
//
//   console.log('其他数据信息',dataStream);
// })
