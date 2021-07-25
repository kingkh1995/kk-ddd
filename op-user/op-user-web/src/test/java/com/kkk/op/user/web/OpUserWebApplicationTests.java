package com.kkk.op.user.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.kkk.op.user.domain.types.AccountId;
import com.kkk.op.user.persistence.mapper.AccountMapper;
import com.kkk.op.user.persistence.mapper.UserMapper;
import com.kkk.op.user.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class OpUserWebApplicationTests {

  @Autowired UserMapper userMapper;

  @Autowired AccountMapper accountMapper;

  @Autowired AccountRepository accountRepository;

  @Test
  void test() throws JsonProcessingException {
    var objectMapper = new ObjectMapper();
    System.out.println(objectMapper.writeValueAsString(userMapper.selectByGender("MALE")));
    var list =
        accountRepository.list(
            ImmutableSet.of(AccountId.of(1l), AccountId.of(2l), AccountId.of(3l)));
  }
}
