package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dto.JoinDTO;
import com.example.demo.service.JoinService;

@Controller
@ResponseBody
public class JoinController {

	private final JoinService joinService;
	
	public JoinController(JoinService joinService) {
		this.joinService = joinService;
	}
	
	@PostMapping("/join")
	public String joinProcess(JoinDTO joinDTO) {
		
		//JoinService 는 회원가입 로직
		joinService.joingProcess(joinDTO);
		
		return "ok";
	}
	
}
