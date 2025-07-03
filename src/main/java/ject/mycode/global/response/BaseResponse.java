package ject.mycode.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
public class BaseResponse<T> {

	private final boolean isSuccess;
	private final Integer code;
	private final String message;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T result;

	public BaseResponse(BaseResponseCode responseCode) {
		this.isSuccess = responseCode.getStatus();
		this.message = responseCode.getMessage();
		this.code = responseCode.getCode();
	}

	public BaseResponse(BaseResponseCode responseCode, T result) {
		this.isSuccess = responseCode.getStatus();
		this.message = responseCode.getMessage();
		this.code = responseCode.getCode();
		this.result = result;
	}
}
