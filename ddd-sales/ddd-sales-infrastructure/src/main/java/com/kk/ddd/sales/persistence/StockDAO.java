package com.kk.ddd.sales.persistence;

import java.util.List;

/**
 * todo...
 * <br/>
 *
 * @author KaiKoo
 */
public interface StockDAO {

    int deductStock(int count);

    int insertStockOperateLog(String orderNo);

    int insertStockOperateLog(List<String> orderNos);

}
