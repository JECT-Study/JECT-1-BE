package ject.mycode.global.exception;

import com.nimbusds.oauth2.sdk.GeneralException;
import ject.mycode.global.response.ErrorResponseCode;

public class AuthHandler extends GeneralException {
    public AuthHandler(ErrorResponseCode errorCode) {
        super(errorCode.getMessage());
    }
}