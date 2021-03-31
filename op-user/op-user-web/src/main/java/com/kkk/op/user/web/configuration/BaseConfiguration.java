package com.kkk.op.user.web.configuration;

import com.kkk.op.support.bean.RedisDistributedReentrantLock;
import com.kkk.op.support.marker.DistributedReentrantLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * todo... 待优化
 * @author KaiKoo
 */
@Configuration
public class BaseConfiguration {

    // 配置分布式可重入锁bean
    @Bean
    public DistributedReentrantLock distributedReentrantLock(
            @Autowired StringRedisTemplate stringRedisTemplate) {
        var redisDistributedReentrantLock = new RedisDistributedReentrantLock(stringRedisTemplate);
        // 设置自定义参数
        redisDistributedReentrantLock.setMaxRetryTimes(2);
        return redisDistributedReentrantLock;
    }

}
