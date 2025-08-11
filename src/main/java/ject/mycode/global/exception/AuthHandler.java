package ject.mycode.global.exception;

import ject.mycode.global.response.BaseResponseCode;
import ject.mycode.global.response.ErrorResponseCode;

public class AuthHandler extends RuntimeException{
    public AuthHandler(BaseResponseCode errorCode) {
        super(errorCode.getMessage());
    }
}