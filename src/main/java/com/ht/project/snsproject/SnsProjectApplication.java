package com.ht.project.snsproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@ConfigurationPropertiesScan("com.ht.project.snsproject.properites")
@EnableAspectJAutoProxy
@EnableScheduling
@SpringBootApplication
public class SnsProjectApplication {
  public static void main(String[] args) {
    SpringApplication.run(SnsProjectApplication.class, args);
  }
}
