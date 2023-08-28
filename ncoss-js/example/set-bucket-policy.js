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

// Note: YOUR-ACCESSKEYID, YOUR-SECRETACCESSKEY, and my-bucketname
// are dummy values, please replace them with original values.

/**
 * 导入客户端变量
 */
var {s3Client,s3ClientV4} = require('./getS3Client')
// Bucket policy - GET requests on "testbucket" bucket will not need authentication.
var policy = `
{
    "Version": "2012-10-17",
    "Statement": [{
            "Sid": "sid",
            "Action": [
                "ListObjects","HeadBucket","PutBucketPolicy"
            ],
            "Effect": "Allow",
            "Resource": ["*"],
            "Principal": {
                "HWS": [
                    "7c9dfff2139b11edbc330391d2a979b2:root"
                ]
            }
        }
    ]
}
`

s3ClientV4.setBucketPolicy('nodejs', policy, (err) => {
	if (err) throw err

	console.log('Set bucket policy')
})
