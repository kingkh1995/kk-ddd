package com.kkk.op.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.kkk.op")
public class OpJobCenterApplication {

  public static void main(String[] args) {
    SpringApplication.run(OpJobCenterApplication.class, args);
  }
}
