package com.kkk.op.user.converter;

import com.kkk.op.support.base.CommonTypesMapper;
import com.kkk.op.user.domain.entity.User;
import com.kkk.op.user.domain.types.mapper.UserTypesMapper;
import com.kkk.op.user.persistence.po.AccountDO;
import com.kkk.op.user.persistence.po.UserDO;
import java.util.List;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Mapper(
    componentModel = "spring",
    uses = {CommonTypesMapper.class, UserTypesMapper.class, AccountDataConverter.class},
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserDataConverter {

  UserDO toData(User user);

  @InheritInverseConfiguration(name = "toData")
  @Mapping(target = "accounts", source = "accountDOList")
  User fromData(UserDO userDO, List<AccountDO> accountDOList);

  @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
  List<UserDO> toData(List<User> userList);
}
