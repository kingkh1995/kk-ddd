package com.kkk.op.job.persistence;

import com.kkk.op.support.enums.JobStateEnum;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * PagingAndSortingRepository：支持curd、排序、分页; <br>
 * QueryByExampleExecutor：支持Example查询，一般不使用；<br>
 * JpaSpecificationExecutor：支持Specification查询，CriteriaQuery和CriteriaBuilder都支持返回Predicate。 <br>
 *
 * @author KaiKoo
 */
public interface JobDAO extends JpaRepository<JobDO, Long>, JpaSpecificationExecutor<JobDO> {

  List<JobDO> findAllByState(JobStateEnum state);

  Page<JobDO> findByState(JobStateEnum state, Pageable pageable);
}
