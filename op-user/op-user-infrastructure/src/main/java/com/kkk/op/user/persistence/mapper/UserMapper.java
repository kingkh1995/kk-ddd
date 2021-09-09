package com.kkk.op.user.persistence.mapper;

import com.kkk.op.user.persistence.model.AccountDO;
import com.kkk.op.user.persistence.model.UserDO;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * User聚合根Mapper<br>
 * （方法名使用：insert delete update select selectList selectPage） <br>
 *
 * @author KaiKoo
 */
@Mapper
public interface UserMapper {

  // User表

  @Select("SELECT * FROM user WHERE gender = #{gender}")
  List<UserDO> selectListByGender(String gender);

  UserDO selectByPK(Long userId);

  @Delete("DELETE FROM user WHERE id = #{userId}")
  void deleteByPK(Long userId);

  int insert(UserDO userDO);

  int updateByPK(UserDO userDO);

  // Account表

  AccountDO selectAccountByPK(Long accountId);

  List<AccountDO> selectAccountsByUserId(Long userId);

  @Delete("DELETE FROM account WHERE id = #{accountId}")
  int deleteAccountByPK(Long accountId);

  @Delete("DELETE FROM account WHERE user_id = #{userId}")
  int deleteAccountsByUserId(Long userId);

  int insertAccount(AccountDO accountDO);

  int updateAccountByPK(AccountDO accountDO);
}
