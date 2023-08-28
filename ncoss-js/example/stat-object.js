/*
 * MinIO Javascript Library for Amazon S3 Compatible Cloud Storage, (C) 2020 MinIO, Inc.
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


// Note: YOUR-ACCESSKEYID, YOUR-SECRETACCESSKEY, my-bucketname
// and my-objectname are dummy values, please replace them with original values.

/**
 * 导入客户端变量
 */
var {s3Client,s3ClientV4} = require('./getS3Client')

// Get stat information for my-objectname.
s3Client.statObject('ncoss-4js', 'string3.txt', function(e, stat) {
  if (e) {
    return console.log(e)
  }
  console.log(stat)
})

// Get stat information for a specific version of 'my-objectname'
//Bucket must be versioning enabled.
// s3Client.statObject('ncoss-4js', 'string3.txt', {versionId:"b4be6b04430d11eeb3a4fa163e2fcf06"},function(e, stat) {
//   if (e) {
//     return console.log(e)
//   }
//   console.log(stat)
// })
