package com.kk.ddd.user.web.configuration;

import com.kk.ddd.support.annotation.LiteConfiguration;
import com.kk.ddd.support.aspect.BaseControllerAspect;
import com.kk.ddd.support.bean.IpControlInterceptor;
import com.kk.ddd.support.bean.IpControlInterceptor.IPControlProperties;
import com.kk.ddd.support.bean.LocalRequestFilter;
import com.kk.ddd.support.bean.ThreadLocalRemoveInterceptor;
import javax.validation.Validation;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 接口层配置
 *
 * @author KaiKoo
 */
@LiteConfiguration
@EnableConfigurationProperties(IPControlProperties.class) // 加载IPControl配置
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

  // 消息总线会在收到远程配置变更事件后触发环境重新加载刷新配置类属性。
  private final IPControlProperties ipControlProperties;

  // 拦截器配置
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // 先添加的开始时先执行，结束时后执行
    registry.addInterceptor(new IpControlInterceptor(ipControlProperties)).addPathPatterns("/**");
    registry.addInterceptor(new ThreadLocalRemoveInterceptor()).addPathPatterns("/**");
  }

  // 添加LocalRequestFilter，匹配所有路径，且最先执行。
  @Bean
  protected FilterRegistrationBean<LocalRequestFilter> localRequestFilterRegistrationBean() {
    FilterRegistrationBean<LocalRequestFilter> filterRegistrationBean =
        new FilterRegistrationBean<>();
    filterRegistrationBean.setFilter(new LocalRequestFilter());
    filterRegistrationBean.setName("localRequestFilter");
    filterRegistrationBean.setOrder(Integer.MIN_VALUE);
    return filterRegistrationBean;
  }

  @Bean
  public BaseControllerAspect baseControllerAspect() {
    return new BaseControllerAspect();
  }

  @Bean
  @RefreshScope // 设置作用域为refresh，远程配置变更时，触发RefreshScope的bean缓存失效，获取时再重新加载。
  public Validator validator(@Value("${validator.fail-fast:false}") boolean failFast) {
    // 指定HibernateValidator，并设置快速失败参数
    try (var validatorFactory =
        Validation.byProvider(HibernateValidator.class)
            .configure()
            .failFast(failFast)
            .buildValidatorFactory(); ) {
      return validatorFactory.getValidator();
    }
  }
}
