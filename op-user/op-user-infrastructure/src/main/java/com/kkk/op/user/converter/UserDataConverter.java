package com.kkk.op.user.converter;

import com.kkk.op.support.marker.DataConverter;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.entity.User;
import com.kkk.op.user.persistence.UserDO;
import java.util.Optional;

/**
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
        data.setId(Optional.ofNullable(user.getId()).map(LongId::getValue).orElse(null));
        return data;
    }

    @Override
    public User fromData(UserDO data) {
        var builder = User.builder();
        if (data != null) {
            builder.id(Optional.ofNullable(data.getId()).map(LongId::new).orElse(null));
            return builder.build();
        }
        return builder.build();
    }
}
