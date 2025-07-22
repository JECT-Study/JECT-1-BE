package ject.mycode.domain.auth.controller;

import ject.mycode.domain.auth.dto.AccessTokenReq;
import ject.mycode.domain.auth.dto.TokenRes;
import ject.mycode.domain.auth.service.KakaoLoginService;
import lombok.*;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<TokenRes> kakaoLogin(@RequestBody AccessTokenReq request) {
        TokenRes tokenRes = kakaoLoginService.getAccessToken(request.getAccessToken());
        return ResponseEntity.ok(tokenRes);
    }
}