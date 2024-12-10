package com.heredata.eics.controller;

import com.heredata.eics.service.Service;
import com.heredata.eics.utils.EicsUtils;
import com.heredata.hos.model.HOSVersionSummary;
import com.heredata.hos.model.LifecycleRule;
import com.heredata.hos.model.bucket.Bucket;
import com.sitech.cmap.fw.core.wsg.WsgPageResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/eics")
public class Controller {


    @Resource
    private Service service;

    @Value("${mailSubject}")
    private String mailSubject;

    /**
     * 测试接口连通性
     * @param a
     * @return
     */
    @GetMapping("/hello")
    public String hello(String a) {
        System.out.println(EicsUtils.unicodeToCN(mailSubject));

        return "hello " + a;
    }

    @GetMapping("/listObject")
    public String objectList(@RequestParam(required = false) String fileName
            , @RequestParam(required = false) String startTime
            , @RequestParam(required = false) String endTime
            , @RequestParam(required = false) String orderCondition) {
        List<HOSVersionSummary> listWsgPageResult = service.objectList(fileName, startTime, endTime, orderCondition);

        // 数据查询出来了，进行格式化输出处理，找到key名字最长长度和size最长长度
        int keyNameMaxLength = 0, sizeMaxLength = 0;
        for (HOSVersionSummary hosVersionSummary : listWsgPageResult) {
            keyNameMaxLength = Math.max(keyNameMaxLength, hosVersionSummary.getKey().length());
            sizeMaxLength = Math.max(sizeMaxLength, (hosVersionSummary.getSize() + "").length());
        }

        StringBuffer stringBuffer = new StringBuffer();
        for (HOSVersionSummary datum : listWsgPageResult) {
            stringBuffer.append("bucketName:").append(datum.getBucketName())
                    .append("    fileName:").append(datum.getKey());

            // 补齐keyName长度
            for (int i = 0; i < (keyNameMaxLength - datum.getKey().length()); i++) {
                stringBuffer.append(" ");
            }
            stringBuffer.append("    size:").append(datum.getSize());

            stringBuffer.append("\r\n");
        }
        return stringBuffer.toString();
    }

    @GetMapping("/deleteObject")
    public boolean deleteObject(String bucketName, String key, String versionId) {
        return service.deleteObject(bucketName, key, versionId);
    }


    @GetMapping("/downLoad")
    public boolean downLoadObject(String bucketName, String fileName, String filePath) {
        return service.downLoadObject(bucketName, fileName, filePath);
    }

    /**
     * 删除桶中的数据
     * @param bucket
     * @param isForce
     * @return
     */
    @DeleteMapping("/delete")
    public boolean deleteBukcet(String bucket, boolean isForce) {
        return service.deleteBucket(bucket, isForce);
    }

    /**
     * 查看桶详情
     * @param bucketName
     * @return
     */
    @GetMapping("/bucketInfo")
    public Bucket getBucketIno(String bucketName) {
        return service.getBucketInfo(bucketName);
    }

    /**
     * 上传指定日期的文件到对象存储中
     * @param date
     * @return
     */
    @GetMapping("/upload")
    public boolean upload(String date) {
        return service.upload(date);
    }

    @PostMapping("/setBucketLife")
    public boolean setBucketLife(@RequestBody List<String> list) {
        return service.setBucketLife(list);
    }

    @GetMapping("listBuckets")
    public List<Bucket> listBuckets() {
        return service.listBuckets();
    }

    @GetMapping("/getBucketLife")
    public Map<String, List<LifecycleRule>> getBucketLife() {
        return service.getBucketLife();
    }

    @GetMapping("/upoadSingleObject")
    public boolean upoadSingleObject(String fileName) throws Throwable {
        return service.upoadSingleObject(fileName);
    }

    @GetMapping("/deleteOldVersion")
    public boolean deleteOldVersion(String bucketName) throws Throwable {
        System.out.println("deleteOldVersion  start  bucketName:" + bucketName);
        return service.deleteOldVersion(bucketName);
    }

    @Deprecated
    @GetMapping("/writeOnFile")
    public boolean writeOnFile(Long fileSize) throws Throwable {
        return service.writeOnFile(fileSize);
    }
}
