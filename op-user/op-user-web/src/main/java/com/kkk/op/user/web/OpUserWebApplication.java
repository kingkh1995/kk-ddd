package com.kkk.op.user.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@ComponentScan("com.kkk.op.user")// 扫描所有组件
@MapperScan("com.kkk.op.user.persistence.mapper")// 扫描Mapper
@SpringBootApplication
public class OpUserWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpUserWebApplication.class, args);
    }

}
