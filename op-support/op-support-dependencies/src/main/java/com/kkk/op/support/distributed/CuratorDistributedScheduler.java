package com.kkk.op.support.distributed;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;

/**
 * 基于zk选举机制的分布式定时执行器，每次只能由一个节点执行，直到无法再接受任务才会切换到其他节点。 <br>
 *
 * @author KaiKoo
 */
@Slf4j
public class CuratorDistributedScheduler {

  /** 基于分布式锁实现 */
  private final LeaderSelector selector;

  public CuratorDistributedScheduler(
      CuratorFramework client,
      String leaderPath,
      long takeInterval,
      TimeUnit takeIntervalUnit,
      Predicate<CuratorFramework> canTake,
      Consumer<CuratorFramework> doTake) {
    this.selector =
        new LeaderSelector(
            client,
            leaderPath,
            new LeaderSelectorListener() {
              @Override
              public void takeLeadership(CuratorFramework client) throws Exception {
                // 选举成功，获取到leader角色
                log.info("take at '{}'.", LocalDateTime.now());
                while (canTake.test(client)) {
                  doTake.accept(client);
                  Thread.sleep(takeIntervalUnit.toMillis(takeInterval));
                }
                log.info("give up at '{}'.", LocalDateTime.now());
                // 执行完毕，放弃leader角色
              }

              @Override
              public void stateChanged(CuratorFramework client, ConnectionState newState) {
                log.info("stage changed to {}.", newState);
                if (!newState.isConnected()) {
                  throw new IllegalStateException("curator client bad state!");
                }
              }
            });
  }

  public void start() {
    this.selector.start();
    // 自动重新参与选举
    this.selector.autoRequeue();
  }

  public void close() {
    this.selector.close();
  }
}
