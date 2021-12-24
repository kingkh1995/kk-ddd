package com.kkk.op.support.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Curator 配置 <br>
 *
 * @author KaiKoo
 */
@Data
@ConfigurationProperties("spring.zookeeper.curator")
public class CuratorProperties {
  private String connectString = "localhost:2181";
  private String namespace;
  private int connectionTimeoutMs = 15 * 1000;
  private int sessionTimeoutMs = 60 * 1000;
  private int retryBaseSleepTimeMs = 1000;
  private int maxRetries;
}
