package com.kk.ddd.support.grl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import lombok.Getter;

/**
 * <br>
 *
 * @author mm
 */
public class ClusterClient {

  protected final ConsistentHashRing ring = new ConsistentHashRing();
  private final ClusterClientConfig clientConfig;
  private final List<String> servers;
  private long lastCheckTimestamp;

  @Getter private volatile boolean running = true;

  public ClusterClient(ClusterClientConfig clientConfig) {
    this.clientConfig = clientConfig;
    this.servers =
        new ArrayList<>(
            Objects.requireNonNull(clientConfig.getServers(), "servers must not be null"));
  }

  private static String getServers(String address) {
    // todo.. RPC to cluster node, e.g. call ClusterServer.allMembers()
    return "";
  }

  /** Gracefully stop the client. Subclasses may override to add wake-up logic. */
  public void stop() {
    this.running = false;
  }

  protected void doCheckServer() {
    if (!running
        || System.currentTimeMillis() - lastCheckTimestamp < clientConfig.getCheckInterval()) {
      return;
    }
    // shuffle to randomize which server we query
    Collections.shuffle(this.servers);
    // pick a random server and fetch the live cluster member list via RPC (stub below)
    this.servers.stream()
        .map(ClusterClient::getServers)
        .filter(Predicate.not(String::isBlank))
        .findAny()
        .ifPresent(
            ret -> {
              ring.rebuild(ret);
              lastCheckTimestamp = System.currentTimeMillis();
            });
  }

  protected String findServer(String key) {
    return ring.findServer(key);
  }
}
