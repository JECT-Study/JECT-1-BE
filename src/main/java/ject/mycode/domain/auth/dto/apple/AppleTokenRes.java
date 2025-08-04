package ject.mycode.domain.auth.dto.apple;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AppleTokenRes(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("id_token") String idToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("expires_in") Long expiresIn,
        @JsonProperty("token_type") String tokenType
) {
}