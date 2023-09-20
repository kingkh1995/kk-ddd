package com.kk.ddd.user.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.kk.ddd.user.persistence") // 配置Mapper接口扫描路径（在接口上可以不添加@Mapper注解）
@SpringBootApplication(scanBasePackages = "com.kk.ddd")
public class UserWebApplication {

  public static void main(String[] args) {
    SpringApplication.run(UserWebApplication.class, args);
  }
}
