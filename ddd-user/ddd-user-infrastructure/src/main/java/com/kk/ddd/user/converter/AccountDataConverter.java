package com.kk.ddd.user.converter;

import com.kk.ddd.support.base.CommonTypesMapper;
import com.kk.ddd.user.domain.entity.Account;
import com.kk.ddd.user.domain.mapper.UserTypesMapper;
import com.kk.ddd.user.persistence.AccountPO;
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
  AccountPO toData(Account account);

  @InheritInverseConfiguration(name = "toData")
  Account fromData(AccountPO accountPO);

  @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
  List<AccountPO> toData(List<Account> accountList);

  @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
  List<Account> fromData(List<AccountPO> accountPOList);
}
