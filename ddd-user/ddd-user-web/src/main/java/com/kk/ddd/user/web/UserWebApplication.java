package com.kk.ddd.user.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @EnableOpenApi // swagger3.0新增注解（此注解不需要加，默认开启） 访问地址修改为 /swagger-ui/ & /v3/api-docs
@MapperScan("com.kk.ddd.user.persistence") // 配置Mapper接口扫描路径（在接口上可以不添加@Mapper注解）
@SpringBootApplication(scanBasePackages = "com.kk.ddd")
public class UserWebApplication {

  public static void main(String[] args) {
    SpringApplication.run(UserWebApplication.class, args);
  }
}