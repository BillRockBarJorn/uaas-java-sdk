// //引入类，暂时ES6标准中有import，但NodeJs还不支持
// var Point = require('../dist/main/point');
// //新建类对象
// var point = new Point(2, 3);
// //调用对象中的方法
// console.log(point.toString());
// //调用类中的静态函数
// console.log(Point.sayHello('Ence'));
// //调用类中的静态变量
// console.log(Point.para);


var NCOSS = require('../dist/main/ncoss')

var s3Client = new NCOSS.Client({
  endPoint: '172.18.232.192',
  port: 8089,
  path: '/v1',
  accessKey: 'A20FDEGJZWYWWKIL5O56',
  secretKey: 'dcOzt9VrOneTRdldueozZJIeVPN7zYAiBUgdhA8V',
  accountId: '7c9dfff2139b11edbc330391d2a979b2'
})

// s3Client.findUploadId('bucket','2022/08/3123/4.avi', function (err,res){
//   if (err) {
//     return console.log(err)
//   }
//   console.log(res)
//   console.log("Success")
// })


s3Client.listParts('bucket', '2022/08/3123/5.avi', 'f7f9231cdf2d11edac37c7c965b9af79', function (err, res) {
  if (err) {
    return console.log(err)
  }
  console.log(res)
  console.log("Success")
})