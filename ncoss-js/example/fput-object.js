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
const ObjectMetadata = require('../dist/main/ObjectMetadata');
/**
 * 导入客户端变量
 */
var {s3Client, s3ClientV4} = require('./getS3Client')

// 设置元数据
var objectMetadata = new ObjectMetadata();
objectMetadata.addUserMetadata('example', 'value');
objectMetadata.addUserMetadata('example2', 'vlaue2');

// Put a file in bucket my-bucketname.
var file = 'E:\\比洛巴乔\\Desktop\\haha2.java'
s3ClientV4.fPutObject('nodejs', '2023/04/23/a.avi', file, function (e, res) {
    if (e) {
        return console.log(e)
    }
    console.log("Success!!!!", res)
})
