package com.example.demo.exception;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <pre>
自定義的輸入無效異常
 * <pre>
 */
@Schema(description = "客戶輸入資料校驗失敗異常")
public class BusinessLogicException extends RuntimeException {
	public BusinessLogicException(String errMsg) {
		super(errMsg);
	};
}
