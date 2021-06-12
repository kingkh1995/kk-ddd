package com.kkk.op.user.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * EnableOpenApi 为swagger3.0新增注解（此注解也可以不加，默认开启swagger功能） <br>
 * 依赖调整为 springfox-boot-starter 地址修改为 /swagger-ui/index.html & /v3/api-docs
 */
@EnableOpenApi
@ComponentScan("com.kkk.op.user") // 扫描所有模块组件
@MapperScan("com.kkk.op.user.persistence.mapper") // Mybatis扫描Mapper
@SpringBootApplication(exclude = RedisAutoConfiguration.class) // fixme... 暂时未开放redis功能
public class OpUserWebApplication {

  public static void main(String[] args) {
    SpringApplication.run(OpUserWebApplication.class, args);
  }
}
