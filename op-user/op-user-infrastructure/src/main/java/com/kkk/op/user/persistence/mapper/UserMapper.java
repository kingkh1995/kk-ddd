package com.kkk.op.user.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kkk.op.user.persistence.UserDO;
import java.util.List;

/**
 * user Mapper
 *
 * @author KaiKoo
 */
public interface UserMapper extends BaseMapper<UserDO> {

  List<UserDO> selectByGender(String gender);

  // Mybatis-Plus分页实现 添加分页插件后 只需要将第一个参数设置为IPage对象即可 复用批量查询的PreparedStatement
  IPage<UserDO> selectByGender(IPage<?> page, String gender);
}
