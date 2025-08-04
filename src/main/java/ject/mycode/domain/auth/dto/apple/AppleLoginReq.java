package ject.mycode.domain.auth.dto.apple;

public record AppleLoginReq(
        String identityToken,
        String authorizationCode
) {
}
