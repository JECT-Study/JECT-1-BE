package ject.mycode.domain.user.converter;

import ject.mycode.domain.auth.dto.AuthRes;
import ject.mycode.domain.user.entity.User;

public class UserConverter {
    public static AuthRes.LoginResultDTO toLoginResultDTO(User user, String accessToken, String refreshToken) {
        return AuthRes.LoginResultDTO.builder()
                .id(user.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .image(user.getImage())
                .nickname(user.getNickname())
                .build();
    }
}