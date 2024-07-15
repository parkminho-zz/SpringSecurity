package com.example.demo.config;

import java.util.Collections;

import org.apache.coyote.http11.Http11InputBuffer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.example.demo.jwt.JWTFilter;
import com.example.demo.jwt.JWTUtil;
import com.example.demo.jwt.LoginFilter;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	//AuthenticationManager가 인자로 받을 AuthenticationConfiguration 객체 생성자 주입
	public final AuthenticationConfiguration authenticationConfiguration;   
	public final JWTUtil jwtUtil;
	
	public SecurityConfig( AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil ) {
		
		this.authenticationConfiguration = authenticationConfiguration;
		this.jwtUtil = jwtUtil;
	}
	
	//AuthenticationManager 빈 등록
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
		
		return configuration.getAuthenticationManager();
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		//cors 설정 security 부분, 스프링단에서도 cors설정 해줘야됨
		http
			.cors((cors) -> cors
					.configurationSource(new CorsConfigurationSource() {
						
						@Override
						public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
							
							CorsConfiguration configuration = new CorsConfiguration();
							
		                    configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
		                    configuration.setAllowedMethods(Collections.singletonList("*"));
		                    configuration.setAllowCredentials(true);
		                    configuration.setAllowedHeaders(Collections.singletonList("*"));
		                    configuration.setMaxAge(3600L);
							
							
							configuration.setExposedHeaders(Collections.singletonList("Authorization"));

							
							
							
							
							return null;
						}
					}));
		
		//csrf 비호라성화
		http
			.csrf((auth) -> auth.disable());
		
		//폼 비활성화
		http
			.formLogin((auth) -> auth.disable());
		
		//http basic 인증방식 비활성화
		http
			.httpBasic((auth) -> auth.disable());
		
		// 경로 권한 설정 
		http
			.authorizeHttpRequests((auth) -> auth
					.requestMatchers("/login", "/", "/join").permitAll() // 이 경로에대해서는 모든권한허용
					.requestMatchers("admin").hasRole("ADMIN") // 어드민 계정만 접근가능 
					.anyRequest().authenticated()); // 나머지 다른요청에대해서는 로그인유저만 접근가능
		
		//role 필터
		http
			.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class); 
		
		//필터추가
		http
			.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil ), UsernamePasswordAuthenticationFilter.class);
		
		//세션설정 (가장중요)
		http
			.sessionManagement((session) -> session
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		return http.build();
		
	}
}
