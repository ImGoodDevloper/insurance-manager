package com.example.demo.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <pre>
 * 唯一識別碼產生器
 * </pre>
 */
public class IdGenerator {
	
	/**
	 * 時間格式：精確到秒 (14位數字)
	 */
	private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	private IdGenerator() {
	}

	/**
	 * 產生唯一的 Long 型態 ID
	 * 
	 * @param uniquekey
	 * @return
	 */
	public static Long generateId(String uniquekey) {
		String timestamp = LocalDateTime.now().format(dtf);
		String hashPart = String.format("%02d", Math.abs(uniquekey.hashCode() % 100));
		return Long.parseLong(timestamp + hashPart);
	}
}