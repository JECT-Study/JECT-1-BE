package ject.mycode.domain.auth.service;

import ject.mycode.domain.auth.dto.AuthReq;
import ject.mycode.domain.user.entity.User;
import ject.mycode.global.exception.AuthHandler;

public interface AuthCommandService {
    User signup(AuthReq.SignupDTO request) throws AuthHandler;
    boolean checkNickname(AuthReq.CheckNicknameDTO request);
    boolean checkId(AuthReq.CheckIdDTO request);
//    String verifyEmail(AuthReq.VerifyEmailDTO request) throws MessagingException;
//    boolean verifyCode(AuthReq.VerifyCodeDTO request);
}