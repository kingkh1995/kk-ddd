package com.kk.ddd.user.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Mapper // 可以不加
public interface AccountMapper {

  int insert(AccountPO accountPO);

  int updateById(AccountPO accountPO);

  @Delete("DELETE FROM `account` WHERE `id` = #{id} AND `version` = #{version}")
  int deleteById(AccountPO accountPO);

  Optional<AccountPO> selectById(Long id);

  List<AccountPO> selectByIds(Collection<Long> ids);

  List<AccountPO> selectByUserId(Long userId);

  List<AccountPO> selectByUserIds(Collection<Long> userIds);
}
