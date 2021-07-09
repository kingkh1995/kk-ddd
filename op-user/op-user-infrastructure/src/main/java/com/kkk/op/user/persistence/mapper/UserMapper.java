package com.kkk.op.user.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kkk.op.user.persistence.UserDO;
import java.util.List;

/**
 * user Mapper
 *
 * @author KaiKoo
 */
public interface UserMapper extends BaseMapper<UserDO> {

  List<UserDO> listByGender(String gender);
}
