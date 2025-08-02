package ject.mycode.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AuthRes {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SignupResultDTO {
        Long id;
        LocalDateTime createdAt;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CheckNicknameResultDTO {
        boolean isDuplicated;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CheckIdResultDTO {
        boolean isDuplicated;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class VerifyCodeResultDTO {
        boolean isVerified;
    }

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