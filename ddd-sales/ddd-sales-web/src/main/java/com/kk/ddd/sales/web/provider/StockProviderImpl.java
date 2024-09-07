package com.kk.ddd.sales.web.provider;

import com.kk.ddd.sales.manager.StockManager;
import com.kk.ddd.support.bean.Jackson;
import com.kk.ddd.support.model.proto.StockOperateReply;
import com.kk.ddd.support.model.proto.StockOperateRequest;
import com.kk.ddd.support.model.proto.StockProviderGrpc.StockProviderImplBase;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * grpc server <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockProviderImpl extends StockProviderImplBase {

  private final StockManager stockManager;

  @Override
  public void operate(
      StockOperateRequest request, StreamObserver<StockOperateReply> responseObserver) {
    switch (request.getOperateType()) {
      case DEDUCT -> responseObserver.onNext(deduct(request));
      default ->
          responseObserver.onNext(
              StockOperateReply.newBuilder().setCode(999).setMessage("Type Not Allowed").build());
    }
    responseObserver.onCompleted();
  }

  protected StockOperateReply deduct(StockOperateRequest request) {
    try {
      if (stockManager.deduct(request.getOrderNo(), request.getCount()).get()) {
        return StockOperateReply.newBuilder().setCode(0).setMessage("Deduct Succeeded").build();
      }
      // todo... send rollback message
      throw new RuntimeException("deduct return false!");
    } catch (Exception e) {
      log.error("deduct failed, request:{}.", Jackson.object2String(request), e);
      return StockOperateReply.newBuilder().setCode(99).setMessage("Deduct Failed").build();
    }
  }
}
