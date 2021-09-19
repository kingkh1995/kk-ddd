package com.kkk.op.user.persistence.mapper;

import com.kkk.op.user.persistence.model.UserDO;
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

  @Select("SELECT * FROM user WHERE gender = #{gender}")
  List<UserDO> selectListByGender(String gender);

  @Select("SELECT * FROM user")
  List<UserDO> selectList();
}
