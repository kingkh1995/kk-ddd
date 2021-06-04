package com.kkk.op.user.web.configuration;

import com.kkk.op.support.bean.IPControlInterceptor;
import com.kkk.op.support.bean.ThreadLocalRemoveInterceptor;
import com.kkk.op.support.marker.DistributedLock;
import com.kkk.op.support.mock.MockDistributedLock;
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
 * @author KaiKoo
 */
@Configuration
public class BaseConfiguration implements WebMvcConfigurer {

    //todo... 配合nacos配置中心实时刷新
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
        registry.addInterceptor(ipControlInterceptor())
                .addPathPatterns("/api/**"); // 最先执行
        registry.addInterceptor(new ThreadLocalRemoveInterceptor())
                .addPathPatterns("/api/**"); // 最后执行
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

    //配置valiator快速失败
    @Bean
    public Validator validator() {
        // todo... debug时设置为不快速失败 通过配置或开关控制
        return Validation.byProvider(HibernateValidator.class).configure()
                .failFast(false).buildValidatorFactory().getValidator();
    }

}
