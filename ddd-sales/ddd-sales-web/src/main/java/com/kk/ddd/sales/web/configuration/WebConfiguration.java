package com.kk.ddd.sales.web.configuration;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.kk.ddd.support.annotation.LiteConfiguration;
import com.kk.ddd.support.aspect.BaseControllerAspect;
import com.kk.ddd.support.bean.Jackson;
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
}
