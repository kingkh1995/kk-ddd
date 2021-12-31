package com.kkk.op.support.cache;

import org.springframework.beans.factory.config.BeanDefinition;
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
  public CacheInterceptor cacheInterceptor(CacheOperationSource cacheOperationSource) {
    var interceptor = new EnhancedCacheInterceptor();
    interceptor.setCacheOperationSource(cacheOperationSource);
    return interceptor;
  }
}
