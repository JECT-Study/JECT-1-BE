package ject.mycode.domain.auth.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtRes {
    private String accessToken;
    private String refreshToken;
}
