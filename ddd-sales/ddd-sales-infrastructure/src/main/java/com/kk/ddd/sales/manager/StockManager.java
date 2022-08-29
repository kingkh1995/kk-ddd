package com.kk.ddd.sales.manager;

import com.kk.ddd.sales.bo.StockOperateBO;
import com.kk.ddd.sales.persistence.StockDAO;
import java.util.Arrays;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jctools.queues.MpscUnboundedArrayQueue;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 回滚时由请求端发送MQ
 * <br/>
 *
 * @author KaiKoo
 */
@Slf4j
@Service
public class StockManager implements InitializingBean {

    private Queue<StockOperateBO> queue;

    private ExecutorService executorService;

    private final int batchSize;

    private final int maxWaitMillis;

    private final PlatformTransactionManager transactionManager;

    private final StockDAO stockDAO;

    public StockManager(@Value("${stock.operate.batch-size:10}") int batchSize,
            @Value("${stock.operate.max-wait-millis:10}") int maxWaitMillis,
            @Autowired PlatformTransactionManager transactionManager,
            @Autowired StockDAO stockDAO) {
        this.batchSize = batchSize;
        this.maxWaitMillis = maxWaitMillis;
        this.transactionManager = transactionManager;
        this.stockDAO = stockDAO;
    }

    public Future<Boolean> deduct(String orderNo, int count) {
        var future = new CompletableFuture<Boolean>();
        if (!queue.offer(new StockOperateBO(orderNo, count, future))) {
            return CompletableFuture.completedFuture(false);
        }
        return future;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        queue = new MpscUnboundedArrayQueue<>(1024);
        executorService = ForkJoinPool.commonPool();
        new Thread(new Worker(), "StockOperateThread").start();
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
                var arr = new StockOperateBO[batchSize];
                for (var i = 0; i < arr.length; ++i) {
                    var poll = queue.poll();
                    if (Objects.isNull(poll)) {
                        break;
                    }
                    arr[i] = poll;
                }
                executorService.submit(() -> {
                    mergeDeduct(arr);
                });
            }
            log.warn("Stock operate thread stopped!");
        }

    }

    private void waitSomeTime() {
        var time = System.nanoTime();
        var duration = maxWaitMillis * 1_000_000L;
        var sleepTimeMs = ((time / duration + 1) * duration + 999_999L - time) / 1_000_000L; // always positive
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException ignored) {
            // ignore
        }
    }

    private static final TransactionDefinition TD;

    static {
        var definition = new DefaultTransactionDefinition(
                TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        definition.setTimeout(1);
        definition.setReadOnly(false);
        TD = definition;
    }

    private void mergeDeduct(StockOperateBO[] bos) {
        var num = 0;
        var sum = 0;
        for (StockOperateBO bo : bos) {
            if (Objects.isNull(bo)) {
                break;
            }
            ++num;
            sum += bo.count();
        }
        if (num == 0) {
            return;
        }
        try {
            // 开启事务
            var status = transactionManager.getTransaction(TD);
            try {
                // 先合并扣除
                if (stockDAO.deductStock(sum) == 0) {
                    // 合并扣除失败则循环单独扣除
                    Arrays.stream(bos).limit(num).forEach(bo -> bo.future().complete(singleDeduct(bo)));
                    return;
                }
                // 合并扣除成功，批量保存扣除日志。
                var OrderNos = Arrays.stream(bos).limit(num).map(StockOperateBO::orderNo)
                        .collect(Collectors.toList());
                if (stockDAO.insertStockOperateLog(OrderNos) < num) {
                    throw new RuntimeException("deduct failed because of insert log failed!");
                }
                // 提交事务
                transactionManager.commit(status);
                // 设置Future结果为成功
                Arrays.stream(bos).limit(num).forEach(bo -> bo.future().complete(true));
                log.info("stock deduct succeeded, bos:{}.", Arrays.toString(bos));
            } catch (Exception e) {
                log.error("merge deduct failed, bos:{}.", Arrays.toString(bos), e);
                // 回滚事务
                transactionManager.rollback(status);
                // 设置Future结果为失败
                Arrays.stream(bos).limit(num).forEach(bo -> bo.future().complete(false));
            }
        } catch (Exception e) {
            log.error("can't get transaction!", e);
        }
    }

    private boolean singleDeduct(StockOperateBO bo) {
        try {
            // 开启事务
            var status = transactionManager.getTransaction(TD);
            try {
                // 扣除库存，保存扣除日志。
                if (stockDAO.deductStock(bo.count()) == 0) {
                    throw new RuntimeException("deduct failed because of update stock failed!");
                } else if (stockDAO.insertStockOperateLog(bo.orderNo()) == 0) {
                    throw new RuntimeException("deduct failed because of insert log failed!");
                }
                // 提交事务
                transactionManager.commit(status);
                log.info("stock deduct succeeded, bo:{}.", bo);
                return true;
            } catch (Exception e) {
                log.error("single deduct failed, bo:{}.", bo, e);
                transactionManager.rollback(status); // 回滚事务
                return false;
            }
        } catch (Exception e) {
            log.error("can't get transaction!", e);
            return false;
        }
    }

}
