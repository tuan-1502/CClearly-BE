package com.swp391.cclearly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CclearlyApplication {

  public static void main(String[] args) {
    SpringApplication.run(CclearlyApplication.class, args);
  }
}
