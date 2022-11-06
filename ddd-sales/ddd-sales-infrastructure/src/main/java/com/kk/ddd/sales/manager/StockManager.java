package com.kk.ddd.sales.manager;

import com.kk.ddd.sales.bo.StockDeductBO;
import com.kk.ddd.sales.persistence.StockDAO;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.persistence.RollbackException;
import lombok.extern.slf4j.Slf4j;
import org.jctools.queues.MpscUnboundedArrayQueue;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 回滚时由请求端发送MQ <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Service
public class StockManager implements InitializingBean, DisposableBean {

  private int batchSize;
  private int maxWaitMillis;
  private TransactionTemplate transactionTemplate;
  private StockDAO stockDAO;
  private Queue<StockDeductBO> queue;
  private ExecutorService executorService;
  private Thread worker;

  @Autowired
  public void setBatchSize(@Value("${stock.operate.batch-size:10}") int batchSize) {
    this.batchSize = batchSize;
  }

  @Autowired
  public void setMaxWaitMillis(@Value("${stock.operate.max-wait-millis:20}") int maxWaitMillis) {
    this.maxWaitMillis = maxWaitMillis;
  }

  @Autowired
  public void setTransactionTemplate(
      PlatformTransactionManager transactionManager,
      @Value("${stock.operate.transaction-timeout:5}") int transactionTimeout) {
    var transactionTemplate = new TransactionTemplate(transactionManager);
    transactionTemplate.setName("StockManager-TransactionTemplate");
    transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    transactionTemplate.setReadOnly(false);
    transactionTemplate.setTimeout(transactionTimeout);
    this.transactionTemplate = transactionTemplate;
  }

  @Autowired
  public void setStockDAO(StockDAO stockDAO) {
    this.stockDAO = stockDAO;
  }

  public Future<Boolean> deduct(String orderNo, int count) {
    var future = new CompletableFuture<Boolean>();
    if (!queue.offer(new StockDeductBO(orderNo, count, future))) {
      future.complete(false);
    }
    return future;
  }

  private class Worker implements Runnable {

    @Override
    public void run() {
      log.info("Stock operate thread started!");
      while (!Thread.currentThread().isInterrupted()) {
        if (queue.isEmpty()) {
          waitSomeTime();
          continue;
        }
        execute(drain());
      }
      log.warn("Stock operate thread stopped!");
    }
  }

  private void waitSomeTime() {
    var time = System.nanoTime();
    var duration = maxWaitMillis * 1_000_000L;
    var sleepTimeMs =
        ((time / duration + 1) * duration + 999_999L - time) / 1_000_000L; // always positive
    try {
      Thread.sleep(sleepTimeMs);
    } catch (InterruptedException e) {
      // interrupt to stop
      Thread.currentThread().interrupt();
    }
  }

  private static final Predicate<StockDeductBO> UNCANCELLED = bo -> !bo.future().isCancelled();

  private List<StockDeductBO> drain() {
    // poll直到为空或元素个数达到限制并过滤掉已取消
    return Stream.generate(() -> queue.poll())
        .takeWhile(Objects::nonNull)
        .filter(UNCANCELLED)
        .limit(batchSize)
        .toList();
  }

  private void execute(List<StockDeductBO> bos) {
    if (bos.isEmpty()) {
      return;
    }
    executorService.submit(
        () -> {
          // filter cancelled again
          var filtered = bos.stream().filter(UNCANCELLED).toList();
          if (filtered.isEmpty()) {
            return;
          }
          try { // catch to prevent thread exit
            // merge deduct first
            if (mergeDeduct(filtered)) {
              return;
            }
            // merge deduct failed then single deduct each
            filtered.forEach(this::singleDeduct);
          } catch (Exception e) {
            // handle unexpected exception
            log.error("deduct failed!", e);
            bos.forEach(bo -> complete(bo, false));
          }
        });
  }

  private boolean mergeDeduct(List<StockDeductBO> bos) {
    var sum = bos.stream().mapToInt(StockDeductBO::count).sum();
    var succeeded = false;
    try {
      succeeded =
          Boolean.TRUE.equals(
              transactionTemplate.execute(
                  status -> {
                    // deduct stock first
                    if (stockDAO.deductStock(sum) == 0) {
                      // throw to rollback
                      throw new RollbackException("update stock failed");
                    }
                    // deduct stock succeeded then save logs
                    var OrderNos = bos.stream().map(StockDeductBO::orderNo).toList();
                    if (stockDAO.insertStockOperateLog(OrderNos) < bos.size()) {
                      // throw to rollback
                      throw new RollbackException("save logs failed");
                    }
                    // all succeeded
                    log.info("merge deduct succeeded, bos:{}.", bos);
                    return true;
                  }));
    } catch (RollbackException e) {
      // ignore thrown rollback exception
      log.info("merge deduct failed by [{}].", e.getMessage());
    }
    // if succeeded complete future
    if (succeeded) {
      bos.forEach(bo -> complete(bo, true));
    }
    return succeeded;
  }

  private void singleDeduct(StockDeductBO bo) {
    var succeeded = false;
    try {
      succeeded =
          Boolean.TRUE.equals(
              transactionTemplate.execute(
                  status -> {
                    if (stockDAO.deductStock(bo.count()) == 0) {
                      throw new RollbackException("update stock failed");
                    } else if (stockDAO.insertStockOperateLog(bo.orderNo()) == 0) {
                      throw new RollbackException("insert log failed");
                    }
                    log.info("single deduct succeeded, bo:{}.", bo);
                    return true;
                  }));
    } catch (RollbackException e) {
      log.info("single deduct failed by [{}].", e.getMessage());
    }
    // at last complete
    complete(bo, succeeded);
  }

  private void complete(StockDeductBO bo, boolean result) {
    if (!bo.future().complete(result)) {
      log.info("compete failed, result: {}, orderNo: {}.", result, bo.orderNo());
      // todo... 发送告警消息
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (this.worker != null) {
      return;
    }
    queue = new MpscUnboundedArrayQueue<>(1024);
    executorService = Executors.newCachedThreadPool();
    startWorker();
  }

  @Override
  public void destroy() throws Exception {
    stopWorker();
  }

  private synchronized void startWorker() {
    stopWorker();
    this.worker = new Thread(new Worker(), "StockOperateThread");
    this.worker.start();
  }

  private synchronized void stopWorker() {
    if (this.worker == null) {
      return;
    }
    this.worker.interrupt();
  }
}
