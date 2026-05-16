package com.kk.ddd.support.grl;

import io.scalecube.cluster.Cluster;
import io.scalecube.cluster.ClusterImpl;
import io.scalecube.cluster.ClusterMath;
import io.scalecube.cluster.ClusterMessageHandler;
import io.scalecube.cluster.Member;
import io.scalecube.cluster.membership.MembershipConfig;
import io.scalecube.cluster.membership.MembershipEvent;
import io.scalecube.cluster.transport.api.Message;
import io.scalecube.transport.netty.tcp.TcpTransportFactory;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * <br>
 *
 * @author mm
 */
@Slf4j
public class ClusterServer {

  private final Cluster cluster;
  private final ClusterServerConfig serverConfig;

  @Getter private final String address;
  @Getter private final long maxRemoveTimeout;
  private final AtomicBoolean running = new AtomicBoolean();

  public ClusterServer(ClusterServerConfig serverConfig) {
    this.serverConfig = serverConfig;
    this.cluster =
        new ClusterImpl()
            .config(
                clusterConfig -> {
                  clusterConfig = clusterConfig.memberAlias(serverConfig.getAlias());
                  if (serverConfig.getHost() != null) {
                    clusterConfig = clusterConfig.externalHost(serverConfig.getHost());
                  }
                  if (serverConfig.getPort() > 0) {
                    clusterConfig = clusterConfig.externalPort(serverConfig.getPort());
                  }
                  return clusterConfig;
                })
            .membership(membershipConfig -> membershipConfig.seedMembers(serverConfig.getServers()))
            .failureDetector(
                failureDetectorConfig -> {
                  if (serverConfig.getHeartbeatInterval() > 0) {
                    failureDetectorConfig =
                        failureDetectorConfig.pingInterval(serverConfig.getHeartbeatInterval());
                  }
                  return failureDetectorConfig;
                })
            .transport(transportConfig -> transportConfig.port(serverConfig.getListenPort()))
            .transportFactory(TcpTransportFactory::new)
            .handler(
                cc ->
                    new ClusterMessageHandler() {
                      @Override
                      public void onMessage(Message message) {
                        log.info("[{}] onMessage: {}.", cc.member().alias(), message);
                      }

                      @Override
                      public void onGossip(Message gossip) {
                        log.info("[{}] onGossip: {}.", cc.member().alias(), gossip);
                      }

                      @Override
                      public void onMembershipEvent(MembershipEvent event) {
                        log.info("[{}] onMembershipEvent: {}.", cc.member().alias(), event);
                      }
                    })
            .start().block(Duration.ofSeconds(30));
    running.set(true);
    log.info("start [{}] succeeded, address: {}.", cluster.member().alias(), cluster.address());
    address = cluster.address();
    maxRemoveTimeout =
        ClusterMath.suspicionTimeout(
                MembershipConfig.DEFAULT_SUSPICION_MULT,
                serverConfig.getServers().size(),
                serverConfig.getHeartbeatInterval())
            + 3L * serverConfig.getHeartbeatInterval();
    log.info("The maxRemoveTimeout of this cluster is {}ms.", maxRemoveTimeout);
    Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
  }

  public boolean isRunning() {
    return running.get();
  }

  public void stop() {
    if (running.compareAndSet(true, false)) {
      cluster.shutdown();
      cluster.onShutdown().block();
    }
  }

  public ClusterMembersResult allMembers() {
    if (!running.get()) {
      return ClusterMembersResult.notRunning();
    }
    var members = cluster.members();
    List<String> addressList = members.stream().map(Member::address).collect(Collectors.toList());
    // majority-quorum: if remaining members <= half of expected (configured) cluster size
    if (members.size() <= serverConfig.getServers().size() / 2) {
      log.warn("[{}] left the cluster.", addressList);
      return ClusterMembersResult.left(addressList);
    }
    return ClusterMembersResult.running(addressList);
  }
}
