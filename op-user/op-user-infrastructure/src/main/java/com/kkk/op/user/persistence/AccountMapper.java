package com.kkk.op.user.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper（方法名使用：insert update delete select selectList selectPage） <br>
 * <br>
 *
 * @author KaiKoo
 */
@Mapper // 可以不加
public interface AccountMapper {

  int insert(AccountPO accountPO);

  int updateById(AccountPO accountPO);

  @Delete("DELETE FROM account WHERE id = #{id}")
  int deleteById(Long id);

  @Delete("DELETE FROM account WHERE user_id = #{userId}")
  int deleteByUserId(Long userId);

  // Mybatis支持封装为Optional
  Optional<AccountPO> selectById(Long id);

  List<AccountPO> selectByIds(Collection<Long> ids);

  List<AccountPO> selectByUserId(Long userId);

  List<AccountPO> selectByUserIds(Collection<Long> userIds);
}
