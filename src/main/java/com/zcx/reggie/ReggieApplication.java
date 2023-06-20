package com.zcx.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement    // 开启事务注解支持
@EnableCaching  // 开始缓存注解功能
public class ReggieApplication {
    public static void main(String[] args) {
        log.info("项目启动...");
        SpringApplication.run(ReggieApplication.class, args);
    }
}
