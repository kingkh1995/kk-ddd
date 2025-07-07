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
  @Getter private final String address;
  @Getter private final int memberSize;
  @Getter private final long maxRemoveTimeout;
  @Getter private volatile boolean running;

  public ClusterServer(ClusterServerConfig serverConfig) {
    cluster =
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
            .startAwait();
    running = true;
    log.info("start [{}] succeeded, address: {}.", cluster.member().alias(), cluster.address());
    address = cluster.address();
    memberSize = serverConfig.getServers().size();
    maxRemoveTimeout =
        ClusterMath.suspicionTimeout(
                MembershipConfig.DEFAULT_SUSPICION_MULT,
                memberSize,
                serverConfig.getHeartbeatInterval())
            + 3L * serverConfig.getHeartbeatInterval();
    log.info("The maxRemoveTimeout of this cluster is {}ms.", maxRemoveTimeout);
    Runtime.getRuntime().addShutdownHook(new Thread(this::leave));
  }

  public void leave() {
    cluster.shutdown();
    cluster.onShutdown().doOnSuccess(unused -> running = false).block();
  }

  public String allMembers() {
    if (!running) {
      return "NOT_RUNNING";
    }
    var members = cluster.members();
    var address = members.stream().map(Member::address).collect(Collectors.joining(","));
    if (members.size() <= memberSize / 2) {
      log.warn("[{}] left the cluster.", address);
      return "LEFT";
    }
    return address;
  }
}
