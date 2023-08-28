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

import Crypto from 'crypto'
import _ from 'lodash'
import {
    uriEscape,
    getScope,
    isString,
    isObject,
    isArray,
    isNumber,
    makeDateShort,
    makeDateLong
} from './helpers.js'
import * as errors from './errors.js'

const signV4Algorithm = 'HmacSHA1'

// getCanonicalRequest generate a canonical request of style.
//
// canonicalRequest =
//  <HTTPMethod>\n
//  <CanonicalURI>\n
//  <CanonicalQueryString>\n
//  <CanonicalHeaders>\n
//  <SignedHeaders>\n
//  <HashedPayload>
//
function getCanonicalRequest(method, path, headers, signedHeaders, requestDate) {
    if (!isString(method)) {
        throw new TypeError('method should be of type "string"')
    }
    if (!isString(path)) {
        throw new TypeError('path should be of type "string"')
    }
    if (!isObject(headers)) {
        throw new TypeError('headers should be of type "object"')
    }
    if (!isArray(signedHeaders)) {
        throw new TypeError('signedHeaders should be of type "array"')
    }
    if (!requestDate && !isString(requestDate)) {
        throw new TypeError('requestDate should be of type "string"')
    }

    const headersKey = signedHeaders.sort();

    var headersArray = [];
    headersKey.forEach(item => {
        headersArray.push(item.toLowerCase() + ':' + headers[item])
    })

    // console.log(path.split('?'));

    const requestResource = path.split('?')[0]
    let requestQuery = path.split('?')[1]
    if (!requestQuery) requestQuery = ''

    if (requestQuery) {
        // requestQuery = 'uploadId=111111111111&partNumber=1&max-keys=1';
        // console.log(requestQuery);
        const arr = requestQuery.split('&');

        const needSign = ['acl', 'policy', 'quota', 'tagging', 'versioning', 'versionId', 'versions', 'lifecycle', 'uploads', 'restore', 'uploadId', 'partNumber']

        // console.log('arr:::' + arr);
        requestQuery = arr.filter(item => needSign.indexOf(item.split('=')[0]) != -1).sort().join('&');

        // console.log('requestQuery:::' + requestQuery);


        // requestQuery = requestQuery
        //   .split('&')
        //   .sort()
        //   .map(element => element.indexOf('=') === -1 ? element + '=' : element)
        //   .join('&')
    }

    const canonical = []

    // console.log('headers::' + JSON.stringify(headers));

    canonical.push(method.toUpperCase());
    canonical.push(headers['content-md5'] ? headers['content-md5'] : '')
    canonical.push(headers['content-type'] ? headers['content-type'] : '')
    canonical.push(requestDate)

    // console.log("headersArray：：：", headersArray);
    for (var i = 0; i < headersArray.length; i++) {
        if (headersArray[i].startsWith('x-hos-')) {
            canonical.push(headersArray[i])
        }
    }

    // canonical.push(requestQuery)
    canonical.push((requestResource.length == 0 ? '/' : requestResource) + (requestQuery.length == 0 ? '' : '?' + requestQuery));

    // canonical.push(headersArray.join('\n') + '\n')
    // canonical.push(signedHeaders.join(';').toLowerCase())
    // console.log(canonical);

    return canonical.join('\n')
}

// generate a credential string
function getCredential(accessKey, region, requestDate, serviceName = "s3") {
    if (!isString(accessKey)) {
        throw new TypeError('accessKey should be of type "string"')
    }
    if (!isString(region)) {
        throw new TypeError('region should be of type "string"')
    }
    if (!isObject(requestDate)) {
        throw new TypeError('requestDate should be of type "object"')
    }
    return `${accessKey}/${getScope(region, requestDate, serviceName)}`
}

// Returns signed headers array - alphabetically sorted
function getSignedHeaders(headers) {
    if (!isObject(headers)) {
        throw new TypeError('request should be of type "object"')
    }
    // Excerpts from @lsegal - https://github.com/aws/aws-sdk-js/issues/659#issuecomment-120477258
    //
    //  User-Agent:
    //
    //      This is ignored from signing because signing this causes problems with generating pre-signed URLs
    //      (that are executed by other agents) or when customers pass requests through proxies, which may
    //      modify the user-agent.
    //
    //  Content-Length:
    //
    //      This is ignored from signing because generating a pre-signed URL should not provide a content-length
    //      constraint, specifically when vending a S3 pre-signed PUT URL. The corollary to this is that when
    //      sending regular requests (non-pre-signed), the signature contains a checksum of the body, which
    //      implicitly validates the payload length (since changing the number of bytes would change the checksum)
    //      and therefore this header is not valuable in the signature.
    //
    //  Content-Type:
    //
    //      Signing this header causes quite a number of problems in browser environments, where browsers
    //      like to modify and normalize the content-type header in different ways. There is more information
    //      on this in https://github.com/aws/aws-sdk-js/issues/244. Avoiding this field simplifies logic
    //      and reduces the possibility of future bugs
    //
    //  Authorization:
    //
    //      Is skipped for obvious reasons

    const ignoredHeaders = ['authorization', 'user-agent', 'api-style', 'host', 'Date', 'content-length']
    return _.map(headers, (v, header) => header)
        .filter(header => ignoredHeaders.indexOf(header) === -1)
        .sort()
}

