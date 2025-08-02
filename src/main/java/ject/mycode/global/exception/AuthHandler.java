package ject.mycode.global.exception;

import ject.mycode.global.response.ErrorResponseCode;

public class AuthHandler extends RuntimeException{
    public AuthHandler(ErrorResponseCode errorCode) {
        super(errorCode.getMessage());
    }
}