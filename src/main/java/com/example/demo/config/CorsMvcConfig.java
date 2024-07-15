package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer{
	
	@Override
	public void addCorsMappings(CorsRegistry corsRegistry) {
		//cors 스프링단에서 모든 주소가 3000번의 포트만 허용가능하게
		corsRegistry.addMapping("/**")
			.allowedOrigins("http://localhost:3000");
	}
	
}
