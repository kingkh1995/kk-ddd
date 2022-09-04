package com.kk.ddd.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan("com.kk.ddd.job.domain") // Jpa实体扫描
@EnableJpaRepositories("com.kk.ddd.job.domain") // 开启Jpa
@SpringBootApplication(scanBasePackages = "com.kk.ddd")
public class JobCenterApplication {

  public static void main(String[] args) {
    SpringApplication.run(JobCenterApplication.class, args);
  }
}
