package com.kk.ddd.user.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * UserMapper <br>
 * mapper方法名应该使用：insert update delete select selectList selectPage <br>
 *
 * @author KaiKoo
 */
@Mapper
public interface UserMapper {

  int insert(UserPO userPO);

  int updateById(UserPO userPO);

  @Update("UPDATE `user` SET `deleted` = 1 WHERE `id` = #{id} AND `version` = ${version} AND `deleted` = 0")
  int logicalDeleteById(UserPO userPO);

  // Mybatis支持封装为Optional
  Optional<UserPO> selectById(Long id);

  List<UserPO> selectByIds(Collection<Long> ids);

  @Select("SELECT * FROM `user`")
  List<UserPO> selectAll();

  @Select("SELECT * FROM `user` WHERE `name` = #{name} AND `deleted` = 0")
  Optional<UserPO> selectByName(String name);

  @Select("SELECT * FROM `user` WHERE `phone` = #{phone} AND `deleted` = 0")
  Optional<UserPO> selectByPhone(String phone);

  @Select("SELECT * FROM `user` WHERE `email` = #{email} AND `deleted` = 0")
  Optional<UserPO> selectByEmail(String email);
}
