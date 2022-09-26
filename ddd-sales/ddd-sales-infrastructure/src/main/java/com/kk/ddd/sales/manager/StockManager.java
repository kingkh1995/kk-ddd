package com.kk.ddd.sales.manager;

import com.kk.ddd.sales.bo.StockOperateBO;
import com.kk.ddd.sales.persistence.StockDAO;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Stream;
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
  private Queue<StockOperateBO> queue;
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
    if (!queue.offer(new StockOperateBO(orderNo, count, future))) {
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

  private static final Predicate<StockOperateBO> UNCANCELLED = bo -> !bo.future().isCancelled();

  private List<StockOperateBO> drain() {
    // poll直到为空或元素个数达到限制并过滤掉已取消
    return Stream.generate(() -> queue.poll())
        .takeWhile(Objects::nonNull)
        .filter(UNCANCELLED)
        .limit(batchSize)
        .toList();
  }

  private void execute(List<StockOperateBO> bos) {
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
          try {
            // merge deduct first
            if (mergeDeduct(filtered)) {
              return;
            }
            // merge deduct failed then single deduct each
            filtered.forEach(this::singleDeduct);
          } catch (Throwable e) {
            // when reach here means get transaction failed
            log.error("stock operate execute failed.", e);
          }
        });
  }

  private boolean mergeDeduct(List<StockOperateBO> bos) {
    var sum = bos.stream().mapToInt(StockOperateBO::count).sum();
    var succeeded =
        Boolean.TRUE.equals(
            transactionTemplate.execute(
                status -> {
                  // deduct stock first
                  if (stockDAO.deductStock(sum) == 0) {
                    log.info("merge deduct failed because of update stock failed!");
                    return false;
                  }
                  // deduct stock succeeded then save logs
                  var OrderNos = bos.stream().map(StockOperateBO::orderNo).toList();
                  if (stockDAO.insertStockOperateLog(OrderNos) < bos.size()) {
                    // save logs failed throw to rollback
                    log.info("merge deduct failed because of insert logs failed!");
                    return false;
                  }
                  // all succeeded
                  log.info("merge deduct succeeded, bos:{}.", bos);
                  return true;
                }));
    // if succeeded complete future
    if (succeeded) {
      bos.forEach(bo -> complete(bo, true));
    }
    return succeeded;
  }

  private void singleDeduct(StockOperateBO bo) {
    var succeeded =
        Boolean.TRUE.equals(
            transactionTemplate.execute(
                status -> {
                  if (stockDAO.deductStock(bo.count()) == 0) {
                    log.info("single deduct failed because of update stock failed!");
                    return false;
                  } else if (stockDAO.insertStockOperateLog(bo.orderNo()) == 0) {
                    log.info("single deduct failed because of insert log failed!");
                    return false;
                  }
                  log.info("single deduct succeeded, bo:{}.", bo);
                  return true;
                }));
    complete(bo, succeeded);
  }

  private void complete(StockOperateBO bo, boolean result) {
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
    executorService = ForkJoinPool.commonPool();
    startWorker();
  }

  @Override
  public void destroy() throws Exception {
    stopWorker();
  }

  private void startWorker() {
    synchronized (this) {
      stopWorker();
      this.worker = new Thread(new Worker(), "StockOperateThread");
      this.worker.start();
    }
  }

  private void stopWorker() {
    synchronized (this) {
      if (this.worker == null) {
        return;
      }
      this.worker.interrupt();
    }
  }
}
