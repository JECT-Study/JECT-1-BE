package ject.mycode.domain.auth.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import ject.mycode.domain.auth.dto.AuthReq;
import ject.mycode.domain.auth.dto.AuthRes;
import ject.mycode.domain.auth.dto.KakaoUserRes;
import ject.mycode.domain.auth.jwt.userdetails.PrincipalDetails;
import ject.mycode.domain.auth.jwt.util.JwtProvider;
import ject.mycode.domain.user.converter.UserConverter;
import ject.mycode.domain.user.entity.User;
import ject.mycode.domain.user.repository.UserRepository;
import ject.mycode.global.config.SocialProperties;
import ject.mycode.global.exception.AuthHandler;
import ject.mycode.global.response.ErrorResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SocialAuthServiceImpl implements SocialAuthService {
    private final SocialProperties socialProperties;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final Gson gson;

//    @Override
//    public AuthRes.LoginResultDTO login(String provider, AuthReq.SocialLoginDTO request) {
//        // yml 파일의 social 아래 값을 자바 객체로 매핑
//        SocialProperties.ProviderProperties properties = getProviderProperties(provider);
//
//        // 인가 코드를 이용하여 AccessToken 가져옴
//        String accessToken = getAccessToken(
//                URLDecoder.decode(request.getCode(), StandardCharsets.UTF_8),
//                properties.getClientId(),
//                properties.getClientSecret(),
//                properties.getRedirectUri(),
//                properties.getTokenUri()
//        );
//
//        // AccessToken을 사용하여 유저 정보를 가져옴
//        Object userInfo = getUserInfo(
//                accessToken,
//                properties.getUserInfoUri(),
//                getMethod(provider),
//                getUserInfoClass(provider)
//        );
//
//        return socialLogin(provider, userInfo);
//    }

    // 인가 코드를 이용하여 AccessToken 가져옴
    private String getAccessToken(String code, String clientId, String clientSecret, String redirectUri, String tokenUri) {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUri, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                // JSON 응답에서 access_token을 추출
                System.out.println(jsonNode);
                return jsonNode.get("access_token").asText();
            }
        } catch (Exception e) {
            throw new AuthHandler(ErrorResponseCode.INTERNAL_SERVER_ERROR);
        }
        throw new AuthHandler(ErrorResponseCode.INTERNAL_SERVER_ERROR);
    }

    // AccessToken을 사용하여 유저 정보를 가져옴
    private <T> T getUserInfo(String accessToken, String userInfoUri, HttpMethod httpMethod, Class<T> userInfoClass) {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessToken);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(userInfoUri, httpMethod, request, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                // JSON 응답을 userInfoClass로 변환
                System.out.println(response.getBody());
                return gson.fromJson(response.getBody(), userInfoClass);
            }
        } catch (Exception e) {
            throw new AuthHandler(ErrorResponseCode.INTERNAL_SERVER_ERROR);
        }
        throw new AuthHandler(ErrorResponseCode.INTERNAL_SERVER_ERROR);
    }

    // 회원가입 & 로그인
    private <T> AuthRes.LoginResultDTO socialLogin(String provider, T userInfo){
        Optional<User> userOptional = userRepository.findByEmail(getEmail(provider, userInfo));
        User user;

        if (userOptional.isEmpty()) {    // 회원가입
            user = createUser(provider, userInfo);
            userRepository.save(user);
        } else {
            user = userOptional.get();
        }

        // 로그인
        PrincipalDetails memberDetails = new PrincipalDetails(user);

        // 로그인 성공 시 토큰 생성
        String accessToken = jwtProvider.createAccessToken(memberDetails, user.getId());
        String refreshToken = jwtProvider.createRefreshToken(memberDetails, user.getId());

        return UserConverter.toLoginResultDTO(user, accessToken, refreshToken);
    }

    // provider에 맞는 Properties를 반환
    private SocialProperties.ProviderProperties getProviderProperties(String provider) {
        switch (provider.toLowerCase()) {
            case "kakao":
                return socialProperties.getKakao();
            default:
                throw new AuthHandler(ErrorResponseCode.UNSUPPORTED_PROVIDER);
        }
    }

    // provider에 맞는 클래스를 반환
    private Class<?> getUserInfoClass(String provider) {
        switch (provider.toLowerCase()) {
            case "kakao":
                return KakaoUserRes.class;
            default:
                throw new AuthHandler(ErrorResponseCode.UNSUPPORTED_PROVIDER);
        }
    }

    // provider에 맞는 HTTP method를 반환
    private HttpMethod getMethod(String provider) {
        switch (provider.toLowerCase()) {
            case "kakao":
                return HttpMethod.POST;
            default:
                throw new AuthHandler(ErrorResponseCode.UNSUPPORTED_PROVIDER);
        }
    }

    // provider에 맞는 email를 반환
    private <T> String getEmail(String provider, T userInfo) {
        switch (provider.toLowerCase()) {
            case "kakao":
                return ((KakaoUserRes) userInfo).getKakao_account().getEmail();
            default:
                throw new AuthHandler(ErrorResponseCode.UNSUPPORTED_PROVIDER);
        }
    }

    // 네이버 로그인 시 상태코드가 필수값
    private String generateState() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    // provider에 맞는 User 생성
    private <T> User createUser(String provider, T userInfo) {
        switch (provider.toLowerCase()) {
            case "kakao":
                return UserConverter.toKakaoUser((KakaoUserRes) userInfo);
            default:
                throw new AuthHandler(ErrorResponseCode.UNSUPPORTED_PROVIDER);
        }
    }
}