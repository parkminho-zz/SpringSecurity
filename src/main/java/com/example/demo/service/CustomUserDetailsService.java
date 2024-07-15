package com.example.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.dto.CustomUserDetails;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	
	private final UserRepository userRepository;
	
	public CustomUserDetailsService(UserRepository userRepository) {
		
		this.userRepository = userRepository;
	}
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
		
		UserEntity userData = userRepository.findByUsername(username);
		
		if(userData != null) {
			return new CustomUserDetails(userData);
		}
		
		return null;
	}
}
