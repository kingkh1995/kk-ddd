package com.kk.ddd.support.cache;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import java.util.concurrent.ForkJoinPool;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.core.Ordered;

/**
 * SpringCache切面配置类（基于Spring AOP）。 <br>
 * spring aop 基于动态代理（jdk基于接口，cglib基于类） aspectj基于字节码
 *
 * @see org.springframework.cache.annotation.CachingConfigurationSelector @EnableCaching实现
 * @author KaiKoo
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE) // 标识为spring内部使用的bean
public class EnhancedProxyCachingConfiguration {

  public static final String EVENT_BUS_BEAN_NAME = "enhancedCacheEventBus";

  @Bean
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  public BeanFactoryCacheOperationSourceAdvisor cacheAdvisor(
      CacheOperationSource cacheOperationSource, CacheInterceptor cacheInterceptor) {
    var advisor = new BeanFactoryCacheOperationSourceAdvisor();
    advisor.setCacheOperationSource(cacheOperationSource);
    advisor.setAdvice(cacheInterceptor);
    // 指定order
    advisor.setOrder(Ordered.LOWEST_PRECEDENCE);
    return advisor;
  }

  @Bean
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  public CacheOperationSource cacheOperationSource() {
    return new AnnotationCacheOperationSource();
  }

  @Bean
  @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
  public CacheInterceptor cacheInterceptor(
      CacheOperationSource cacheOperationSource,
      @Qualifier(EVENT_BUS_BEAN_NAME) EventBus eventBus) {
    var interceptor = new EnhancedCacheInterceptor(eventBus);
    interceptor.setCacheOperationSource(cacheOperationSource);
    return interceptor;
  }

  @Bean(EVENT_BUS_BEAN_NAME)
  @ConditionalOnMissingBean(name = EVENT_BUS_BEAN_NAME)
  public EventBus eventBus() {
    // 事件异步通知，线程池默认ForkJoinPool。
    return new AsyncEventBus(EVENT_BUS_BEAN_NAME, ForkJoinPool.commonPool());
  }
}
