package com.example.springbootstarterkit.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("Spring Boot Starter Kit API")
				.version("1.0.0")
				.description("Spring Boot REST API with File Upload, Memo, and User Management")
				.contact(new Contact()
					.name("API Support")
					.email("support@example.com")
				)
			)
			.servers(List.of(
				new Server()
					.url("http://192.168.0.74:8100")
					.description("Development Server")
			));
	}
}
