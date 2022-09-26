package com.kk.ddd.sales.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * todo... <br>
 *
 * @author KaiKoo
 */
@Repository
public interface StockDAO extends JpaRepository<StockPO, Long>, JpaSpecificationExecutor<StockPO> {

  default int deductStock(@Param("count") int count) {
    return 1;
  }

  default int insertStockOperateLog(String orderNo) {
    return 1;
  }

  default int insertStockOperateLog(List<String> orderNos) {
    return 0;
  }
}
