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

// Note: YOUR-ACCESSKEYID, YOUR-SECRETACCESSKEY, my-testfile, my-bucketname
// and my-objectname are dummy values, please replace them with original values.

var NCOSS = require('../dist/main/ncoss')
const ObjectMetadata = require('../dist/main/ObjectMetadata');

var client = new NCOSS.Client({
  endPoint: '172.18.232.192',
  port: 8089,
  path: '/v1',
  username: 'test_user1',
  password: 'TEST#ps@857',
  scopeName: 'test_pro1',
  uaasURL: 'http://172.18.232.192:6020/v3/auth/tokens'
})

// 设置元数据
var objectMetadata = new ObjectMetadata();
objectMetadata.addUserMetadata('example', 'value');
objectMetadata.addUserMetadata('example2', 'vlaue2');

// Put a file in bucket my-bucketname.
var file = 'E:\\比洛巴乔\\Desktop\\aa.avi'
client.fPutObject('bucket', '2023/04/23/a.avi', file, function (e,res) {
  if (e) {
    return console.log(e)
  }
  console.log("Success!!!!",res)
})