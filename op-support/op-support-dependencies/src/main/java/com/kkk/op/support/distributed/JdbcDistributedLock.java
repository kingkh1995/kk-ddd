package com.kkk.op.support.distributed;

import com.kkk.op.support.marker.DistributedLock;
import com.kkk.op.support.tool.SleepHelper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 数据库分布式锁，基于事务和 select for update nowait实现，要求PreparedStatement为线程安全 <br>
 * mysql只支持 nowait，oracle还支持 wait n，h2暂时均不支持，且获取行锁失败会导致连接断开。 <br>
 * insert执行前会先获取行锁，只能等待锁超时，mysql可以修改锁超时时间(set global innodb_lock_wait_timeout = 5;)
 *
 * @author KaiKoo
 */
@Slf4j
@Builder
@Transactional(propagation = Propagation.MANDATORY) // 设置要求必须存在事务
public class JdbcDistributedLock implements DistributedLock {

  @Default private final long sleepInterval = 200L;

  private final DataSource dataSource;

  @Default
  private final String select4UpdateNowaitSql =
      "SELECT lock_name FROM distributed_lock WHERE lock_name = ? FOR UPDATE NOWAIT;";

  @Default private final String insertSql = "INSERT INTO distributed_lock (lock_name) VALUES (?);";

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
    // 获取当前连接，如果不存在会创建一个。
    var connection = DataSourceUtils.getConnection(this.dataSource);
    // 获取当前隔离级别
    var isolation = getTransactionIsolation(connection);
    // 执行select4update需要设置隔离级别为RC，因为RR级别下如果未匹配到数据会加上间隙锁，之后的插入操作会导致死锁，结果会是无法并发获取锁。
    setTransactionIsolation(connection, Isolation.READ_COMMITTED.value());
    try {
      return tryLock0(connection, name, waitSeconds);
    } finally {
      // 执行结束将隔离级别回滚
      setTransactionIsolation(connection, isolation);
    }
  }

  private boolean tryLock0(Connection connection, String name, long waitSeconds) {
    try {
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

  private int getTransactionIsolation(Connection connection) {
    try {
      return connection.getTransactionIsolation();
    } catch (SQLException e) {
      log.error(
          "*This should be unreachable!* JdbcDistributedLock set transactionIsolation error!", e);
      return Isolation.DEFAULT.value();
    }
  }

  private void setTransactionIsolation(Connection connection, int isolation) {
    try {
      connection.setTransactionIsolation(isolation);
    } catch (SQLException e) {
      log.error(
          "*This should be unreachable!* JdbcDistributedLock set transactionIsolation error!", e);
    }
  }

  @Override
  public void unlock(String name) {
    // do nothing
  }
}
