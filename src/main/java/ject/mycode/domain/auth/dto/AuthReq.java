package ject.mycode.domain.auth.dto;

import jakarta.validation.constraints.NotNull;
import ject.mycode.domain.user.enums.SocialType;
import lombok.Getter;

public class AuthReq {
    @Getter
    public static class SocialLoginDTO {
        @NotNull
        SocialType socialType;
        @NotNull
        String socialId;
    }

}
