package com.credv3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CredilyV3Application {

	public static void main(String[] args) {
		SpringApplication.run(CredilyV3Application.class, args);
	}

}
