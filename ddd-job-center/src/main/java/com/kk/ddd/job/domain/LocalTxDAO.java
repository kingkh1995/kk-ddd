package com.kk.ddd.job.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Repository
public interface LocalTxDAO extends JpaRepository<LocalTxDO, String> {}
