package com.kk.ddd.user.application.assembler;

import com.kk.ddd.support.model.dto.UserAuthInfo;
import com.kk.ddd.support.model.dto.UserDTO;
import com.kk.ddd.support.type.CommonTypesMapper;
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
  UserAuthInfo toAuthcInfo(User user);

  UserDTO toDTO(User user);

  @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
  List<UserDTO> toDTO(List<User> users);
}
