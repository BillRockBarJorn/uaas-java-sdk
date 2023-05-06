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

var NCOSS = require('../dist/main/ncoss')
var ObjectDomain = require('../dist/main/ObjectDomain')

// var client = new NCOSS.Client({
//   endPoint: '172.18.232.192',
//   port: 8089,
//   path: '/v1',
//   accessKey: 'EL5CG1OX6HEOSQ5YAJYL',
//   secretKey: 'ljCUFFBNwJCYDYzZnpcXYbWSc6L29aypEXJ36dJY',
//   accountId: '7c9dfff2139b11edbc330391d2a979b2'
// })

var client = new NCOSS.Client({
  endPoint: '172.18.232.192',
  port: 8089,
  path: '/v1',
  username: 'test_user1',
  password: 'TEST#ps@857',
  scopeName:'test_pro1',
  uaasURL:'http://172.18.232.192:6020/v3/auth/tokens'
})


// client.statObject("bucket", "2023/04/18/ccc.txt", function (e, dataStream) {
//   // let aa = Buffer.from(dataStream.getObjectContent()).toString('utf8');
//   // console.log('数据流：：'+aa);

//   console.log('最终结果',dataStream);
  
// })

var getObjectRequest = new ObjectDomain.GetObjectRequest({
  includeInputStream:true
});

// console.log(typeof getObjectRequest);
// console.log(getObjectRequest instanceof ObjectDomain.GetObjectRequest);

// console.log(typeof Buffer);

// var size = 0
// // Get a full object.
client.getObject('jssdk', '2023/04/20/NCOSSFileSystemStore.java', getObjectRequest, function (e, dataStream) {
  let aa = Buffer.from(dataStream.getObjectContent()).toString('utf8');
  console.log('数据流：：'+aa);
  
  console.log('其他数据信息',dataStream);
})

// //Get a specific version of an object
// var versionedObjSize = 0
// s3Client.getObject('my-versioned-bucket', 'my-versioned-object', {versionId:"my-versionId"}, function(err, dataStream) {
//   if (err) {
//     return console.log(err)
//   }
//   dataStream.on('data', function(chunk) {
//     versionedObjSize += chunk.length
//   })
//   dataStream.on('end', function() {
//     console.log('End. Total size = ' + versionedObjSize)
//   })
//   dataStream.on('error', function(err) {
//     console.log(err)
//   })
// })