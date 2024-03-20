package com.kk.ddd.sales.web.configuration;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.kk.ddd.support.access.AccessConditionAspect;
import com.kk.ddd.support.access.SpelAccessConditionChecker;
import com.kk.ddd.support.annotation.LiteConfiguration;
import com.kk.ddd.support.aspect.BaseControllerAspect;
import com.kk.ddd.support.aspect.QueryServiceAspect;
import com.kk.ddd.support.aspect.QueryServiceChecker;
import com.kk.ddd.support.bean.Jackson;
import com.kk.ddd.support.distributed.CuratorDistributedLockFactory;
import com.kk.ddd.support.distributed.DistributedLockFactory;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.context.annotation.Bean;

/**
 * <br>
 *
 * @author KaiKoo
 */
@LiteConfiguration
public class WebConfiguration {

  @Bean
  public JsonMapper jsonMapper() {
    return Jackson.newJsonMapperBuilder().build();
  }

  @Bean
  public BaseControllerAspect baseControllerAspect() {
    return new BaseControllerAspect();
  }

  @Bean
  public DistributedLockFactory distributedLockFactory(CuratorFramework curatorFramework) {
    return CuratorDistributedLockFactory.builder().client(curatorFramework).build();
  }

  @Bean
  public QueryServiceChecker queryServiceChecker() {
    return new SpelAccessConditionChecker();
  }

  @Bean
  public QueryServiceAspect queryServiceAspect(QueryServiceChecker checker) {
    return new QueryServiceAspect(checker);
  }

  @Bean
  public AccessConditionAspect accessConditionAspect() {
    return new AccessConditionAspect();
  }
}
