package com.website.military;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;

@EnableJpaAuditing
@SpringBootApplication(exclude = SecurityAutoConfiguration.class) 
@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")}) // swagger상의 코스에러 문제 해결. 
public class MilitaryApplication {

	public static void main(String[] args) {
		SpringApplication.run(MilitaryApplication.class, args);
	}

}
