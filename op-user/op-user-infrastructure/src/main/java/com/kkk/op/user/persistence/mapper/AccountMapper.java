package com.kkk.op.user.persistence.mapper;

import com.kkk.op.user.persistence.po.AccountDO;
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
@Mapper
public interface AccountMapper {

  int insert(AccountDO accountDO);

  int updateById(AccountDO accountDO);

  @Delete("DELETE FROM account WHERE id = #{id}")
  int deleteById(Long id);

  @Delete("DELETE FROM account WHERE user_id = #{userId}")
  int deleteByUserId(Long userId);

  // Mybatis支持封装为Optional
  Optional<AccountDO> selectById(Long id);

  List<AccountDO> selectListByUserId(Long userId);
}
