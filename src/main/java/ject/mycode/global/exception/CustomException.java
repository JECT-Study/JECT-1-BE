package ject.mycode.global.exception;

import ject.mycode.global.response.BaseResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomException extends RuntimeException {
	private final BaseResponseCode baseResponseCode;

	@Override
	public String getMessage() {
		return baseResponseCode.getMessage();
	}
}

