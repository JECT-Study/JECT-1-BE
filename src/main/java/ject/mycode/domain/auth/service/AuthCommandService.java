package ject.mycode.domain.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import ject.mycode.domain.auth.dto.AuthReq;
import ject.mycode.domain.auth.dto.AuthRes;
import ject.mycode.domain.auth.jwt.dto.JwtRes;
import ject.mycode.domain.user.entity.User;
import ject.mycode.global.exception.AuthHandler;

public interface AuthCommandService {
    User signup(AuthReq.SignupDTO request) throws AuthHandler;
    boolean checkNickname(AuthReq.CheckNicknameDTO request);
    boolean checkId(AuthReq.CheckIdDTO request);
    AuthRes.LoginResultDTO login(AuthReq.SocialLoginDTO request);
    JwtRes reissueToken(String refreshToken);
    void logout(HttpServletRequest request);
//    String verifyEmail(AuthReq.VerifyEmailDTO request) throws MessagingException;
//    boolean verifyCode(AuthReq.VerifyCodeDTO request);
}