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

/**
 * 导入客户端变量
 */
var {s3Client,s3ClientV4} = require('./getS3Client')
const ObjectMetadata = require('../dist/main/ObjectMetadata');

// Upload a stream
var file = 'E:\\比洛巴乔\\Desktop\\haha2.java'
var fileStream = Fs.createReadStream(file);

// 设置元数据
var objectMetadata = new ObjectMetadata();
objectMetadata.addUserMetadata('example', 'value');
objectMetadata.addUserMetadata('example2', 'vlaue2');

// var fileStat = Fs.stat(file, function (e, stat) {
//   if (e) {
//     return console.log(e)
//   }
//   s3ClientV4.putObject('ncoss-4js', 'haha22.java', fileStream, stat.size, objectMetadata, function (e,result) {
//     if (e) {
//       return console.log(e)
//     }
//     console.log(result);
//
//     console.log("Successfully uploaded the stream")
//   })
// })

// // Upload a buffer
// var buf = new Buffer(10)
// buf.fill('a')
// s3ClientV4.putObject('ncoss-4js', 'buffer.txt', buf, 'application/octet-stream', function(e) {
//   if (e) {
//     return console.log(e)
//   }
//   console.log("Successfully uploaded the buffer")
// })

// // Upload a string
// var str = "random string to be uploaded"
// s3ClientV4.putObject('ncoss-4js', 'string.txt', str, 'text/plain', function(e) {
//   if (e) {
//     return console.log(e)
//   }
//   console.log("Successfully uploaded the string")
// })


// Upload a Buffer without content-type (default: 'application/octet-stream')
var buf1 = new Buffer(10)
buf1.fill('a')
s3ClientV4.putObject('ncoss-4js', 'string3.txt', buf1, function(e) {
  if (e) {
    return console.log(e)
  }
  console.log("Successfully uploaded the Buffer")
})
