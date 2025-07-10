package ject.mycode.domain.auth.service;

import ject.mycode.domain.auth.dto.KakaoUserInfoResponse;
import ject.mycode.domain.auth.dto.TokenResponse;
import ject.mycode.domain.auth.jwt.util.JwtTokenProvider;
import ject.mycode.domain.user.entity.User;
import ject.mycode.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoLoginServiceImpl implements KakaoLoginService{
    private final RestTemplate restTemplate = new RestTemplate();
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final String kakaoUserInfoUrl = "https://kapi.kakao.com/v2/user/me";

    @Value("${kakao.rest-api-key}")
    private String kakaoRestApiKey;

    public KakaoLoginServiceImpl(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public TokenResponse login(String kakaoAccessToken) {
        KakaoUserInfoResponse userInfo = getUserInfoFromKakao(kakaoAccessToken);

        User user = userRepository.findByEmail(userInfo.getKakaoAccount().getEmail())
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(userInfo.getKakaoAccount().getEmail())
                        .nickname(userInfo.getKakaoAccount().getProfile().getNickname())
                        .provider("KAKAO")
                        .build()
                ));


        String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return new TokenResponse(accessToken, refreshToken);
    }

    public KakaoUserInfoResponse getUserInfoFromKakao(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserInfoResponse> response = restTemplate.exchange(
                kakaoUserInfoUrl,
                HttpMethod.GET,
                entity,
                KakaoUserInfoResponse.class
        );

        return response.getBody();
    }
}
