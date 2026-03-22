package com.example.demo.exception;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * <pre>
無法找到所請求的特定資料或資源的異常
 * <pre>
 */
@Schema(description = "無法找到所請求的特定資料或資源的異常")
public class NotFoundException extends RuntimeException {
	public NotFoundException(String errMsg) {
		super(errMsg);
	};
}
