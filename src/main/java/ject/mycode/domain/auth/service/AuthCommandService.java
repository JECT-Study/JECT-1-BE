package ject.mycode.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import ject.mycode.domain.auth.dto.AuthReq;
import ject.mycode.domain.auth.dto.AuthRes;
import ject.mycode.domain.auth.jwt.dto.JwtRes;

public interface AuthCommandService {
    AuthRes.LoginResultDTO login(AuthReq.SocialLoginDTO request);
    JwtRes reissueToken(String refreshToken);
    void logout(HttpServletRequest request);
    void withdraw(HttpServletRequest request);
}