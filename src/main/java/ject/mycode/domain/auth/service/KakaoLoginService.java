package ject.mycode.domain.auth.service;

import ject.mycode.domain.auth.dto.KakaoUserInfoResponse;
import ject.mycode.domain.auth.dto.TokenResponse;

public interface KakaoLoginService {
    TokenResponse login(String kakaoAccessToken);
    KakaoUserInfoResponse getUserInfoFromKakao(String accessToken);
}
