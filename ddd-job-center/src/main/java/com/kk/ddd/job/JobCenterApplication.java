package com.kk.ddd.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.kk.ddd")
public class JobCenterApplication {

  public static void main(String[] args) {
    SpringApplication.run(JobCenterApplication.class, args);
  }
}
