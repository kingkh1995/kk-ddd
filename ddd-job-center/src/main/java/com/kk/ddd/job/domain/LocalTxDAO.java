package com.kk.ddd.job.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <br>
 *
 * @author KaiKoo
 */
public interface LocalTxDAO extends JpaRepository<LocalTxDO, String> {}
