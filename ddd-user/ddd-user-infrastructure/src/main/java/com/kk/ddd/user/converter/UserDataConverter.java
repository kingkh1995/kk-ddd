package com.kk.ddd.user.converter;

import com.kk.ddd.support.base.CommonTypesMapper;
import com.kk.ddd.user.domain.entity.User;
import com.kk.ddd.user.domain.mapper.UserTypesMapper;
import com.kk.ddd.user.persistence.AccountPO;
import com.kk.ddd.user.persistence.UserPO;
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

  UserPO toData(User user);

  @InheritInverseConfiguration(name = "toData")
  @Mapping(target = "accounts", source = "accountPOList")
  User fromData(UserPO userPO, List<AccountPO> accountPOList);

  @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
  List<UserPO> toData(List<User> userList);
}
