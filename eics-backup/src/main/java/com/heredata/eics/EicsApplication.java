package com.heredata.eics;

import com.heredata.eics.service.DirDataService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

// 启动类
@EnableScheduling
@SpringBootApplication
public class EicsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EicsApplication.class, args);
        System.out.println("============启动成功====================");
    }

   /* @Bean
    public CommandLineRunner run(DirDataService dirDataService) {
        // 调用方法执行全量备份
        return args -> {dirDataService.fullFir();};
    }*/

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) {
                // 这里可以获取jar包的基本信息，例如版本号、名称等
                // 使用HTTP请求将信息发送到平台

            }
        };
    }

}
