package com.ht.project.snsproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class SnsProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SnsProjectApplication.class, args);
	}

}
