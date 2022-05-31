package com.kk.ddd.support.configuration;

import com.kk.ddd.support.annotation.LiteConfiguration;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Curator Client 自动配置类 <br>
 *
 * @author KaiKoo
 */
@LiteConfiguration
@ConditionalOnClass(CuratorFramework.class) // Curator类（jar包）存在时才加载
@ConditionalOnProperty(name = "spring.zookeeper.curator.enabled", matchIfMissing = true)
@EnableConfigurationProperties(CuratorProperties.class) // 自动导入CuratorProperties配置类
public class CuratorAutoConfiguration {

  private final CuratorProperties properties;

  public CuratorAutoConfiguration(final CuratorProperties properties) {
    this.properties = properties;
  }

  @Bean
  @ConditionalOnMissingBean(CuratorFramework.class)
  public CuratorFramework curatorFramework() {
    var client =
        CuratorFrameworkFactory.builder()
            .connectString(properties.getConnectString())
            .namespace(properties.getNamespace())
            .connectionTimeoutMs(properties.getConnectionTimeoutMs()) // 连接超时时间
            .sessionTimeoutMs(properties.getSessionTimeoutMs()) // 客户端超时时间
            // 配置操作失败重试策略，重试指定的次数, 且每一次重试之间停顿的时间逐渐增加
            .retryPolicy(
                new ExponentialBackoffRetry(
                    properties.getRetryBaseSleepTimeMs(), properties.getMaxRetries()))
            // 关闭ensembleTracker，可以用于动态获取connectString
            .ensembleTracker(false)
            .build();
    // 启动客户端
    client.start();
    return client;
  }
}
