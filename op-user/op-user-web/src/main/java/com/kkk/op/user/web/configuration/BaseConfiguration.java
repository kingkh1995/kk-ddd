package com.kkk.op.user.web.configuration;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.kkk.op.support.bean.IPControlInterceptor;
import com.kkk.op.support.bean.ThreadLocalRemoveInterceptor;
import com.kkk.op.support.marker.CacheManager;
import com.kkk.op.support.marker.DistributedLock;
import com.kkk.op.support.mock.MockCacheManager;
import com.kkk.op.support.mock.MockDistributedLock;
import javax.validation.Validation;
import javax.validation.Validator;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * todo... 待优化
 *
 * @author KaiKoo
 */
@Configuration
public class BaseConfiguration implements WebMvcConfigurer {

  // Mybatis-Plus插件
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    var interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor()); // 添加分页插件
    return interceptor;
  }

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

  // 配置valiator快速失败
  @Bean
  public Validator validator() {
    // todo... debug时设置为不快速失败 通过配置或开关控制
    return Validation.byProvider(HibernateValidator.class)
        .configure()
        .failFast(false)
        .buildValidatorFactory()
        .getValidator();
  }

  // 配置文件上传MultipartFile解析器 使用commons-fileupload方式 文件达到一定大小会被解析到指定临时目录
  // max-file-size和max-size 默认都是-1 表示无限制（前端ngnix也要配置client_max_body_size否则会抛出413异常）
  @Bean
  public MultipartResolver multipartResolver() {
    var commonsMultipartResolver = new CommonsMultipartResolver();
    commonsMultipartResolver.setResolveLazily(true); // 设置文件懒解析 默认值为非懒解析
    //    commonsMultipartResolver.setUploadTempDir(); // 设置文件的临时目录 默认为系统变量java.io.tempdir的值
    commonsMultipartResolver.setMaxInMemorySize(10240); // 设置文件上传至内存阈值 默认超出10kb则解析到磁盘
    return commonsMultipartResolver;
  }
}
