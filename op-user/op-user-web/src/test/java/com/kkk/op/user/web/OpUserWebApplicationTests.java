package com.kkk.op.user.web;

import com.kkk.op.user.persistence.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
class OpUserWebApplicationTests {

  @Autowired UserMapper userMapper;

  @Test
  void test() {
    log.warn("{}", userMapper.listByGender("MALE"));
  }
}
