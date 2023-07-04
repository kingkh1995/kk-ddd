package com.kk.ddd.job.configuration;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.kk.ddd.support.annotation.LiteConfiguration;
import com.kk.ddd.support.aspect.BaseControllerAspect;
import com.kk.ddd.support.bean.Jackson;
import com.kk.ddd.support.distributed.DistributedLockFactory;
import com.kk.ddd.support.distributed.JdbcDistributedLockFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 基础层定义实现 <br>
 *
 * @author KaiKoo
 */
@LiteConfiguration
public class BeanConfiguration {

  @Bean
  public JsonMapper jsonMapper() {
    return Jackson.newJsonMapperBuilder().build();
  }

  @Bean
  public DistributedLockFactory distributedLockFactory(
      PlatformTransactionManager transactionManager) {
    return JdbcDistributedLockFactory.builder().transactionManager(transactionManager).build();
  }

  @Bean
  public BaseControllerAspect baseControllerAspect() {
    return new BaseControllerAspect();
  }
}
