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


// Note: YOUR-ACCESSKEYID, YOUR-SECRETACCESSKEY, my-bucketname and
// my-objectname are dummy values, please replace them with original values.

/**
 * 导入客户端变量
 */
var {s3Client,s3ClientV4} = require('./getS3Client')

// // Remove an object name my-objectname.
// s3ClientV4.removeObject('ncoss-4js', 'string3.txt', function(e) {
//   if (e) {
//     return console.log(e)
//   }
//   console.log("Success")
// })

// Remove an object with name 'my-objectname' and a versionId.
s3ClientV4.removeObject('nodejs', '2023/04/23/a.avi', {versionId:"e209392a457111eeb3a4fa163e2fcf06"}, function(e) {
  if (e) {
    return console.log(e)
  }
  console.log("Success")
})
