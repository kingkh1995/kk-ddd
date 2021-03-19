package com.kkk.op.user.converter;

import com.kkk.op.support.bean.DataConvertSupport;
import com.kkk.op.support.marker.DataConverter;
import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.entity.User;
import com.kkk.op.user.persistence.UserDO;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;

/**
 *
 * @author KaiKoo
 */
public enum UserDataConverter implements DataConverter<User, UserDO> {

    INSTANCE;

    private final static transient DataConverter<User, UserDO> CONVERT_SUPPORT = new UserDataConverterSupport();

    @Override
    public UserDO toData(User user) {
        return CONVERT_SUPPORT.toData(user);
    }

    @Override
    public User fromData(UserDO data) {
        return CONVERT_SUPPORT.fromData(data);
    }

    @Override
    public List<UserDO> toData(Collection<User> entityCol) {
        return CONVERT_SUPPORT.toData(entityCol);
    }

    @Override
    public List<User> fromData(Collection<UserDO> dataCol) {
        return CONVERT_SUPPORT.fromData(dataCol);
    }

    private static class UserDataConverterSupport extends DataConvertSupport<User, UserDO> {

        private UserDataConverterSupport() {
            if (CONVERT_SUPPORT != null) {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        protected UserDO buildDataFromEntity(@NotNull User user) {
            var data = new UserDO();
            data.setId(Optional.ofNullable(user.getId()).map(LongId::getValue).orElse(null));
            return data;
        }

        @Override
        protected User buildEntityFromData(@NotNull UserDO data) {
            var builder = User.builder();
            builder.id(Optional.ofNullable(data.getId()).map(LongId::new).orElse(null));
            return builder.build();
        }
    }

}
