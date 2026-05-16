package com.kk.ddd.support.grl;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <br>
 *
 * @author mm
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TokenClientConfig extends ClusterClientConfig {
  private String key;
  private int permitsAcquiredPerRequest = 20;
  private int maxPermitsPerRequest = 200;
}
