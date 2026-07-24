package com.fitconnect.fitconnect_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FitconnectBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitconnectBackendApplication.class, args);
	}

}
