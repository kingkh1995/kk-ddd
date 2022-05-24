package com.kkk.op.user.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * UserMapper
 *
 * @author KaiKoo
 */
@Mapper
public interface UserMapper {

  int insert(UserPO userPO);

  int updateById(UserPO userPO);

  @Delete("DELETE FROM user WHERE id = #{id}")
  int deleteById(Long id);

  Optional<UserPO> selectById(Long id);

  List<UserPO> selectByIds(Collection<Long> ids);

  @Select("SELECT * FROM user WHERE gender = #{gender}")
  List<UserPO> selectByGender(String gender);

  @Select("SELECT * FROM user")
  List<UserPO> selectAll();

  @Select("SELECT * FROM user WHERE username = #{username}")
  Optional<UserPO> selectByUsername(String username);
}
