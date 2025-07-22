package ject.mycode.domain.auth.service;

import ject.mycode.domain.auth.KakaoApiClient;
import ject.mycode.domain.auth.dto.KakaoUserRes;
import ject.mycode.domain.auth.dto.TokenRes;
import ject.mycode.domain.auth.jwt.util.JwtTokenProvider;
import ject.mycode.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoLoginServiceImpl implements KakaoLoginService {

    private final KakaoApiClient kakaoApiClient;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;  // REST API 키

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    public TokenRes getAccessToken(String code) {
        // 1. 인가 코드로 액세스 토큰 요청
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<TokenRes> response = restTemplate.postForEntity(
                "https://kauth.kakao.com/oauth/token",
                request,
                TokenRes.class
        );


        TokenRes tokenRes = response.getBody();

        // 2. 토큰으로 사용자 정보 요청
        KakaoUserRes kakaoUserRes = kakaoApiClient.getUserInformation("Bearer " + tokenRes.getAccessToken());

        // 3. 사용자 정보 DB 저장
        userService.saveUserIfNotExists(kakaoUserRes);

        return response.getBody();
    }
}