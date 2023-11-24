package com.Klusterthon.Medbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableWebSecurity
public class MedbotApplication {
	@Bean
	public WebMvcConfigurer webMvcConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {

				registry
						.addMapping("/**")
						.allowedOrigins("*", "http://127.0.0.1", "http://example.org", "http://localhost", "http://localhost:5500")
						.allowedHeaders("*")
						.allowedMethods("POST", "GET", "PUT", "PATCH", "OPTIONS");
			}
		};
	}


	public static void main(String[] args) {
		SpringApplication.run(MedbotApplication.class, args);
	}

}
