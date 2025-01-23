var NCOSS = require('../dist/main/ncoss')

/**
 * ncoss-4.*的客户端连接并使用临时秘钥，底层通过path是"/HOSv1"还是"/v1"来决定请求对应的ncoss服务端
 */
exports.s3ClientV4 = new NCOSS.Client({
    endPoint: '10.20.29.12',
    port: 8089,
    path: '/v1',
    username: 'oss_project-1737254683227',
    password: 'ede5b8ea2f1d46e48de5a4f662ae1f2b',
    scopeName: 'oss_project-1737254683227',
    uaasURL: 'http://10.20.29.10:12340/v3/auth/tokens',
    useSSL: false // false代表使用http请求，true代表https请求
})

/**
 * ncoss-4.*的客户端连接并使用永久秘钥，底层通过path是"/HOSv1"还是"/v1"来决定请求对应的ncoss服务端
 */
exports.s3ClientV4Key = new NCOSS.Client({
    endPoint: '172.18.232.37',
    port: 8089,
    path: '/HOSv1',
    accessKey: 'G59F5BRRZH6AH35OSJ7U',
    secretKey: 'teA7OhKT3KvEMj1jzN2jbyI50uUzBqdMGNcCVAbL',
    uaasURL: 'http://172.18.232.192:6020/v3/auth/tokens',
    useSSL: false // false代表使用http请求，true代表https请求
})

/**
 * 导出ncoss-3.*的客户端连接并使用临时秘钥，底层通过path是"/HOSv1"还是"/v1"来决定请求对应的ncoss服务端
 */
exports.s3Client = new NCOSS.Client({
    endPoint: '10.20.29.12',
    port: 8089,
    path: '/v1',
    username: 'oss_project-1737254683227',
    password: 'ede5b8ea2f1d46e48de5a4f662ae1f2b',
    scopeName: 'oss_project-1737254683227',
    uaasURL: 'http://10.20.29.10:12340/v3/auth/tokens',
    useSSL: false // false代表使用http请求，true代表https请求
})

/**
 * 导出ncoss-3.*的客户端连接并使用永久秘钥，底层通过path是"/HOSv1"还是"/v1"来决定请求对应的ncoss服务端
 */
exports.s3ClientKey = new NCOSS.Client({
    endPoint: '172.18.232.192',
    port: 8089,
    path: '/v1',
    accessKey: 'G59F5BRRZH6AH35OSJ7U',
    secretKey: 'teA7OhKT3KvEMj1jzN2jbyI50uUzBqdMGNcCVAbL',
    uaasURL: 'http://172.18.232.192:6020/v3/auth/tokens',
    useSSL: false // false代表使用http请求，true代表https请求
})
