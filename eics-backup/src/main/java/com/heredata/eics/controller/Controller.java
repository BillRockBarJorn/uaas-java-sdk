package com.heredata.eics.controller;

import com.heredata.eics.service.Service;
import com.heredata.hos.model.HOSVersionSummary;
import com.heredata.hos.model.bucket.Bucket;
import com.sitech.cmap.fw.core.wsg.WsgPageResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/eics")
public class Controller {


    @Resource
    private Service service;

    /**
     * 测试接口连通性
     * @param a
     * @return
     */
    @GetMapping("/hello")
    public String hello(String a) {
        return "hello " + a;
    }

    @GetMapping("/listObject")
    public WsgPageResult<List<HOSVersionSummary>> objectList(@RequestParam(required = false) String bucketName
            , @RequestParam(required = false) String fileName
            , @RequestParam(required = false) String date
            , @RequestParam(required = false, defaultValue = "-1") int limit
            , @RequestParam(required = false, defaultValue = "-1") int page) {
        WsgPageResult<List<HOSVersionSummary>> listWsgPageResult = service.objectList(bucketName, fileName, date, limit, page);
        return listWsgPageResult;
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

    @GetMapping("/setBucketLife")
    public boolean setBucketLife(String bucketName) {
        return service.setBucketLife(bucketName);
    }


    @GetMapping("/upoadSingleObject")
    public boolean upoadSingleObject(String fileName) throws Throwable {
        return service.upoadSingleObject(fileName);
    }
}
