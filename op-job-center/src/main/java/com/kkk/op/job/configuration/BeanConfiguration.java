package com.kkk.op.job.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.kkk.op.support.annotation.LiteConfiguration;
import com.kkk.op.support.cache.EnhancedProxyCachingConfiguration;
import com.kkk.op.support.distributed.JdbcDistributedLockFactory;
import com.kkk.op.support.marker.DistributedLockFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 基础层定义实现 <br>
 *
 * @author KaiKoo
 */
@LiteConfiguration
@Import(EnhancedProxyCachingConfiguration.class)
public class BeanConfiguration implements ApplicationContextAware {

  // 设置ApplicationContext构造完成后操作
  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {}

  // 配置ObjectMapper，使用JsonMapper（面向json的ObjectMapper子类）
  @Bean
  public JsonMapper jsonMapper() {
    return JsonMapper.builder()
        // 自动注册模块（jdk8Time类：JavaTimeModule）
        .findAndAddModules()
        // 序列化时只按属性
        .visibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
        .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        // 反序列化时忽略多余字段不失败
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        // 序列化时空对象不失败
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        // 序列化时日期不转为时间戳
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        // BigDecimal按plain方式序列化
        .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
        .build();
  }

  @Bean
  public DistributedLockFactory distributedLockFactory(
      PlatformTransactionManager transactionManager) {
    return JdbcDistributedLockFactory.builder().transactionManager(transactionManager).build();
  }
}