// returns the key used for calculating signature
function getSigningKey(secretKey, canonicalString) {
    if (!isString(secretKey)) {
        throw new TypeError('secretKey should be of type "string"')
    }
    const signature = Crypto.createHmac('sha1', secretKey);
    return signature.update(Buffer.from(canonicalString, 'utf-8')).digest('base64');
}

// returns the string that needs to be signed
function getStringToSign(canonicalRequest) {
    if (!isString(canonicalRequest)) {
        throw new TypeError('canonicalRequest should be of type "string"')
    }

    const stringToSign = []
    // stringToSign.push(signV4Algorithm)
    // stringToSign.push(requestDate)
    stringToSign.push(canonicalRequest);
    const signString = stringToSign.join('\n')
    return signString
}

// calculate the signature of the POST policy
export function postPresignSignatureV4(region, date, secretKey, policyBase64) {
    if (!isString(region)) {
        throw new TypeError('region should be of type "string"')
    }
    if (!isObject(date)) {
        throw new TypeError('date should be of type "object"')
    }
    if (!isString(secretKey)) {
        throw new TypeError('secretKey should be of type "string"')
    }
    if (!isString(policyBase64)) {
        throw new TypeError('policyBase64 should be of type "string"')
    }
    const signingKey = getSigningKey(date, region, secretKey)
    return Crypto.createHmac('sha256', signingKey).update(policyBase64).digest('hex').toLowerCase()
}

// Returns the authorization header
export function signV4(request, accessKey, secretKey) {
    if (!isObject(request)) {
        throw new TypeError('request should be of type "object"')
    }
    if (!isString(accessKey)) {
        throw new TypeError('accessKey should be of type "string"')
    }
    if (!isString(secretKey)) {
        throw new TypeError('secretKey should be of type "string"')
    }

    if (!accessKey) {
        throw new errors.AccessKeyRequiredError('accessKey is required for signing')
    }
    if (!secretKey) {
        throw new errors.SecretKeyRequiredError('secretKey is required for signing')
    }

    const requestDate = new Date().toGMTString();
    request.headers['Date'] = requestDate;
    request.headers['api-style'] = 'HOS'

    const signedHeaders = getSignedHeaders(request.headers);

    const canonicalRequest = getCanonicalRequest(request.method, request.path, request.headers, signedHeaders, requestDate)

    const stringToSign = getStringToSign(canonicalRequest)
    console.log('stringToSign:: \n' + stringToSign);
    const signingKey = getSigningKey(secretKey, Buffer.from(stringToSign, 'utf-8'))
    // const credential = getCredential(accessKey, requestDate)

    return 'HOS ' + accessKey + ':' + signingKey
}

export function signV4ByServiceName(request, accessKey, secretKey, region, requestDate, serviceName = "s3") {
    return signV4(request, accessKey, secretKey, region, requestDate, serviceName)
}

// returns a presigned URL string
export function presignSignatureV4(request, accessKey, secretKey, sessionToken, region, requestDate, expires) {
    if (!isObject(request)) {
        throw new TypeError('request should be of type "object"')
    }
    if (!isString(accessKey)) {
        throw new TypeError('accessKey should be of type "string"')
    }
    if (!isString(secretKey)) {
        throw new TypeError('secretKey should be of type "string"')
    }
    if (!isString(region)) {
        throw new TypeError('region should be of type "string"')
    }

    if (!accessKey) {
        throw new errors.AccessKeyRequiredError('accessKey is required for presigning')
    }
    if (!secretKey) {
        throw new errors.SecretKeyRequiredError('secretKey is required for presigning')
    }

    if (!isNumber(expires)) {
        throw new TypeError('expires should be of type "number"')
    }
    if (expires < 1) {
        throw new errors.ExpiresParamError('expires param cannot be less than 1 seconds')
    }
    if (expires > 604800) {
        throw new errors.ExpiresParamError('expires param cannot be greater than 7 days')
    }

    const iso8601Date = makeDateLong(requestDate)
    const signedHeaders = getSignedHeaders(request.headers)
    const credential = getCredential(accessKey, region, requestDate)
    const hashedPayload = 'UNSIGNED-PAYLOAD'

    const requestQuery = []
    requestQuery.push(`X-Amz-Algorithm=${signV4Algorithm}`)
    requestQuery.push(`X-Amz-Credential=${uriEscape(credential)}`)
    requestQuery.push(`X-Amz-Date=${iso8601Date}`)
    requestQuery.push(`X-Amz-Expires=${expires}`)
    requestQuery.push(`X-Amz-SignedHeaders=${uriEscape(signedHeaders.join(';').toLowerCase())}`)
    if (sessionToken) {
        requestQuery.push(`X-Amz-Security-Token=${uriEscape(sessionToken)}`)
    }

    const resource = request.path.split('?')[0]
    let query = request.path.split('?')[1]
    if (query) {
        query = query + '&' + requestQuery.join('&')
    } else {
        query = requestQuery.join('&')
    }

    const path = resource + '?' + query

    const canonicalRequest = getCanonicalRequest(request.method, path,
        request.headers, signedHeaders, hashedPayload)

    const stringToSign = getStringToSign(canonicalRequest, requestDate, region)
    const signingKey = getSigningKey(requestDate, region, secretKey)
    const signature = Crypto.createHmac('sha256', signingKey).update(stringToSign).digest('hex').toLowerCase()
    const presignedUrl = request.protocol + '//' + request.headers.host + path + `&X-Amz-Signature=${signature}`
    return presignedUrl
}
