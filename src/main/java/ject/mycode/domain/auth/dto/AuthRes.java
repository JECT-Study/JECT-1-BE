package ject.mycode.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthRes {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LoginResultDTO {
        Long id;
        String accessToken;
        String refreshToken;
    }
}