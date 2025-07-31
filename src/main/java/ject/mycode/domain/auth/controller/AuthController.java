package ject.mycode.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import ject.mycode.domain.auth.dto.AuthReq;
import ject.mycode.domain.auth.dto.AuthRes;
import ject.mycode.domain.auth.jwt.dto.JwtRes;
import ject.mycode.domain.auth.service.AuthCommandService;
import ject.mycode.domain.user.converter.UserConverter;
import ject.mycode.global.response.BaseResponse;
import ject.mycode.global.response.BaseResponseCode;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthCommandService authCommandService;

    @Operation(summary = "회원가입", description = "회원가입 기능입니다.")
    @PostMapping("/signup")
    public BaseResponse<AuthRes.SignupResultDTO> signup(@RequestBody @Valid AuthReq.SignupDTO request) {
        return new BaseResponse<>(BaseResponseCode.LOGIN_SUCCESS, UserConverter.toSignupResultDTO(authCommandService.signup(request)));
    }

    @Operation(summary = "일반 로그인", description = "일반 로그인 기능입니다.")
    @PostMapping("/login")
    public BaseResponse<AuthRes.LoginResultDTO> login(@RequestBody @Valid AuthReq.LoginDTO request) {
        return new BaseResponse<>(BaseResponseCode.LOGIN_SUCCESS, authCommandService.login(request));
    }

    @Operation(summary = "토큰 재발급", description = "accessToken이 만료 시 refreshToken을 통해 accessToken을 재발급합니다.")
    @PostMapping("/reissue")
    public BaseResponse<JwtRes> reissueToken(@RequestHeader("RefreshToken") String refreshToken) {
        return new BaseResponse<>(BaseResponseCode.TOKEN_REISSUE_SUCCESS, authCommandService.reissueToken(refreshToken));
    }

    @Operation(summary = "로그아웃", description = "로그아웃 기능입니다.")
    @PostMapping("/logout")
    public BaseResponse<String> logout(HttpServletRequest request) {
        authCommandService.logout(request);
        return new BaseResponse<>(BaseResponseCode.LOGOUT_SUCCESS);
    }
}
