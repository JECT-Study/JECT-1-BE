package ject.mycode.domain.auth.controller;

import ject.mycode.domain.auth.dto.AccessTokenReq;
import ject.mycode.domain.auth.dto.TokenRes;
import ject.mycode.domain.auth.service.KakaoLoginService;
import ject.mycode.global.response.BaseResponse;
import lombok.*;
import ject.mycode.global.response.BaseResponseCode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final KakaoLoginService kakaoLoginService;

    @PostMapping("/login/kakao")
    public BaseResponse<TokenRes> kakaoLogin(@RequestBody AccessTokenReq request) {
        TokenRes tokenRes = kakaoLoginService.getAccessToken(request.getAccessToken());
        return new BaseResponse<>(BaseResponseCode.LOGIN_SUCCESS, tokenRes);
    }
}