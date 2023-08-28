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

/**
 * 导入客户端变量
 */
var {s3Client,s3ClientV4} = require('./getS3Client')

var ACL = {}
// 设置拥有者信息
ACL['OwnerID'] = '7c9dfff2139b11edbc330391d2a979b2'
// 设置权限控制集合
const accessControlList = [];

// 第一种类型权限控制
var accessControl1 = {};
// 授权人权限 READ | WRITE | READ_ACP | WRITE_ACP | FULL_CONTROL
accessControl1['Permission'] = 'READ'
// 设置授权信信息容器，只有两个值可选
//    1:CanonicalUser:规范用户需指定用户ID，与 元素 ID 一起使用
//    2:Group: 范围授权需指定元素URI，有效值为：
//              http://www.heredata.com/groups/global/AllUsers 代表所有人包括匿名用户
//              http://www.heredata.com/groups/global/AuthenticatedUsers 代表经过身份认证的所有用户。
accessControl1['GranteeType'] = 'CanonicalUser'
accessControl1['ID'] = '7c9dfff2139b11edbc330391d2a979b2'


// 第二种类型权限控制
var accessControl2 = {};
// 授权人权限 READ | WRITE | READ_ACP | WRITE_ACP | FULL_CONTROL
accessControl2['Permission'] = 'WRITE'
accessControl2['GranteeType'] = 'Group'
accessControl2['URI'] = 'http://www.heredata.com/groups/global/AllUsers'

// 将权限控制push到集合中
accessControlList.push(accessControl1)
accessControlList.push(accessControl2)
ACL['accessControlList'] = accessControlList

s3ClientV4.setObjectACL('nodejs', '2023/04/23/a.avi', ACL, function (err) {
  if (err) {
    return console.log(err)
  }
  console.log("Success")
})

// const ACLString = '<AccessControlPolicy><Owner><ID>7c9dfff2139b11edbc330391d2a979b2</ID></Owner><AccessControlList><Grant><Grantee xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="CanonicalUser"><ID>7c9dfff2139b11edbc330391d2a979b2</ID></Grantee><Permission>READ</Permission></Grant><Grant><Grantee xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="Group"><URI>http://www.heredata.com/groups/global/AllUsers</URI></Grantee><Permission>WRITE</Permission></Grant></AccessControlList></AccessControlPolicy>';
// client.setBucketACL('bucket', ACLString, function (err) {
//   if (err) {
//     return console.log(err)
//   }
//   console.log("Success")
// })
