package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.LoginRequest;
import com.example.demo.security.service.JwtService;
import com.example.demo.service.CustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "身分驗證管理")
public class UserController {

	@Autowired
    private JwtService jwtService;
	
	@Autowired
	private CustomerService us;
	
	@Autowired
    private UserDetailsService userDetailsService;
	
	/**
	 * 使用者登入並簽發 JWT
	 * @param request
	 * @return
	 */
	@Operation(summary = "登入系統", description = "驗證身分並取得 JWT Access Token")
    @ApiResponse(responseCode = "200", description = "登入成功，回傳 Token", 
                 content = @Content(schema = @Schema(implementation = String.class, example = "eyJhbGciOiJIUzI1NiJ...")))
    @ApiResponse(responseCode = "401", description = "帳號或密碼錯誤")
    @PostMapping("/login")
    public String login(@Valid @RequestBody LoginRequest request) {
    	UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());
    	us.userAuth(user, request.getPassword());
    	return jwtService.createLoginAccessToken(user);
    }
}
