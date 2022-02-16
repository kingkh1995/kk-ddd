package com.kkk.op.job.persistence;

import com.kkk.op.support.enums.JobStateEnum;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * JpaRepository：对继承父接口中方法的返回值进行了适配，注意getById在hibernate实现中为懒加载，需要开启事务才能正常执行；<br>
 * PagingAndSortingRepository：支持curd、排序、分页; <br>
 * QueryByExampleExecutor：支持Example查询，一般不使用；<br>
 * JpaSpecificationExecutor：支持Specification查询，CriteriaQuery和CriteriaBuilder都支持返回Predicate。 <br>
 *
 * @author KaiKoo
 */
public interface JobDAO extends JpaRepository<JobDO, Long>, JpaSpecificationExecutor<JobDO> {

  // 不设置nativeQuery则使用jpql，表名和字段直接使用实体类定义，且jpql不支持insert。
  @Query(value = "update JobDO set state = :state where id = :id")
  @Modifying(clearAutomatically = true) // 标明为dml语句，并设置更新完清空追踪（很重要）。
  int updateStateById(@Param("state") JobStateEnum state, @Param("id") Long id);

  List<JobDO> findAllByState(JobStateEnum state);

  Page<JobDO> findByState(JobStateEnum state, Pageable pageable);
}
