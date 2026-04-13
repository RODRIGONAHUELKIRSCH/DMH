package com.dmh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiCardApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiCardApplication.class, args);
	}

}
