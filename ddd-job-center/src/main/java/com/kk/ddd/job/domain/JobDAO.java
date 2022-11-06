package com.kk.ddd.job.domain;

import com.kk.ddd.support.constant.JobStateEnum;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * JpaRepository：对继承父接口中方法的返回值进行了适配，注意getById在hibernate实现中为懒加载，需要开启事务才能正常执行；<br>
 * PagingAndSortingRepository：支持curd、排序、分页; <br>
 * QueryByExampleExecutor：支持Example查询，一般不使用；<br>
 * JpaSpecificationExecutor：支持Specification查询，CriteriaQuery和CriteriaBuilder都支持返回Predicate。 <br>
 *
 * @author KaiKoo
 */
@Repository
public interface JobDAO extends JpaRepository<JobDO, Long>, JpaSpecificationExecutor<JobDO> {

  // nativeQuery默认为false，表示使用jpql（不支持insert），表名和字段直接使用实体类定义，支持位置绑定和参数名绑定。
  @Query(value = "update JobDO set state = ?3 where id = :id and state = :from")
  @Modifying(clearAutomatically = true) // 标明为dml语句，并设置更新完清空追踪（很重要）。
  @Transactional
  int transferStateById(
      @Param("id") Long id, @Param("from") JobStateEnum from, @Param("to") JobStateEnum to);

  List<JobDO> findAllByState(JobStateEnum state);

  Page<JobDO> findByState(JobStateEnum state, Pageable pageable);
}
