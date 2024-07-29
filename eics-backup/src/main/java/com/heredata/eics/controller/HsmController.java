package com.heredata.eics.controller;

import com.heredata.eics.service.HsmService;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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


    @GetMapping("/data/account")
    public void accountMigrate() {
         hoervice.accountData();
    }

    @GetMapping("/buckets")
    public String buckets() {
        return hoervice.getBuckets();
    }


}
