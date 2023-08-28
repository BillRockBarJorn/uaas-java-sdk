var NCOSS = require('../dist/main/ncoss')

/**
 * 导出ncoss-4.*的客户端连接，底层通过path是"/HOSv1"还是"/v1"来决定请求对应的ncoss服务端
 */
exports.s3ClientV4 = new NCOSS.Client({
    endPoint: '172.18.232.37',
    port: 8089,
    path: '/HOSv1',
    username: 'test_user1',
    password: 'TEST#ps@857',
    scopeName: 'test_pro1',
    uaasURL: 'http://172.18.232.192:6020/v3/auth/tokens',
    useSSL: false
})

/**
 * 导出ncoss-3.*的客户端连接，底层通过path是"/HOSv1"还是"/v1"来决定请求对应的ncoss服务端
 */
exports.s3Client = new NCOSS.Client({
    endPoint: '172.18.232.192',
    port: 8089,
    path: '/v1',
    username: 'test_user1',
    password: 'TEST#ps@857',
    scopeName: 'test_pro1',
    uaasURL: 'http://172.18.232.192:6020/v3/auth/tokens',
    useSSL: false
})
