package com.kk.ddd.sales.web.controller;

import com.kk.ddd.sales.manager.StockManager;
import com.kk.ddd.sales.web.provider.StockProviderImpl;
import com.kk.ddd.support.annotation.BaseController;
import com.kk.ddd.support.model.proto.StockOperateEnum;
import com.kk.ddd.support.model.proto.StockOperateRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Slf4j
@Validated
@BaseController
@RequestMapping("/api/v1/stock")
public class StockController extends StockProviderImpl {

  @Autowired
  public StockController(StockManager stockManager) {
    super(stockManager);
  }

  @PostMapping("/operate")
  @ResponseStatus(HttpStatus.CREATED)
  public void add(@RequestBody @Validated StockOperateApiRequest request) {
    deduct(request.toStockOperateRequest());
  }

  @Data
  private static class StockOperateApiRequest {
    private StockOperateEnum operateType;
    private String orderNo;
    private int count = 3;

    private StockOperateRequest toStockOperateRequest() {
      return StockOperateRequest.newBuilder()
          .setOperateType(this.operateType)
          .setOrderNo(this.orderNo)
          .setCount(this.count)
          .build();
    }
  }
}
