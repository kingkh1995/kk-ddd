package com.kkk.op.support.distributed;

import java.util.UUID;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatch.State;

/**
 * 可靠的zk选举，每次执行任务前或定时判断是否当选成功。 <br>
 *
 * @author KaiKoo
 */
@Slf4j
public class ReliableLeaderLatch {

  private final CuratorFramework client;

  @Getter private final String path;

  @Getter private final String id;

  private LeaderLatch latch;

  public ReliableLeaderLatch(CuratorFramework client, String path) {
    this(client, path, UUID.randomUUID().toString());
  }

  public ReliableLeaderLatch(CuratorFramework client, String path, String id) {
    this.client = client;
    this.path = path;
    this.id = id;
  }

  public void start() throws Exception {
    if (latch == null) {
      latch = new LeaderLatch(client, path, id);
      latch.start();
    }
  }

  public void close() throws Exception {
    if (latch != null) {
      latch.close();
      latch = null;
    }
  }

  private void requeue() throws Exception {
    close();
    start();
  }

  /** 原hasLeadership()方法不可靠，是直接返回属性hasLeadership的值，该值的修改是在收到连接状态改变事件后修改，而事件有丢失的可能。 */
  public boolean hasLeadership() throws Exception {
    if (latch == null || !State.STARTED.equals(latch.getState())) {
      throw new IllegalStateException("not started!");
    }
    // 判断是否还存在结果集中
    var exists =
        latch.getParticipants().stream().anyMatch(participant -> id.equals(participant.getId()));
    // 如果不存在
    if (!exists) {
      requeue();
    }
    // 判断是否选举成功
    return id.equals(latch.getLeader().getId());
  }

  public boolean doWorkIfHasLeadership(Runnable runnable) throws Exception {
    if (this.hasLeadership()) {
      log.info("{} run as leader now!", id);
      runnable.run();
      return true;
    } else {
      log.info("{} run as slave now!", id);
      return false;
    }
  }
}
