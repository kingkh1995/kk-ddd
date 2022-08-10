package com.kk.ddd.sales.web;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@EmbeddedKafka(count = 5)
@SpringBootTest
@ActiveProfiles("dev")
class SalesWebApplicationTests {

  @Test
  void contextLoads() {}
}
