/*
 * MinIO Javascript Library for Amazon S3 Compatible Cloud Storage, (C) 2016 MinIO, Inc.
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

// Note: YOUR-ACCESSKEYID, YOUR-SECRETACCESSKEY, my-bucketname, my-objectname,
// my-src-bucketname and my-src-objectname are dummy values, please replace
// them with original values.

/**
 * 导入客户端变量
 */
var {s3Client,s3ClientV4} = require('./getS3Client')
var ObjectMetadata = require('../dist/main/ObjectMetadata')

// 创建对象元数据
var objectMetadata = new ObjectMetadata({});
objectMetadata.setObjectStorageClass('STANDARD'); //设置存储类型
objectMetadata.addUserMetadata('meta1', 'value1'); // 设置自定义元数据
objectMetadata.addUserMetadata('meta2', 'value2');
objectMetadata.addUserMetadata('meta3', 'value3');
objectMetadata.setObjectDirective('REPLACE_NEW'); // 设置复制类型

// 调用函数进行复制
s3ClientV4.copyObject('ncoss-5js', '2023/04/21/dest.txt', 'ncoss-4js', '2023/04/23/a.avi', objectMetadata, function (e, data) {
  if (e) {
    return console.log(e)
  }
  console.log("Successfully copied the object:")
  console.log("etag = " + data.etag + ", lastModified = " + data.lastModified)
})
