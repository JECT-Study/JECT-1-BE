package ject.mycode.domain.auth.service;

import ject.mycode.domain.auth.dto.TokenRes;

public interface KakaoLoginService {
    TokenRes getAccessToken(String code);
}
