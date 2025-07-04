package ject.mycode.global.response;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BaseResponseCode {
	// 1000번대 : 성공코드

	// 2000번대 : 클라이언트 오류
	VALIDATION_FAILED(false, 2999, "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),

	// 3000번대 : 응답 오류

	// 4000번대 : 서버 내부 오류;
	INTERNAL_SERVER_ERROR(false, 4000, "서버 내부 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR);

	private final Boolean status;
	private final Integer code;
	private final String message;
	private final HttpStatus httpStatus;
}
