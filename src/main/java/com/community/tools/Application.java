package com.community.tools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {

  public static void main(String[] args) {
    System.setProperty("java.io.tmpdir", "/tmp");
    System.out.println(System.getProperty("java.io.tmpdir"));
    SpringApplication.run(Application.class, args);
  }
}
