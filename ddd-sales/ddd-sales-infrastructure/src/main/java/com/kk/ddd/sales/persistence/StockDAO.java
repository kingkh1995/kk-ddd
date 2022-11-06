package com.kk.ddd.sales.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * todo... <br>
 *
 * @author KaiKoo
 */
@Repository
public interface StockDAO extends JpaRepository<StockPO, Long>, JpaSpecificationExecutor<StockPO> {

  @Modifying(clearAutomatically = true)
  @Query(value = "update StockPO set inventory = inventory - :count where inventory >= :count")
  int deductStock(@Param("count") int count);

  default int insertStockOperateLog(String orderNo) {
    return 1;
  }

  default int insertStockOperateLog(List<String> orderNos) {
    return orderNos.size();
  }
}
