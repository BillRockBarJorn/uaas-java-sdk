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

var NCOSS = require('../dist/main/ncoss')

// var ncossClient = new NCOSS.Client({
//   endPoint: '172.18.232.37',
//   port: 8089,
//   path: '/v1',
//   username: 'test_user1',
//   password: 'TEST#ps@857',
//   scopeName: 'test_pro1',
//   uaasURL: 'http://172.18.232.37:7079/v3/auth/tokens'
// })

var s3Client = new NCOSS.Client({
  endPoint: '172.18.232.192',
  port: 8089,
  path: '/v1',
  username: 'test_user1',
  password: 'TEST#ps@857',
  scopeName: 'test_pro1',
  uaasURL: 'http://172.18.232.192:6020/v3/auth/tokens'
})

// List all object paths in bucket my-bucketname.
var objectsStream = s3Client.listObjectsV2('bucket', '', true, '')
objectsStream.on('data', function (obj) {
  console.log(obj)
})
objectsStream.on('error', function (e) {
  console.log(e)
})