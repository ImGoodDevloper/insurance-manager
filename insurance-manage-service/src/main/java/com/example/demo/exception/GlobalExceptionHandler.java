package com.example.demo.exception;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * <pre>
 * 全域異常處理器
 * 捕捉 Controller 層拋出的所有異常，並將其轉換為標準化 {@link ErrorDetails} 格式。
 * </pre>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 當前端傳入的參數格式不符時觸發（如日期格式錯誤、姓名為空等）
	 * 
	 * @param exception
	 * @param webrequest
	 * @return
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ApiResponse(responseCode = "400", description = "輸入資料無效", 
    content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
	public ResponseEntity<ErrorDetails> handleInvalidInputException(MethodArgumentNotValidException ex,
			WebRequest webrequest) {
		
		StringBuilder sb = new StringBuilder("驗證失敗：");
	    List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
	    
	    for (ObjectError error : allErrors) {
	        String fieldName = ((FieldError) error).getField(); 
	        String errorMessage = error.getDefaultMessage();
	        
	        sb.append("[").append(fieldName).append(": ").append(errorMessage).append("] ");
	    }
		
		ErrorDetails errorDetails = new ErrorDetails(
				LocalDateTime.now(), 
				sb.toString(),
				webrequest.getDescription(false), 
				"CLIENT_INVALID_INPUT"
				);
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * 業務邏輯錯誤時觸發（保單號碼重複等）
	 * 
	 * @param ex
	 * @param webrequest
	 * @return
	 */
	@ExceptionHandler(BusinessLogicException.class)
	@ApiResponse(responseCode = "400", description = "業務邏輯驗證失敗", 
	             content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
	public ResponseEntity<ErrorDetails> handleBusinessInvalidInputException(BusinessLogicException ex,
			WebRequest webrequest) {

		ErrorDetails errorDetails = new ErrorDetails(
				LocalDateTime.now(), 
				ex.getMessage(),
				webrequest.getDescription(false), 
				"BUSINESS_VALIDATION_FAILED"
				);

		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	/**
	 * 處理系統未預期之通用異常 (500 Internal Server Error)
     * 作為系統最後一道防線，捕捉所有未被特定處理器攔截的 Exception
	 * 
	 * @param exception
	 * @param webrequest
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	@ApiResponse(responseCode = "500", description = "伺服器內部錯誤", 
    content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
	public ResponseEntity<ErrorDetails> handleGenericException(Exception exception, WebRequest webrequest) {
		ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), exception.getMessage(),
				webrequest.getDescription(false), "INTERNAL_SERVER_ERROR");
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
