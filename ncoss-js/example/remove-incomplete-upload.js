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


  // Note: YOUR-ACCESSKEYID, YOUR-SECRETACCESSKEY, my-bucketname and
  // my-objectname are dummy values, please replace them with original values.

  var NCOSS = require('../dist/main/ncoss')

  var s3Client = new NCOSS.Client({
    endPoint: '172.18.232.192',
    port: 8089,
    path: '/v1',
    accessKey: 'A20FDEGJZWYWWKIL5O56',
    secretKey: 'dcOzt9VrOneTRdldueozZJIeVPN7zYAiBUgdhA8V',
    accountId: '7c9dfff2139b11edbc330391d2a979b2'
  })
// Remove a partially uploaded object name my-objectname.
s3Client.removeIncompleteUpload('my-bucketname', 'my-objectname', function(e) {
  if (e) {
    return console.log(e)
  }
  console.log("Success")
})
