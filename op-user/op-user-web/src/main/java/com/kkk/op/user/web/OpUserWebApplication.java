package com.kkk.op.user.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi // swagger3.0新增注解（此注解不需要加，默认开启） 访问地址修改为 /swagger-ui/ & /v3/api-docs
@MapperScan("com.kkk.op.user.persistence.mapper") // Mybatis：配置Mapper接口扫描路径 或 在接口上添加@Mapper注解即可
@SpringBootApplication(
    scanBasePackages = "com.kkk.op",
    exclude = RedisAutoConfiguration.class) // fixme... 暂时未开放redis功能
public class OpUserWebApplication {

  public static void main(String[] args) {
    SpringApplication.run(OpUserWebApplication.class, args);
  }
}
