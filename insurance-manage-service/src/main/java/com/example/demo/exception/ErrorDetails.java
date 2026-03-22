package com.example.demo.exception;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <pre>
 * 全域錯誤訊息模型
 * </pre>
 */
@Schema(name = "ErrorDetails", description = "全域錯誤訊息模型")
public record ErrorDetails(

	@Schema(description = "錯誤發生時間", example = "2026-03-22T10:30:00")
    LocalDateTime timestamp,
    
    @Schema(description = "錯誤摘要訊息", example = "身分驗證失敗")
    String message,
    
    @Schema(description = "詳細錯誤內容或請求路徑", example = "uri=/api/policies/123")
    String details,
    
    @Schema(description = "系統標準化錯誤代碼", example = "ERR_AUTH_401")
    String errorCode
) {}
