package com.kkk.op.user.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kkk.op.user.persistence.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class OpUserWebApplicationTests {

  @Autowired UserMapper userMapper;

  @Test
  void test() throws JsonProcessingException {
    var objectMapper = new ObjectMapper();
    System.out.println(objectMapper.writeValueAsString(userMapper.selectByGender("MALE")));
    System.out.println(
        objectMapper.writeValueAsString(userMapper.selectByGender(new Page<>(), "MALE")));
  }
}
