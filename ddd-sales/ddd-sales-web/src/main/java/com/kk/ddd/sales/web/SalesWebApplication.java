package com.kk.ddd.sales.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.kk.ddd")
public class SalesWebApplication {

  public static void main(String[] args) {
    SpringApplication.run(SalesWebApplication.class, args);
  }
}
