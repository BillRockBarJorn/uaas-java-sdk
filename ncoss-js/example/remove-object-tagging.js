
/*
 * MinIO Javascript Library for Amazon S3 Compatible Cloud Storage, (C) 2021 MinIO, Inc.
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

var NCOSS = require('../dist/main/ncoss')

var client = new NCOSS.Client({
  endPoint: '172.18.232.192',
  port: 8089,
  path: '/v1',
  accessKey: 'H4OAXD042VFNDV13UNEC',
  secretKey: 'KLkG5MdKTO7lAp19zOWYeFcVkEKhicZzAoZ8Ip5j',
  accountId: '7c9dfff2139b11edbc330391d2a979b2'
})

client.removeObjectTagging("bucket", "2023/04/18/ccc.txt", function (err){
  if (err) {
    return console.log(err)
  }
  console.log("Success")
})

// //remove tags on a version of an object
// client.removeObjectTagging('bucketname', 'object-name', { versionId: "my-object-version-id" }, function (err){
//   if (err) {
//     return console.log(err)
//   }
//   console.log("Success")
// })