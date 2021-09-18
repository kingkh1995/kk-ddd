package com.kkk.op.user.web.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.kkk.op.support.bean.Kson;
import com.kkk.op.support.cache.MockCacheManager;
import com.kkk.op.support.distributed.MockDistributedLock;
import com.kkk.op.support.handler.IPControlInterceptor;
import com.kkk.op.support.handler.LocalRequestInterceptor;
import com.kkk.op.support.handler.ThreadLocalRemoveInterceptor;
import com.kkk.op.support.marker.CacheManager;
import com.kkk.op.support.marker.DistributedLock;
import javax.validation.Validation;
import javax.validation.Validator;
import org.hibernate.validator.HibernateValidator;
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
  @Value("${ip_control_switch:true}")
  private boolean ipControlSwtich;

  // 注入IPControlInterceptor实现自动刷新配置
  @Bean
  public IPControlInterceptor ipControlInterceptor() {
    return new IPControlInterceptor(ipControlSwtich);
  }

  // 拦截器配置
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(ipControlInterceptor()).addPathPatterns("/api/**"); // 最先执行
    registry.addInterceptor(new LocalRequestInterceptor()).addPathPatterns("/api/**");
    registry.addInterceptor(new ThreadLocalRemoveInterceptor()).addPathPatterns("/api/**"); // 最后执行
  }

  // 配置分布式可重入锁bean // fixme... 暂时Mock住
  @Bean
  public DistributedLock distributedLock() {
    return new MockDistributedLock();
  }
  /*    public DistributedLock distributedLock(
          @Autowired StringRedisTemplate stringRedisTemplate) {
      var builder = RedisDistributedLock.builder()
              .redisTemplate(stringRedisTemplate)
              .sleepInterval(200L);
      return builder.build();
  }*/

  // 配置CacheManager // fixme... 暂时Mock住
  @Bean
  public CacheManager cacheManager() {
    return new MockCacheManager();
  }

  // 配置jakarta-valiator-bean，校验快速失败
  @Bean
  public Validator validator() {
    // todo... debug时不快速失败 通过配置或开关控制
    return Validation.byProvider(HibernateValidator.class)
        .configure()
        .failFast(false)
        .buildValidatorFactory()
        .getValidator();
  }

  // 配置Jackson-bean 使用 JsonMapper 面向json的ObjectMapper子类
  @Bean
  public JsonMapper jsonMapper() {
    return JsonMapper.builder()
        // 自动注册模块（jdk8Time类：JavaTimeModule）
        .findAndAddModules()
        // 序列化时只按属性
        .visibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
        .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        // 反序列化时忽略多余字段 反序列化默认使用无参构造器
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        // 序列化时日期不转为时间戳
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .build();
  }

  @Bean
  public Kson kson() {
    return new Kson(jsonMapper());
  }
}
