package com.kkk.op.user.converter;

import com.kkk.op.support.base.CommonTypesMapper;
import com.kkk.op.user.domain.entity.Account;
import com.kkk.op.user.domain.types.mapper.UserTypesMapper;
import com.kkk.op.user.persistence.AccountDO;
import java.util.List;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
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
    uses = {CommonTypesMapper.class, UserTypesMapper.class},
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountDataConverter {
  AccountDO toData(Account account);

  @InheritInverseConfiguration(name = "toData")
  Account fromData(AccountDO accountDO);

  @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
  List<AccountDO> toData(List<Account> accountList);

  @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
  List<Account> fromData(List<AccountDO> accountDOList);
}
