package com.example.demo.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.dto.CustomUserDetails;
import com.example.demo.entity.UserEntity;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTFilter extends OncePerRequestFilter{

	private final JWTUtil jwtUtil;
	
	public JWTFilter(JWTUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		//요청에서 Authorization을 찾음
		String authorization = request.getHeader("Authorization");
		
		
		//Authorization 헤더 검증: 만약 없거나 "Bearer " 라는 스트링을 찾지못한다면 다음필터로이동
		if(authorization == null || !authorization.startsWith("Bearer ")) {
			System.out.println("token null ( 헤더가 없거나 Bearer 라는 스트링 못찾음 )");
			filterChain.doFilter(request, response);
		
			//조건이 해당되면 메소드 종료 (필수)
			return;
		}
		
		//Bearer 부분 제거 후 순수토큰만 획득
		String token = authorization.split(" ")[1];

		//토큰 소멸시간 검증
		if(jwtUtil.isExpired(token)) {
			
			System.out.println("Token expired");
			filterChain.doFilter(request, response);
			
			//조건이 해당되면 메소드종룡 (필수)
			return;
		}
		
		//토큰에서 username과 role 획득
		String username = jwtUtil.getUsername(token);
		String role = jwtUtil.getRole(token);
		
		//UserEntity를 생성하여 값 set
		UserEntity userEntity = new UserEntity();
		userEntity.setUsername(username);
		userEntity.setPassword("tempPassword");
		userEntity.setRole(role);

		CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);
		
		
		Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
		
		SecurityContextHolder.getContext().setAuthentication(authToken);
		
		//다 완료되었으면 다음 핆터로 넘어가기
		filterChain.doFilter(request, response);
	}
}
