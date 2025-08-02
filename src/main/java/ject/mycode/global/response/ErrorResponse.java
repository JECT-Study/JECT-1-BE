package ject.mycode.global.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "isSuccess", "code", "message" })
public class ErrorResponse {

    private final boolean isSuccess = false;
    private final Integer code;
    private final String message;

    public ErrorResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
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
}
