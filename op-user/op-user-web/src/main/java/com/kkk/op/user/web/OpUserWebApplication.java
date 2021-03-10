package com.kkk.op.user.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * swagger3.0新增（此注解也可以不加），默认开启swagger功能
 * 依赖调整为 springfox-boot-starter
 * 地址修改为 /swagger-ui/index.html & /v3/api-docs
 */
@EnableOpenApi
@ComponentScan("com.kkk.op.user")// 扫描所有组件
@MapperScan("com.kkk.op.user.persistence.mapper")// 扫描Mapper
@SpringBootApplication
public class OpUserWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpUserWebApplication.class, args);
    }

}
