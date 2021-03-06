package com.kkk.op.user.converter;

import com.kkk.op.support.types.LongId;
import com.kkk.op.user.domain.entity.User;
import com.kkk.op.user.persistence.UserDO;
import java.util.Optional;

/**
 * todo... 待优化
 * @author KaiKoo
 */
public class UserDataConverter {

    //使用volatile解决双重检查问题
    private static volatile UserDataConverter INSTANCE;

    //构造方法设置为私有
    private UserDataConverter() {
    }

    public static UserDataConverter getInstance() {
        if (INSTANCE == null) {
            synchronized (UserDataConverter.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserDataConverter();
                }
            }
        }
        return INSTANCE;
    }

    public User fromData(UserDO userDO) {
        User entity = new User();
        entity.setId(Optional.ofNullable(userDO.getId()).map(LongId::new).orElse(null));
        return entity;
    }

    public UserDO toData(User user) {
        UserDO data = new UserDO();
        data.setId(Optional.ofNullable(user.getId()).map(LongId::getValue).orElse(null));
        return data;
    }
}
