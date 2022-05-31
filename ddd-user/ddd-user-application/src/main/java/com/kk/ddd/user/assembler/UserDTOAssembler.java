package com.kk.ddd.user.assembler;

import com.kk.ddd.support.base.CommonTypesMapper;
import com.kk.ddd.support.model.dto.UserAuthcInfo;
import com.kk.ddd.support.model.dto.UserDTO;
import com.kk.ddd.user.domain.entity.User;
import com.kk.ddd.user.domain.mapper.UserTypesMapper;
import java.util.List;
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
    uses = {CommonTypesMapper.class, UserTypesMapper.class, AccountDTOAssembler.class},
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserDTOAssembler {

  @Mapping(target = "encryptedPassword", source = "password")
  UserAuthcInfo toAuthcInfo(User user);

  UserDTO toDTO(User user);

  @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
  List<UserDTO> toDTO(List<User> users);
}
