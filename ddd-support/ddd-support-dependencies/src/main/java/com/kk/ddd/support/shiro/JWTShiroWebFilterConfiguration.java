package com.kk.ddd.support.shiro;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.config.web.autoconfigure.ShiroWebFilterConfiguration;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.filter.mgt.DefaultFilter;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * shiro filter 配置类
 *
 * @author KaiKoo
 */
@EnableAutoConfiguration(exclude = ShiroWebFilterConfiguration.class)
@EnableConfigurationProperties({ShiroProperties.class, JWTShiroProperties.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(name = "shiro.web.enabled", matchIfMissing = true)
@RequiredArgsConstructor
public class JWTShiroWebFilterConfiguration {

  private final ShiroProperties shiroProperties;

  private final JWTShiroProperties jwtProperties;

  public static final String REGISTRATION_BEAN_NAME = "filterShiroFilterRegistrationBean";

  public static final String FILTER_NAME = "shiroFilter";

  public static final String GLOBAL_FILTERS_NAME = "globalFilters";

  @Bean(name = REGISTRATION_BEAN_NAME)
  @ConditionalOnMissingBean(name = REGISTRATION_BEAN_NAME)
  protected FilterRegistrationBean<AbstractShiroFilter> filterShiroFilterRegistrationBean(
      ShiroFilterFactoryBean shiroFilterFactoryBean) throws Exception {

    FilterRegistrationBean<AbstractShiroFilter> filterRegistrationBean =
        new FilterRegistrationBean<>();
    filterRegistrationBean.setDispatcherTypes(
        DispatcherType.REQUEST,
        DispatcherType.FORWARD,
        DispatcherType.INCLUDE,
        DispatcherType.ERROR);
    filterRegistrationBean.setFilter(shiroFilterFactoryBean.getObject());
    filterRegistrationBean.setName(FILTER_NAME);
    filterRegistrationBean.setOrder(1);

    return filterRegistrationBean;
  }

  @Bean(name = GLOBAL_FILTERS_NAME)
  @ConditionalOnMissingBean
  protected List<String> globalFilters() {
    return Collections.singletonList(DefaultFilter.invalidRequest.name());
  }

  @Bean
  @ConditionalOnMissingBean
  protected ShiroFilterFactoryBean shiroFilterFactoryBean(
      SecurityManager securityManager,
      @Qualifier(GLOBAL_FILTERS_NAME) List<String> globalFilters,
      ShiroFilterChainDefinition shiroFilterChainDefinition,
      @Autowired(required = false) Map<String, Filter> filterMap) {
    ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();

    filterFactoryBean.setLoginUrl(shiroProperties.getLoginUrl());
    filterFactoryBean.setSuccessUrl(shiroProperties.getSuccessUrl());
    filterFactoryBean.setUnauthorizedUrl(shiroProperties.getUnauthorizedUrl());

    filterFactoryBean.setSecurityManager(securityManager);
    filterFactoryBean.setGlobalFilters(globalFilters);
    filterFactoryBean.setFilterChainDefinitionMap(shiroFilterChainDefinition.getFilterChainMap());
    filterFactoryBean.setFilters(filterMap);
    // 注入自定义的过滤器，替换掉默认的authc过滤器，也可以自定义新的拦截类型
    filterFactoryBean.postProcessBeforeInitialization(
        new JWTAuthenticatingFilter(jwtProperties), DefaultFilter.authc.name());
    return filterFactoryBean;
  }
}
