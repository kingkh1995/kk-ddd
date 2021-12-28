package com.kkk.op.user.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.kkk.op.support.aspect.DegradedServiceAspect;
import com.kkk.op.support.bean.Kson;
import com.kkk.op.support.bean.WheelTimer;
import com.kkk.op.support.cache.RedisCache;
import com.kkk.op.support.distributed.CuratorDistributedLockFactory;
import com.kkk.op.support.marker.Cache;
import com.kkk.op.support.marker.DistributedLockFactory;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.ibatis.session.SqlSessionFactory;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 基础层定义实现 <br>
 *
 * @author KaiKoo
 */
@Configuration
public class BeanConfiguration implements ApplicationContextAware {

  // 设置ApplicationContext构造完成后操作
  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    // 从spring容器获取sqlSessionFactory再获取到typeHandler注册器并添加typeHandler
    var typeHandlerRegistry =
        applicationContext
            .getBean(SqlSessionFactory.class)
            .getConfiguration()
            .getTypeHandlerRegistry();
  }

  // 配置ObjectMapper，使用JsonMapper（面向json的ObjectMapper子类）
  @Bean
  public JsonMapper jsonMapper() {
    return JsonMapper.builder()
        // 自动注册模块（jdk8Time类：JavaTimeModule）
        .findAndAddModules()
        // 序列化时只按属性
        .visibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
        .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        // 反序列化时忽略多余字段 反序列化默认使用无参构造器
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        // 序列化时日期不转为时间戳
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        // BigDecimal按plain方式序列化
        .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)
        .build();
  }

  @Bean
  /*public DistributedLockFactory distributedLockFactory(StringRedisTemplate stringRedisTemplate) {
    return RedisDistributedLockFactory.builder().redisTemplate(stringRedisTemplate).build();
  }*/
  /*public DistributedLockFactory distributedLockFactory(RedissonClient redissonClient) {
    return RedissonDistributedLockFactory.builder().client(redissonClient).build();
  }*/
  public DistributedLockFactory distributedLockFactory(CuratorFramework curatorFramework) {
    return CuratorDistributedLockFactory.builder().client(curatorFramework).build();
  }

  @Bean
  public Cache cache(RedissonClient redissonClient, JsonMapper jsonMapper) {
    return RedisCache.builder()
        .name("RedisCache")
        .redissonClient(redissonClient)
        .redissonCacheConfig(new CacheConfig(1000L * 60L * 30L, 1000L * 60L * 30L))
        .objectMapper(jsonMapper)
        .build();
  }

  @Bean
  public Kson kson(JsonMapper jsonMapper) {
    return new Kson(jsonMapper);
  }

  @Bean
  public WheelTimer wheelTimer() {
    return new WheelTimer(50, TimeUnit.MILLISECONDS);
  }

  @Bean
  @RefreshScope
  public DegradedServiceAspect degradedServiceAspect(
      @Value("${degrade.health-interval:3}") int healthInterval) {
    return new DegradedServiceAspect(healthInterval);
  }
}
