package com.kkk.op.support.distributed;

import com.kkk.op.support.marker.DistributedLock;
import com.kkk.op.support.tool.SleepHelper;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 数据库分布式锁，基于事务和 select for update nowait实现，要求PreparedStatement为线程安全 <br>
 * 如果使用 select for update 获取锁失败，连接会自动断开，mysql只支持 nowait，oracle还支持 wait n，h2暂时均不支持
 *
 * @author KaiKoo
 */
@Slf4j
@Builder
@Transactional(propagation = Propagation.MANDATORY) // 设置要求必须存在事务
public class JdbcDistributedLock implements DistributedLock {

  private final long sleepInterval;
  private final DataSource dataSource;
  private final String select4UpdateNowaitSql;
  private final String insertSql;

  @Override
  public boolean tryRun(String name, Runnable runnable) {
    return DistributedLock.super.tryRun(name, runnable);
  }

  @Override
  public boolean tryRun(String name, long waitSeconds, Runnable runnable) {
    return DistributedLock.super.tryRun(name, waitSeconds, runnable);
  }

  @Override
  public boolean tryLock(String name) {
    return DistributedLock.super.tryLock(name);
  }

  @Override
  public boolean tryLock(String name, long waitSeconds) {
    try {
      // 获取当前连接，如果不存在会创建一个。
      var connection = DataSourceUtils.getConnection(this.dataSource);
      var selectPS = connection.prepareStatement(this.select4UpdateNowaitSql);
      selectPS.setString(1, name);
      var insertPS = connection.prepareStatement(this.insertSql);
      insertPS.setString(1, name);
      return SleepHelper.tryGetThenSleep(
          () -> {
            try {
              if (selectPS.executeQuery().next()) {
                // 查询到数据表示加锁成功
                return true;
              } else {
                // 如未查询到数据则插入，执行成功即加锁成功
                return insertPS.executeUpdate() > 0;
              }
            } catch (SQLException e) {
              log.warn("", e);
              return false;
            }
          },
          TimeUnit.SECONDS.toMillis(waitSeconds),
          this.sleepInterval);
    } catch (Exception e) {
      log.error("JdbcDistributedLock lock error!", e);
      return false;
    }
  }

  @Override
  public void unlock(String name) {
    // do nothing
  }
}
