package com.kkk.op.user.web.configuration;

import com.kkk.op.support.bean.RedisDistributedLock;
import com.kkk.op.support.marker.DistributedLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * todo... 待优化
 * @author KaiKoo
 */
@Configuration
public class BaseConfiguration {

    // 配置分布式可重入锁bean // fixme... 暂时未开放redis功能
//    @Bean
    public DistributedLock distributedLock(
            @Autowired StringRedisTemplate stringRedisTemplate) {
        var builder = RedisDistributedLock.builder()
                .redisTemplate(stringRedisTemplate)
                .sleepInterval(200L);
        return builder.build();
    }

}