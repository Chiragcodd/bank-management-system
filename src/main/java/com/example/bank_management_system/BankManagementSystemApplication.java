package com.example.bank_management_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.example.bank_management_system.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class BankManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankManagementSystemApplication.class, args);
	}

}
