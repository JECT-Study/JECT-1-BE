package ject.mycode.global.exception;

import ject.mycode.global.response.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {
    private final BaseResponseCode code;
}
