package com.kk.ddd.sales.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("com.kk.ddd.sales.persistence")
@EnableJpaRepositories("com.kk.ddd.sales.persistence")
@SpringBootApplication(scanBasePackages = "com.kk.ddd")
public class SalesWebApplication {

  public static void main(String[] args) {
    SpringApplication.run(SalesWebApplication.class, args);
  }
}
