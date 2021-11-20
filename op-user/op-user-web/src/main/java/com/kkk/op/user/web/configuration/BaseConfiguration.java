package com.kkk.op.user.web.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.kkk.op.support.aspect.DegradedServiceAspect;
import com.kkk.op.support.bean.Kson;
import com.kkk.op.support.bean.WheelTimer;
import com.kkk.op.support.cache.RedisCache;
import com.kkk.op.support.distributed.CuratorDistributedLocker;
import com.kkk.op.support.interceptor.IPControlInterceptor;
import com.kkk.op.support.interceptor.LocalRequestInterceptor;
import com.kkk.op.support.interceptor.ThreadLocalRemoveInterceptor;
import com.kkk.op.support.marker.DistributedLocker;
import com.kkk.op.support.marker.EntityCache;
import java.util.concurrent.TimeUnit;
import javax.validation.Validation;
import javax.validation.Validator;
import org.apache.curator.framework.CuratorFramework;
import org.hibernate.validator.HibernateValidator;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * todo... 待优化
 *
 * @author KaiKoo
 */
@Configuration
public class BaseConfiguration implements WebMvcConfigurer {

  // todo... 配合nacos配置中心实时刷新
  @Value("${ipControl.switch:true}")
  private boolean ipControlSwitch;

  // 注入IPControlInterceptor实现自动刷新配置
  @Bean
  public IPControlInterceptor ipControlInterceptor() {
    return new IPControlInterceptor(ipControlSwitch);
  }

  // 拦截器配置
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(ipControlInterceptor()).addPathPatterns("/api/**"); // 最先执行
    registry.addInterceptor(new LocalRequestInterceptor()).addPathPatterns("/api/**");
    registry.addInterceptor(new ThreadLocalRemoveInterceptor()).addPathPatterns("/api/**"); // 最后执行
  }

  // 配置分布式可重入锁bean
  @Bean
  public DistributedLocker distributedLock(CuratorFramework curatorFramework) {
    return CuratorDistributedLocker.builder().client(curatorFramework).build();
  }

  // 配置CacheManager
  @Bean
  public EntityCache cacheManager(RedissonClient redissonClient) {
    return RedisCache.builder()
        .name("RedisCache")
        .redissonClient(redissonClient)
        .redissonCacheConfig(new CacheConfig(1000L * 60L, 1000L * 60L * 30L))
        .objectMapper(jsonMapper())
        .build();
  }

  // 配置jakarta-validator-bean，校验快速失败
  @Bean
  public Validator validator() {
    // 指定HibernateValidator，并设置快速失败参数
    // Validation.buildDefaultValidatorFactory().getValidator(); 配置默认Validator
    return Validation.byProvider(HibernateValidator.class)
        .configure()
        .failFast(false)
        .buildValidatorFactory()
        .getValidator();
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
        .enable(Feature.WRITE_BIGDECIMAL_AS_PLAIN)
        .build();
  }

  // 直接使用定义的jsonMapper，不会重复创建bean。
  @Bean
  public Kson kson() {
    return new Kson(jsonMapper());
  }

  @Bean
  public DegradedServiceAspect degradedServiceAspect() {
    return new DegradedServiceAspect(3);
  }

  @Bean
  public WheelTimer wheelTimer() {
    return new WheelTimer(50, TimeUnit.MILLISECONDS);
  }
}
