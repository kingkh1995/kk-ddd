package com.kk.ddd.support.distributed;

import com.kk.ddd.support.util.SleepHelper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 数据库分布式锁，基于事务和 select for update [nowait]实现 <br>
 * <br>
 * 核心策略：<br>
 * 1. 首次加锁时确保行存在（UPSERT，仅执行一次）<br>
 * 2. 热路径只走 SELECT ... FOR UPDATE [NOWAIT]，纯非阻塞 <br>
 * <br>
 * 支持数据库：MySQL（INSERT IGNORE + NOWAIT）、H2（MERGE KEY + SET LOCK_TIMEOUT 0） <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Getter(AccessLevel.PRIVATE)
public class JdbcDistributedLockFactory implements DistributedLockFactory {

  private final PlatformTransactionManager transactionManager;

  private final DataSource dataSource;

  @Default private long spinInterval = 200L;

  private final Dialect dialect;

  /** 进程内已初始化锁名集合，跳过不必要的 UPSERT */
  private final Set<String> initialized = ConcurrentHashMap.newKeySet();

  @Builder
  public JdbcDistributedLockFactory(
      @NonNull PlatformTransactionManager transactionManager, long spinInterval) {
    this.transactionManager = transactionManager;
    this.spinInterval = spinInterval;
    this.dataSource = extractDataSource(transactionManager);
    this.dialect = detectDialect(this.dataSource);
  }

  private static DataSource extractDataSource(PlatformTransactionManager transactionManager) {
    try {
      return (DataSource)
          transactionManager
              .getClass()
              .getMethod("getDataSource")
              .invoke(transactionManager);
    } catch (Exception e) {
      throw new IllegalArgumentException("Can't get dataSource from transactionManager!", e);
    }
  }

  private static Dialect detectDialect(DataSource dataSource) {
    try (var conn = dataSource.getConnection()) {
      var productName = conn.getMetaData().getDatabaseProductName().toUpperCase();
      if (productName.contains("H2")) {
        return Dialect.H2;
      }
      if (productName.contains("MYSQL")) {
        return Dialect.MYSQL;
      }
      throw new IllegalArgumentException("Unsupported database: " + productName);
    } catch (SQLException e) {
      throw new IllegalArgumentException("Failed to detect database type!", e);
    }
  }

  @Override
  public void init() {
    try (var conn = this.dataSource.getConnection();
        var stmt = conn.createStatement()) {
      stmt.execute(this.dialect.getDdlSql());
      // 加载已有锁名到 initialized，运行时跳过不必要的 UPSERT
      try (var rs = stmt.executeQuery(this.dialect.getLoadSql())) {
        while (rs.next()) {
          this.initialized.add(rs.getString(1));
        }
      }
      log.info(
          "Initialized table 'distributed_lock' ({} existing locks).",
          this.initialized.size());
    } catch (SQLException e) {
      log.error("Failed to initialize table 'distributed_lock'!", e);
    }
  }

  @Slf4j
  @RequiredArgsConstructor
  @Getter(AccessLevel.PACKAGE)
  enum Dialect {
    MYSQL(
        """
        INSERT IGNORE INTO distributed_lock (lock_name) VALUES (?);
        """,
        """
        SELECT lock_name FROM distributed_lock WHERE lock_name = ? FOR UPDATE NOWAIT;
        """,
        """
        CREATE TABLE IF NOT EXISTS distributed_lock (
            lock_name VARCHAR(255) NOT NULL PRIMARY KEY
        );
        """,
        """
        SELECT lock_name FROM distributed_lock;
        """),
    H2(
        """
        MERGE INTO distributed_lock (lock_name) KEY (lock_name) VALUES (?);
        """,
        """
        SELECT lock_name FROM distributed_lock WHERE lock_name = ? FOR UPDATE;
        """,
        """
        CREATE TABLE IF NOT EXISTS distributed_lock (
            lock_name VARCHAR(255) NOT NULL PRIMARY KEY
        );
        """,
        """
        SELECT lock_name FROM distributed_lock;
        """) {
      @Override
      int beforeLock(Connection connection) {
        var saved = queryLockTimeout(connection);
        if (saved > 0) {
          setLockTimeout(connection, 0);
        }
        return saved;
      }

      @Override
      void afterLock(Connection connection, int savedTimeout) {
        if (savedTimeout > 0) {
          setLockTimeout(connection, savedTimeout);
        }
      }
    };

