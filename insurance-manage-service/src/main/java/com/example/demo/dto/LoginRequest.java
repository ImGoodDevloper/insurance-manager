package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Schema(name = "LoginRequest", description = "使用者登入請求參數")
public class LoginRequest {
	
	@NotNull(message = "帳號不可為空")
	@Schema(description = "使用者帳號", example = "tester")
	private String username;
	
	@NotNull(message = "密碼不可為空")
	@Schema(description = "使用者密碼", example = "test123")
    private String password;
}
