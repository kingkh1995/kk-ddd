package com.kk.ddd.sales.persistence;

import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * <br/>
 *
 * @author KaiKoo
 */
@Repository
public class MockStockDAO implements StockDAO{

    @Override
    public int deductStock(int count) {
        return 1;
    }

    @Override
    public int insertStockOperateLog(String orderNo) {
        return 1;
    }

    @Override
    public int insertStockOperateLog(List<String> orderNos) {
        return orderNos.size();
    }
}
