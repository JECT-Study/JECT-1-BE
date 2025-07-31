package ject.mycode.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "isSuccess", "code", "message", "result" })
public class BaseResponse<T> {

	private final boolean isSuccess;
	private final Integer code;
	private final String message;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final T result;

	public BaseResponse(BaseResponseCode responseCode) {
		this.isSuccess = responseCode.getStatus();
		this.message = responseCode.getMessage();
		this.code = responseCode.getCode();
		this.result = null;
	}

	public BaseResponse(BaseResponseCode responseCode, T result) {
		this.isSuccess = responseCode.getStatus();
		this.message = responseCode.getMessage();
		this.code = responseCode.getCode();
		this.result = result;
	}

	@JsonProperty("isSuccess")
	public boolean getIsSuccess() {
		return isSuccess;
	}

	public Integer getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public T getResult() {
		return result;
	}

	public BaseResponse(ErrorResponseCode responseCode) {
		this.isSuccess = responseCode.getStatus();
		this.message = responseCode.getMessage();
		this.code = responseCode.getCode();
		this.result = null;
	}

	public BaseResponse(ErrorResponseCode responseCode, T result) {
		this.isSuccess = responseCode.getStatus();
		this.message = responseCode.getMessage();
		this.code = responseCode.getCode();
		this.result = result;
	}
}
