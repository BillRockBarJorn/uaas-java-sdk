package com.heredata.eics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// 启动类
@EnableScheduling
@SpringBootApplication
public class EicsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EicsApplication.class, args);
        System.out.println("============启动成功====================");
    }

    @Bean
    public CommandLineRunner run(DirDataService dirDataService) {
        // 调用方法执行全量备份
        return args -> {dirDataService.fullFir();};
    }

}
