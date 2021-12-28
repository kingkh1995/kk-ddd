package com.kkk.op.user.web.configuration;

import com.kkk.op.support.interceptor.IPControlInterceptor;
import com.kkk.op.support.interceptor.IPControlInterceptor.IPControlProperties;
import com.kkk.op.support.interceptor.LocalRequestInterceptor;
import com.kkk.op.support.interceptor.ThreadLocalRemoveInterceptor;
import javax.validation.Validation;
import javax.validation.Validator;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 接口层配置
 *
 * @author KaiKoo
 */
@Configuration
@EnableConfigurationProperties(IPControlProperties.class) // 加载IPControl配置
public class WebConfiguration implements WebMvcConfigurer {

  @Autowired private IPControlProperties ipControlProperties;

  // 拦截器配置
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // 先添加的开始时先执行，结束时后执行
    registry.addInterceptor(new IPControlInterceptor(ipControlProperties)).addPathPatterns("/**");
    registry.addInterceptor(new LocalRequestInterceptor()).addPathPatterns("/**");
    registry.addInterceptor(new ThreadLocalRemoveInterceptor()).addPathPatterns("/**");
  }

  @Bean
  public Validator validator(@Value("${validator.fail-fast:false}") boolean failFast) {
    // 指定HibernateValidator，并设置快速失败参数
    return Validation.byProvider(HibernateValidator.class)
        .configure()
        .failFast(failFast)
        .buildValidatorFactory()
        .getValidator();
  }
}
