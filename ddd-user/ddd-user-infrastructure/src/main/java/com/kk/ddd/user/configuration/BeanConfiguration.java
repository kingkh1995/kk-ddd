package com.kk.ddd.user.configuration;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.kk.ddd.support.annotation.LiteConfiguration;
import com.kk.ddd.support.aspect.DegradedServiceAspect;
import com.kk.ddd.support.bean.Jackson;
import com.kk.ddd.support.bean.NettyDelayer;
import com.kk.ddd.support.cache.CaffeineRedissonCompositeCacheManager;
import com.kk.ddd.support.cache.EnhancedProxyCachingConfiguration;
import com.kk.ddd.support.distributed.CuratorDistributedLockFactory;
import com.kk.ddd.support.distributed.DistributedLockFactory;
import com.kk.ddd.support.json.JsonJacksonCoder;
import java.util.concurrent.TimeUnit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.ibatis.session.SqlSessionFactory;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

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
    return Jackson.newJsonMapperBuilder().build();
  }

  @Bean
  /*public DistributedLockFactory distributedLockFactory(RedissonClient redissonClient) {
    return RedissonDistributedLockFactory.builder().client(redissonClient).build();
  }*/
  public DistributedLockFactory distributedLockFactory(CuratorFramework curatorFramework) {
    return CuratorDistributedLockFactory.builder().client(curatorFramework).build();
  }

  /**
   * 定义CacheManager
   *
   * @see CachingConfigurerSupport
   */
  @Bean
  public CacheManager cacheManager(RedissonClient redissonClient, JsonMapper jsonMapper) {
    var codec = new JsonJacksonCodec(jsonMapper);
    // redisson缓存配置 ttl:expireAfterWrite maxIdleTime:expireAfterAccess
    var redissonCacheManager =
        new RedissonSpringCacheManager(
            redissonClient, "classpath:config/redisson-cache.json", codec);
    // 配置内存缓存时间少于redis缓存时间
    var caffeineCacheManager = new CaffeineCacheManager();
    caffeineCacheManager.setCaffeine(
        Caffeine.newBuilder().expireAfterAccess(30L, TimeUnit.MINUTES).softValues());
    return new CaffeineRedissonCompositeCacheManager(
        redissonCacheManager,
        caffeineCacheManager,
        redissonClient.getTopic("CaffeineRedissonCompositeCacheTopic", codec),
        new JsonJacksonCoder(jsonMapper));
  }

  @Bean
  public NettyDelayer nettyDelayer() {
    return new NettyDelayer();
  }

  @Bean
  @RefreshScope
  public DegradedServiceAspect degradedServiceAspect(
      @Value("${degrade.health-interval:3}") int healthInterval) {
    return new DegradedServiceAspect(healthInterval);
  }
}