    private final String initSql;

    private final String lockSql;

    private final String ddlSql;

    private final String loadSql;

    /**
     * 加锁前准备。返回需要恢复的 saved state。
     *
     * @param connection 当前连接
     * @return 需要恢复的状态值；不需要恢复时返回 -1
     */
    int beforeLock(Connection connection) {
      return -1;
    }

    /**
     * 加锁后恢复。
     *
     * @param connection  当前连接
     * @param savedTimeout beforeLock 返回的值
     */
    void afterLock(Connection connection, int savedTimeout) {}

    private static final String TIMEOUT_QUERY_SQL =
        "SELECT VALUE FROM INFORMATION_SCHEMA.SETTINGS WHERE NAME = 'LOCK_TIMEOUT'";

    private static int queryLockTimeout(Connection connection) {
      try (var stmt = connection.createStatement();
          var rs = stmt.executeQuery(TIMEOUT_QUERY_SQL)) {
        if (rs.next()) {
          return rs.getInt(1);
        }
      } catch (SQLException e) {
        log.warn("Failed to query H2 LOCK_TIMEOUT, fallback to 1000.", e);
      }
      return 1000;
    }

    private static final String LOCK_TIMEOUT_PREFIX = "SET LOCK_TIMEOUT ";

    private static void setLockTimeout(Connection connection, int millis) {
      try (var stmt = connection.createStatement()) {
        stmt.execute(LOCK_TIMEOUT_PREFIX + millis);
      } catch (SQLException e) {
        log.warn("Failed to set H2 LOCK_TIMEOUT to {}.", millis, e);
      }
    }
  }

  @Override
  public DistributedLock getLock(String name) {
    return new Lock(name, this);
  }

  @Override
  public DistributedLock getMultiLock(List<String> names) {
    throw new UnsupportedOperationException("MultiLock not yet implemented");
  }

  @Slf4j
  @RequiredArgsConstructor
  private static class Lock implements DistributedLock {

    private final String name;

    private final JdbcDistributedLockFactory factory;

    private static final TransactionDefinition TD =
        new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_MANDATORY);

    private Connection getConnection() {
      // 要求必须存在事务
      this.factory.getTransactionManager().getTransaction(TD);
      return DataSourceUtils.getConnection(Objects.requireNonNull(this.factory.getDataSource()));
    }

    @Override
    public boolean tryLock(long waitSeconds) {
      var connection = getConnection();
      var dialect = this.factory.getDialect();

      // H2 特殊处理：SET LOCK_TIMEOUT 0 使 FOR UPDATE 不阻塞，保存原始值回来恢复
      var savedState = dialect.beforeLock(connection);
      try {
        // 确保行存在（进程内仅执行一次 UPSERT）
        if (this.factory.getInitialized().add(this.name)) {
          try (var upsertPS = connection.prepareStatement(dialect.getInitSql())) {
            upsertPS.setString(1, this.name);
            upsertPS.executeUpdate();
          } catch (SQLException e) {
            // 竞争写入失败（如唯一键冲突）可忽略，后续 SELECT 能找到行
            log.warn("Init upsert failed (concurrent?), may proceed anyway.", e);
          }
        }
        return tryLock0(connection, waitSeconds, dialect);
      } finally {
        dialect.afterLock(connection, savedState);
      }
    }

    private boolean tryLock0(Connection connection, long waitSeconds, Dialect dialect) {
      try (var selectPS = connection.prepareStatement(dialect.getLockSql())) {
        selectPS.setString(1, this.name);
        return SleepHelper.execute(
            () -> {
              try {
                // FOR UPDATE [NOWAIT] 查询到行即加锁成功
                return selectPS.executeQuery().next();
              } catch (SQLException e) {
                // NOWAIT / LOCK_TIMEOUT 0 等异常 → 锁被持有 → 重试
                log.trace("Lock contention on '{}', retrying.", this.name, e);
                return false;
              }
            },
            TimeUnit.SECONDS.toMillis(waitSeconds),
            this.factory.getSpinInterval());
      } catch (SQLException e) {
        log.error("JdbcDistributedLock lock error!", e);
        return false;
      }
    }

    @Override
    public void unlock() {
      // no-op: 锁随事务提交/回滚释放
    }
  }
}
