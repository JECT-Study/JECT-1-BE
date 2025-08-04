package ject.mycode.domain.auth.controller;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import ject.mycode.domain.auth.dto.AuthReq;
import ject.mycode.domain.auth.dto.AuthRes;
import ject.mycode.domain.auth.dto.apple.AppleCodeReq;
import ject.mycode.domain.auth.dto.apple.AppleLoginReq;
import ject.mycode.domain.auth.dto.apple.AppleTokenRes;
import ject.mycode.domain.auth.jwt.dto.JwtRes;
import ject.mycode.domain.auth.service.AppleOauthService;
import ject.mycode.domain.auth.service.AuthCommandService;
import ject.mycode.domain.auth.service.SocialAuthService;
import ject.mycode.domain.user.converter.UserConverter;
import ject.mycode.global.response.BaseResponse;
import ject.mycode.global.response.BaseResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthCommandService authCommandService;
    private final SocialAuthService socialAuthService;
    private final AppleOauthService appleOauthService;

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

    @Operation(summary = "카카오 로그인", description = "카카오 인가 코드를 입력받아 로그인을 처리합니다.")
    @PostMapping("/social/kakao")
    public BaseResponse<AuthRes.LoginResultDTO> kakaoLogin(@Valid @RequestBody AuthReq.SocialLoginDTO request) {
        return new BaseResponse<>(BaseResponseCode.KAKAO_LOGIN_SUCCESS, socialAuthService.login("kakao", request));
    }

    @Operation(summary = "토큰 재발급", description = "accessToken이 만료 시 refreshToken을 통해 accessToken을 재발급합니다.")
    @PostMapping("/token/refresh")
    public BaseResponse<JwtRes> reissueToken(@RequestHeader("RefreshToken") String refreshToken) {
        return new BaseResponse<>(BaseResponseCode.TOKEN_REISSUE_SUCCESS, authCommandService.reissueToken(refreshToken));
    }

    @Operation(summary = "로그아웃", description = "로그아웃 기능입니다.")
    @PostMapping("/logout")
    public BaseResponse<String> logout(HttpServletRequest request) {
        authCommandService.logout(request);
        return new BaseResponse<>(BaseResponseCode.LOGOUT_SUCCESS);
    }

    @Operation(summary = "애플 로그인", description = "애플 인가 코드를 입력받아 로그인을 처리합니다.")
    @PostMapping("social/apple")
    public BaseResponse<AuthRes.LoginResultDTO> appleLogin(@Valid @RequestBody AppleCodeReq request) {

//        JsonNode node = getNode(request.authorizationCode());

        // clientSecret 생성
        String clientSecret = appleOauthService.createClientSecret();
        log.info("애플 로그인용 클라이언트 시크릿 생성됨");

        // authorizationCode와 clientSecret으로 identityToken 가져오기
        AppleTokenRes appleTokenResponse = appleOauthService.getAppleToken(request.authorizationCode(), clientSecret);

        // 클라이언트에서 전달받은 identityToken 검증, 회원가입/로그인 처리
        Map<String, String> memberResponse = appleOauthService.appleOauth(appleTokenResponse);

        AuthRes.LoginResultDTO loginResult = AuthRes.LoginResultDTO.builder()
                .id(memberResponse.containsKey("id") ? Long.parseLong(memberResponse.get("id")) : null)
                .accessToken(memberResponse.get("accessToken"))
                .refreshToken(memberResponse.get("refreshToken"))
                .build();

        return new BaseResponse<>(BaseResponseCode.APPLE_LOGIN_SUCCESS, loginResult);
    }
}
