package com.dmh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiTransactionApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiTransactionApplication.class, args);
	}

}
