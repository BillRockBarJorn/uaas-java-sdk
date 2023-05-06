// import Http from 'http'
/**
 * 获取永久秘钥方式
 */

const {
  resolve
} = require('path');
const {
  reject
} = require('async');

/**
 *创建永久秘钥
 *
 * @param {*} username 用户名
 * @param {*} password 用户名密码
 * @param {*} scopeName 租户名
 * @param {*} ip UAAS系统IP
 * @param {*} port UAAS系统端口
 * @return {*} 
 */
function createPermanent(username, password, scopeName, ip, port) {
  var Http = require('http');
  return new Promise((resolve, reject) => {
    getToken(username, password, scopeName, ip, port).then(resolveobj => {
      const token = resolveobj['token'];

      var body = `{
          "AccessKey": {
            "user_name": "${username}",
            "project_id": "${resolveobj['scopeNameId']}",
            "description": "description"
          }
        }`

      options = {
        host: ip,
        port: port,
        path: '/v3/accesskeys',
        method: 'post',
        headers: {
          'Content-Type': 'application/json',
          'Content-Length': body.length,
          'Accept': 'application/json',
          'X-Auth-Token': token
        }
      };
      const aksk = Http.request(options, (res2) => {
        res2.on('data', (chunk) => {
          resolve(JSON.parse(`${chunk}`))
        });

        res2.on('error', error => {
          reject(error);
        })

      })

      aksk.write(body);
      aksk.end();
    })
  })
}

function getPermanent(username, password, scopeName, ip, port, accessKey) {
  var Http = require('http');
  return new Promise((resolve, reject) => {
    getToken(username, password, scopeName, ip, port).then(tokenObj => {
      var options = {
        host: ip,
        port: port,
        path: `/v3/accesskeys/${accessKey}`,
        method: 'get',
        headers: {
          'Content-Type': 'application/json',
          // 'Content-Length': body.length,
          'Accept': 'application/json',
          'X-Auth-Token': tokenObj['token']
        }
      };

      const aksk = Http.request(options, (res2) => {
        res2.on('data', (chunk) => {
          resolve(JSON.parse(`${chunk}`))
        });

        res2.on('error', error => {
          reject(error);
        })

      })

      // aksk.write(body);
      aksk.end();
    })
  })
}

function listPermanent(username, password, scopeName, ip, port) {
  var Http = require('http');
  return new Promise((resolve, reject) => {

    getToken(username, password, scopeName, ip, port).then(result => {
      options = {
        host: ip,
        port: port,
        path: `/v3/accesskeys`,
        method: 'get',
        headers: {
          'Content-Type': 'application/json;charset=utf8',
          'X-Auth-Token': result.token
        }
      };

      const aksk = Http.request(options, (res2) => {
        res2.on('data', (chunk) => {
          resolve(JSON.parse(`${chunk}`))
        });

        res2.on('error', error => {
          reject(error)
          throw new Error(error);
        })

      })

      aksk.end();
    })
  })
}


function deletrAccessKey(username, password, scopeName, ip, port, accessKey) {
  var Http = require('http');
  getToken(username, password, scopeName, ip, port).then(resolve => {
    var options = {
      host: ip,
      port: port,
      path: `/v3/accesskeys/${accessKey}`,
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'X-Auth-Token': resolve['token']
      }
    };

    const req = Http.request(options, (res) => {
      res.setEncoding('utf8');

      res.on('end', () => {
      });
    });

    req.on('error', (e) => {
      reject(e)
    });

    req.end();

  })
}


function getToken(username, password, scopeName, ip, port) {
  var Http = require('http');
  return new Promise((resolve, reject) => {
    var body = `{
      "auth": {
          "identity": {
              "methods": ["password"],
              "password": {
                  "user": {
                      "name": "${username}",
                      "domain": {
                          "name": "default"
                      },
                      "password": "${password}"
                  }
              }
          },
          "scope": {
              "project": {
                  "domain": {
                      "name": "default"
                  },
                  "name": "${scopeName}"
              }
          }
      }
  }`


    var options = {
      host: ip,
      port: port,
      path: '/v3/auth/tokens',
      method: 'post',
      headers: {
        'Content-Type': 'application/json',
        'Content-Length': body.length
      }
    };
    var scopeNameId = '';
    const req = Http.request(options, (res) => {
      res.setEncoding('utf8');
      res.on('data', (chunk) => {
        const sss = JSON.parse(`${chunk}`);
        scopeNameId = sss.token.project.id;
      });
      res.on('end', () => {
        const token = res['headers']['x-subject-token'];
        resolve({
          scopeNameId,
          token
        })
      });
    });

    req.on('error', (e) => {
      reject(e)
    });

    req.write(body);
    req.end();
  })
}


module.exports = {
  getPermanent,
  getToken,
  createPermanent,
  listPermanent,
  deletrAccessKey
};

// getToken('test_user1', 'TEST#ps@857', 'test_pro1', '172.18.232.192', '6020')

// getPermanent('test_user1', 'TEST#ps@857', 'test_pro1', '172.18.232.192', '6020', 'Q95JS0HVER520TEBME0P').then(result => console.log(result));

// deletrAccessKey('test_user1', 'TEST#ps@857', 'test_pro1', '172.18.232.192', '6020', '1P05QDBK7OGJ7DE71LLJ')

// listPermanent('test_user1', 'TEST#ps@857', 'test_pro1', '172.18.232.192', '6020').then(resolve => console.log('结果：：' + JSON.stringify(resolve)))
// createPermanent('test_user1', 'TEST#ps@857', 'test_pro1', '172.18.232.192', '6020').then(res => {
//   console.log(res);
// })