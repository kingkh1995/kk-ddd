package com.kkk.op.user.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi // swagger3.0新增注解（此注解不需要加，默认开启） 访问地址修改为 /swagger-ui/index.html & /v3/api-docs
@ComponentScan("com.kkk.op.user") // 扫描所有模块组件
@MapperScan(
    "com.kkk.op.user.persistence.mapper") // 配置Mybatis Mapper文件扫描路径 相应的xml映射文件要放在resources/mapper下
@SpringBootApplication(exclude = RedisAutoConfiguration.class) // fixme... 暂时未开放redis功能
public class OpUserWebApplication {

  public static void main(String[] args) {
    SpringApplication.run(OpUserWebApplication.class, args);
  }
}
