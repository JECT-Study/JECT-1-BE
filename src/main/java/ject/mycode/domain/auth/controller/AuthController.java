package ject.mycode.domain.auth.controller;

import ject.mycode.domain.auth.dto.AccessTokenRequest;
import ject.mycode.domain.auth.dto.TokenResponse;
import ject.mycode.domain.auth.service.KakaoLoginService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final KakaoLoginService kakaoLoginService;

    @PostMapping("/kakao")
    public ResponseEntity<TokenResponse> kakaoLogin(@RequestBody AccessTokenRequest request) {
        TokenResponse tokenResponse = kakaoLoginService.login(request.getAccessToken());
        return ResponseEntity.ok(tokenResponse);
    }
}