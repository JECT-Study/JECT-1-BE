package ject.mycode.global.exception;

import ject.mycode.global.response.BaseResponse;
import ject.mycode.global.response.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<BaseResponse<?>> handleCustomException(CustomException e) {
		log.error("[customHandleException] : {}", e.getMessage(), e);

		BaseResponseCode responseCode = e.getBaseResponseCode();
		return ResponseEntity
			.status(responseCode.getHttpStatus())  // HTTP 상태 코드 활용
			.body(new BaseResponse<>(responseCode, null));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<BaseResponse<?>> handleException(Exception e) {
		log.error("[handleException] : {}", e.getMessage(), e);

		BaseResponseCode responseCode = BaseResponseCode.INTERNAL_SERVER_ERROR;
		return ResponseEntity
			.status(responseCode.getHttpStatus())  // 500 상태 코드 설정
			.body(new BaseResponse<>(responseCode, null));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<BaseResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		List<ErrorField> errorFields = new ArrayList<>();

		for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
			errorFields.add(new ErrorField(fieldError.getField(), fieldError.getDefaultMessage()));
		}

		BaseResponseCode responseCode = BaseResponseCode.VALIDATION_FAILED;

		return ResponseEntity
			.status(responseCode.getHttpStatus())
			.body(new BaseResponse<>(responseCode, errorFields));
	}


	@Getter
	@AllArgsConstructor
	public static class ErrorField {
		private String field;
		private String message;
	}
}
