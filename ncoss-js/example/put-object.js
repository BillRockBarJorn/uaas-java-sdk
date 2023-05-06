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

var Fs = require('fs')

var NCOSS = require('../dist/main/ncoss');
const ObjectMetadata = require('../dist/main/ObjectMetadata');

// var client = new NCOSS.Client({
//   endPoint: '172.18.232.192',
//   port: 8089,
//   path: '/v1',
//   accessKey: 'A20FDEGJZWYWWKIL5O56',
//   secretKey: 'dcOzt9VrOneTRdldueozZJIeVPN7zYAiBUgdhA8V',
//   accountId: '7c9dfff2139b11edbc330391d2a979b2'
// })

var client = new NCOSS.Client({
  endPoint: '172.18.232.192',
  port: 8089,
  path: '/v1',
  username: 'test_user1',
  password: 'TEST#ps@857',
  scopeName: 'test_pro1',
  uaasURL: 'http://172.18.232.192:6020/v3/auth/tokens'
})

// Upload a stream
var file = 'localFilePath'
var fileStream = Fs.createReadStream(file);

// 设置元数据
var objectMetadata = new ObjectMetadata();
objectMetadata.addUserMetadata('example', 'value');
objectMetadata.addUserMetadata('example2', 'vlaue2');

var fileStat = Fs.stat(file, function (e, stat) {
  if (e) {
    return console.log(e)
  }
  client.putObject('jssdk', '2023/04/26/NCOSSFileSystemStore2.avi', fileStream, stat.size, objectMetadata, function (e,result) {
    if (e) {
      return console.log(e)
    }
    console.log(result);
    
    console.log("Successfully uploaded the stream")
  })
})

// Upload a buffer
var buf = new Buffer(10)
buf.fill('a')
client.putObject('bucket', '2023/04/18/bbb.txt', buf, 'application/octet-stream', function(e) {
  if (e) {
    return console.log(e)
  }
  console.log("Successfully uploaded the buffer")
})

// Upload a string
var str = "random string to be uploaded"
client.putObject('bucket', '2023/04/18/ccc.txt', str, 'text/plain', function(e) {
  if (e) {
    return console.log(e)
  }
  console.log("Successfully uploaded the string")
})

// Upload a Buffer without content-type (default: 'application/octet-stream')
client.putObject('bucket', '2023/04/18/ddd.txt', buf, function(e) {
  if (e) {
    return console.log(e)
  }
  console.log("Successfully uploaded the Buffer")
})