package com.kkk.op.user.converter;

import com.kkk.op.support.marker.DataConverter;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.entity.User;
import com.kkk.op.user.domain.types.UserId;
import com.kkk.op.user.persistence.po.UserDO;
import java.util.Optional;

/**
 * <br>
 *
 * @author KaiKoo
 */
public enum UserDataConverter implements DataConverter<User, UserDO> {
  INSTANCE;

  @Override
  public UserDO toData(User user) {
    if (user == null) {
      return null;
    }
    var data = new UserDO();
    Optional.ofNullable(user.getId()).map(LongId::longValue).ifPresent(data::setId);
    return data;
  }

  @Override
  public User fromData(UserDO userDO) {
    if (userDO == null) {
      return null;
    }
    var builder = User.builder();
    Optional.ofNullable(userDO.getId()).map(UserId::from).ifPresent(builder::id);
    return builder.build();
  }
}
