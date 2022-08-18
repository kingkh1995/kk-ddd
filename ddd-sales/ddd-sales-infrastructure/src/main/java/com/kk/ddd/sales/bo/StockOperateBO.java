package com.kk.ddd.sales.bo;

import java.util.concurrent.CompletableFuture;

/**
 *
 * <br/>
 *
 * @author KaiKoo
 */
public record StockOperateBO(String orderNo, Integer count, CompletableFuture<Boolean> future) {

}
