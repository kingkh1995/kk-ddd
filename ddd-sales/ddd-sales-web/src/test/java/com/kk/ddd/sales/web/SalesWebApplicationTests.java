package com.kk.ddd.sales.web;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.kk.ddd.sales.persistence.StockDAO;
import com.kk.ddd.sales.persistence.StockPO;
import com.kk.ddd.sales.web.interceptor.MyClientInterceptor;
import com.kk.ddd.support.access.AccessConditionForbiddenException;
import com.kk.ddd.support.bean.Jackson;
import com.kk.ddd.support.model.proto.StockOperateEnum;
import com.kk.ddd.support.model.proto.StockOperateReply;
import com.kk.ddd.support.model.proto.StockOperateRequest;
import com.kk.ddd.support.model.proto.StockProviderGrpc;
import com.kk.ddd.support.type.LongId;
import com.kk.ddd.support.util.task.MultiTask;
import com.kk.ddd.support.util.task.Task;
import com.kk.ddd.support.util.task.TaskContext;
import com.kk.ddd.support.util.task.TaskFlow;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
class SalesWebApplicationTests {

  @Autowired private StockDAO stockDAO;

  @Test
  void grpcClientTest() {
    var stockPO = new StockPO();
    stockPO.setInventory(4);
    stockDAO.save(stockPO);
    // 创建客户端channel
    var managedChannel =
        ManagedChannelBuilder.forAddress("localhost", 9595)
            .usePlaintext() // 普通文本传输
            .build();
    // 添加客户端拦截器
    var channel = ClientInterceptors.intercept(managedChannel, new MyClientInterceptor());
    // 流式处理
    StockProviderGrpc.newStub(channel)
        .operate(
            StockOperateRequest.newBuilder()
                .setOperateType(StockOperateEnum.DEDUCT)
                .setOrderNo("Stream")
                .setCount(1)
                .build(),
            new StreamObserver<>() {
              @Override
              public void onNext(StockOperateReply value) {
                log.info("onNext: {}.", value);
              }

              @Override
              public void onError(Throwable t) {
                log.error("onError!", t);
              }

              @Override
              public void onCompleted() {
                log.info("onCompleted!");
              }
            });
    // 异步方式
    var future =
        StockProviderGrpc.newFutureStub(channel)
            .operate(
                StockOperateRequest.newBuilder()
                    .setOperateType(StockOperateEnum.DEDUCT)
                    .setOrderNo("Future")
                    .setCount(2)
                    .build());
    future.addListener(
        () -> {
          try {
            log.info("async get: {}.", future.get());
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        },
        ForkJoinPool.commonPool());
    // 阻塞方式
    var reply =
        StockProviderGrpc.newBlockingStub(channel)
            .operate(
                StockOperateRequest.newBuilder()
                    .setOperateType(StockOperateEnum.DEDUCT)
                    .setOrderNo("Blocking")
                    .setCount(3)
                    .build());
    log.info("blocking get: {}.", reply);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    System.out.println(stockDAO.findAll());
    managedChannel.shutdown();
  }

  @Autowired private PlatformTransactionManager platformTransactionManager;

  @Test
  @Transactional
  void transactionTest() {
    System.out.println(TransactionSynchronizationManager.isSynchronizationActive());
    System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
    System.out.println(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCompletion(int status) {
            if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
              System.out.println("rollback1");
            }
          }
        });
    var transaction =
        platformTransactionManager.getTransaction(
            new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_NOT_SUPPORTED));
    System.out.println(TransactionSynchronizationManager.isSynchronizationActive());
    System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
    System.out.println(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCompletion(int status) {
            if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
              System.out.println("rollback2");
            }
          }
        });
    platformTransactionManager.commit(transaction);
    System.out.println(TransactionSynchronizationManager.isSynchronizationActive());
    System.out.println(TransactionSynchronizationManager.isActualTransactionActive());
    System.out.println(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
    try {
      platformTransactionManager.commit(transaction);
    } catch (TransactionException e) {
      System.out.println(e.getMessage());
    }
  }

  @Test
  void csvTest() {
    String path = "D:\\test.csv";
    List<CsvVO> list = new ArrayList<>();
    list.add(new CsvVO("Jun", "Wang", 1));
    list.add(new CsvVO("San", "Zhang", 2));
    EasyExcel.write(path, CsvVO.class)
        .excelType(ExcelTypeEnum.CSV)
        .charset(StandardCharsets.UTF_8)
        .needHead(false) // 不写表头
        .sheet()
        .doWrite(list);
    list.clear();
    EasyExcel.read(path, CsvVO.class, new PageReadListener<CsvVO>(list::addAll))
        .charset(StandardCharsets.UTF_8)
        .headRowNumber(0) // 读取时无表头
        .sheet()
        .doRead();
    System.out.println(list);
    new File(path).deleteOnExit();
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CsvVO {
    private String first;
    private String last;
    private Integer n;
  }

  @Test
  void testTask() {
    var task0 = new TestTask("task0", 0);
    var task1 = new TestTask("task1", 2000);
    var task2 = new TestTask("task2", 4000);
    var task3 = new TestTask("task3", 4000);
    var task4 = new TestTask("task4", 8000);
    var task5 = new TestTask("task5", 8000);
    var task6 = new TestMultiTask("task6", 1000, 20);
    var task7 = new TestMultiTask("task7", 1000, 3);
    var task8 = new TestMultiTask("task8", 5000, 5);
    var task9 = new TestMultiTask("task9", 8000, 10);
    var task10 = new TestTask("task10", 200000);
    var task11 = new TestTask("task11", 0);
    var test =
        TaskFlow.<TestContext>newBuilder("test")
            .add(task1, task11)
            .add(task3, task0, task1, task2)
            .add(task4, task0, task1, task3)
            .add(task5, task0)
            .add(task6, task0, task2, task4)
            .add(task7, task0, task3, task4)
            .add(task8, task0, task1, task2, task5)
            .add(task9, task0, task3, task6, task6)
            .add(task10, task0, task1, task2, task3, task4, task5, task6, task7)
            .addHead(task0)
            .remove(task11)
            .addTail(task10)
            .buildSequential();
    test.setExecutor(new ThreadPerTaskExecutor(new DefaultThreadFactory("test")));
    try {
      test.apply(new TestContext()).join();
    } catch (Exception e) {
      log.error("error:", e);
    }
  }

  static class TestContext extends TaskContext {}

  static class TestTask extends Task<TestContext> {

    private final long sleep;

    protected TestTask(String name, long sleep) {
      super(name);
      this.sleep = sleep;
    }

    @Override
    protected Consumer<TestContext> action() {
      return context -> {
        try {
          log.info("task[{}] start.", name());
          Thread.sleep(sleep);
        } catch (InterruptedException e) {
        }
      };
    }
  }

  static class TestMultiTask extends MultiTask<TestContext> {

    private final long sleep;
    private final int count;

    public TestMultiTask(String name, long sleep, int count) {
      super(name);
      this.sleep = sleep;
      this.count = count;
    }

    @Override
    protected ToIntFunction<TestContext> getCount() {
      return c -> this.count;
    }

    @Override
    protected ObjIntConsumer<TestContext> subAction() {
      return (c, i) -> {
        try {
          log.info("multi task[{}]-{} start.", name(), i);
          Thread.sleep(sleep);
        } catch (InterruptedException e) {
        }
      };
    }
  }

  @Autowired private MockAppService mockAppService;

  @Test
  void testAccessCheck() {
    Assertions.assertThrows(
        AccessConditionForbiddenException.class, () -> mockAppService.find(LongId.of(100L)));
    MockQueryService.ENTITY.setName("test");
    Assertions.assertThrows(
        AccessConditionForbiddenException.class, () -> mockAppService.find(LongId.of(100L)));
    var entity = mockAppService.find(LongId.of(1L));
    System.out.println(Jackson.object2String(entity));
  }
}
