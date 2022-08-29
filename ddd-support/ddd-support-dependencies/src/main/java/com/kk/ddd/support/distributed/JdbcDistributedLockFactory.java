package com.kk.ddd.support.distributed;

import com.kk.ddd.support.util.SleepHelper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 数据库分布式锁，基于事务和 select for update nowait实现，要求PreparedStatement为线程安全 <br>
 * mysql只支持 nowait，oracle还支持 wait n，h2暂时均不支持，且获取行锁失败会导致连接断开。 <br>
 * insert执行前会先获取行锁，只能等待锁超时，mysql可以修改锁超时时间(set global innodb_lock_wait_timeout = 5;)
 *
 * @author KaiKoo
 */
@Slf4j
@Getter(AccessLevel.PRIVATE)
public class JdbcDistributedLockFactory implements DistributedLockFactory {

  private final PlatformTransactionManager transactionManager;

  private final DataSource dataSource;

  @Default private long spinInterval = 200L;

  @Default
  private String select4UpdateNowaitSql =
      "SELECT lock_name FROM distributed_lock WHERE lock_name = ? FOR UPDATE NOWAIT;";

  @Default private String insertSql = "INSERT INTO distributed_lock (lock_name) VALUES (?);";

  @Builder
  public JdbcDistributedLockFactory(
      @NonNull PlatformTransactionManager transactionManager,
      long spinInterval,
      String select4UpdateNowaitSql,
      String insertSql) {
    try {
      this.dataSource =
          (DataSource)
              transactionManager.getClass().getMethod("getDataSource").invoke(transactionManager);
    } catch (Exception e) {
      throw new IllegalArgumentException("Can't get dataSource from transactionManager!", e);
    }
    this.transactionManager = transactionManager;
    this.spinInterval = spinInterval;
    this.select4UpdateNowaitSql = select4UpdateNowaitSql;
    this.insertSql = insertSql;
  }

  @Override
  public DistributedLock getLock(String name) {
    return new Lock(name, this);
  }

  @Override
  public DistributedLock getMultiLock(List<String> names) {
    // todo...
    return null;
  }

  @RequiredArgsConstructor
  private static class Lock implements DistributedLock {

    private final String name;

    private final JdbcDistributedLockFactory factory;

    private static final TransactionDefinition TD =
        new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_MANDATORY);

    private Connection getConnection() {
      // 获取事务，要求必须存在事务。
      this.factory.getTransactionManager().getTransaction(TD);
      // 获取当前连接，如果不存在会创建一个。
      return DataSourceUtils.getConnection(Objects.requireNonNull(this.factory.getDataSource()));
    }

    @Override
    public boolean tryLock(long waitSeconds) {
      var connection = getConnection();
      // 需要设置隔离级别为RC，因为RR级别下如果执行select4update未匹配到数据会加上间隙锁，会阻塞插入意向间隙锁，可能导致大量并发加锁操作阻塞。
      var isolation = setTransactionIsolation(connection, Isolation.READ_COMMITTED.value());
      // 需要将readOnly置为false
      var readOnly = setReadOnly(connection, false);
      try {
        return tryLock0(connection, waitSeconds);
      } finally {
        // 执行结束将隔离级别和readOnly标识回滚
        setTransactionIsolation(connection, isolation);
        setReadOnly(connection, readOnly);
      }
    }

    private int setTransactionIsolation(Connection connection, int isolation) {
      try {
        var backup = connection.getTransactionIsolation();
        connection.setTransactionIsolation(isolation);
        return backup;
      } catch (SQLException e) {
        log.error(
            "*This should be unreachable!* JdbcDistributedLock set transactionIsolation error!", e);
        return Isolation.DEFAULT.value();
      }
    }

    private boolean setReadOnly(Connection connection, boolean readOnly) {
      try {
        var backup = connection.isReadOnly();
        connection.setReadOnly(readOnly);
        return backup;
      } catch (SQLException e) {
        log.error("*This should be unreachable!* JdbcDistributedLock set readOnly error!", e);
        return false;
      }
    }

    private boolean tryLock0(Connection connection, long waitSeconds) {
      try {
        var selectPS = connection.prepareStatement(this.factory.getSelect4UpdateNowaitSql());
        selectPS.setString(1, this.name);
        var insertPS = connection.prepareStatement(this.factory.getInsertSql());
        insertPS.setString(1, this.name);
        return SleepHelper.execute(
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
            this.factory.getSpinInterval());
      } catch (Exception e) {
        log.error("JdbcDistributedLock lock error!", e);
        return false;
      }
    }

    @Override
    public void unlock() {
      // do nothing
    }
  }
}
