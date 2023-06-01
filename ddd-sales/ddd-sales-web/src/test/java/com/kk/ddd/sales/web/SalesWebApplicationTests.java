package com.kk.ddd.sales.web;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.kk.ddd.sales.persistence.StockDAO;
import com.kk.ddd.sales.persistence.StockPO;
import com.kk.ddd.support.model.proto.StockOperateEnum;
import com.kk.ddd.support.model.proto.StockOperateReply;
import com.kk.ddd.support.model.proto.StockOperateRequest;
import com.kk.ddd.support.model.proto.StockProviderGrpc;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    var channel =
        ManagedChannelBuilder.forAddress("localhost", 18888)
            .usePlaintext() // 普通文本传输
            .build();
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
    channel.shutdown();
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
    private int n;
  }
}
