package com.kk.ddd.support.grl;

import java.util.List;
import java.util.UUID;
import lombok.Data;

/**
 * <br>
 *
 * @author kingk
 */
@Data
public class ClusterServerConfig {
  private String alias = UUID.randomUUID().toString().replace("-", "");
  private String host;
  private int port;
  private int listenPort = 0;
  private List<String> servers;
  private int heartbeatInterval = 1_000;
}
