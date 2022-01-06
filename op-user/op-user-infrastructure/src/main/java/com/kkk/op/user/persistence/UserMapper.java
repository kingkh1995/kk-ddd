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

  int insert(UserDO userDO);

  int updateById(UserDO userDO);

  @Delete("DELETE FROM user WHERE id = #{id}")
  int deleteById(Long id);

  Optional<UserDO> selectById(Long id);

  List<UserDO> selectByIds(Collection<Long> ids);

  @Select("SELECT * FROM user WHERE gender = #{gender}")
  List<UserDO> selectByGender(String gender);

  @Select("SELECT * FROM user")
  List<UserDO> selectAll();

  @Select("SELECT * FROM user WHERE username = #{username}")
  Optional<UserDO> selectByUsername(String username);
}
