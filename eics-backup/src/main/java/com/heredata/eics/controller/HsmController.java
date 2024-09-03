package com.heredata.eics.controller;

import com.heredata.eics.entity.oss.TbSwiftBackupFileTree;
import com.heredata.eics.service.HsmService;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/hsm")
public class HsmController {



    @Resource
    private HsmService hoervice;


    @SneakyThrows
    @GetMapping("/project/bucket")
    public boolean objectMigrate(String bucketName) {
        return hoervice.dataHier(bucketName);
    }



    @SneakyThrows
    @GetMapping("/project/object")
    public boolean objectMigrate(List<TbSwiftBackupFileTree> objects) {
        return hoervice.dataObj(objects);
    }


    @GetMapping("/data/account")
    public void accountMigrate() {
         hoervice.accountData();
    }

    @GetMapping("/buckets")
    public String buckets() {
        return hoervice.getBuckets();
    }


    @GetMapping("/local/dir/full")
    public void localFull() {
        dirDataService.uploadFull();
    }


}
