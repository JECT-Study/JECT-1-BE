package ject.mycode.domain.auth.service;

import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import ject.mycode.domain.auth.client.AppleClient;
import ject.mycode.domain.auth.dto.apple.AppleIdTokenPayloadRes;
import ject.mycode.domain.auth.dto.apple.AppleLoginReq;
import ject.mycode.domain.auth.dto.apple.ApplePublicKeysRes;
import ject.mycode.domain.auth.dto.apple.AppleTokenRes;
import ject.mycode.domain.auth.jwt.userdetails.PrincipalDetails;
import ject.mycode.domain.auth.jwt.util.AppleClaimsValidator;
import ject.mycode.domain.auth.jwt.util.AppleClientSecret;
import ject.mycode.domain.auth.jwt.util.AppleTokenParser;
import ject.mycode.domain.auth.jwt.util.JwtProvider;
import ject.mycode.domain.user.entity.User;
import ject.mycode.domain.user.enums.SocialType;
import ject.mycode.domain.user.enums.UserRole;
import ject.mycode.domain.user.repository.UserRepository;
import ject.mycode.global.exception.AppleOauthException;
import ject.mycode.global.util.NicknameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleOauthService {
    private static final String GRANT_TYPE = "authorization_code";

    private final AppleTokenParser appleTokenParser;
    private final AppleClient appleClient;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;
    private final AppleClaimsValidator appleClaimsValidator;
    private final AppleClientSecret appleClientSecretProvider;

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    private final SecureRandom random = new SecureRandom();

    @Value("${apple.client-id}")
    private String clientId;

    // client secret 생성
    @Transactional
    public String createClientSecret() {
        log.info("Apple 클라이언트 시크릿 생성 요청");

        try {
            return appleClientSecretProvider.createClientSecret();
        } catch (Exception e) {
            log.error("Apple 클라이언트 시크릿 생성에 실패했습니다. 오류: {}", e.getMessage(), e);
            throw new AppleOauthException("e.getMessage()");
        }
    }

    // apple refreshToken 요청
    @Transactional
    public AppleTokenRes getAppleToken(String code, String clientSecret) {
        log.info("Apple 토큰 요청. 인가 코드: {}, 클라이언트 시크릿: {}", code, clientSecret);

        try {
            return appleClient.appleAuth(clientId, code, GRANT_TYPE, clientSecret);
        } catch (Exception e) {
            log.error("Apple 토큰 요청에 실패했습니다. 오류: {}", e.getMessage(), e);
            throw new AppleOauthException("e.getMessage()");
        }
    }

    // apple user 생성
    @Transactional
    public Map<String, String> appleOauth(AppleTokenRes appleTokenResponse) {
        log.info("Apple OAuth 요청을 받았습니다. 요청: {}, 토큰 응답: {}", appleTokenResponse);

        // identity_token의 header를 추출
        Map<String, String> appleTokenHeader = appleTokenParser.parseHeader(appleTokenResponse.idToken());

        // identity_token을 검증하기 위해 애플의 publicKey list 요청
        ApplePublicKeysRes applePublicKeys = appleClient.getApplePublicKeys();

        // publicKey list에서 identity_token과 alg와 kid가 일치하는 publicKey를 찾기
        // 해당 publicKey로 identity_token 추출에 사용할 RSApublicKey 생성
        PublicKey publicKey = applePublicKeyGenerator.generate(appleTokenHeader, applePublicKeys);

        // identity_token을 publicKey로 검증하여 claim 추출 : 서명 검증
        Claims claims = appleTokenParser.extractClaims(appleTokenResponse.idToken(), publicKey);

        // iss, aud, exp, 검증
        if (!appleClaimsValidator.isValid(claims)) {
            log.error("Apple Claims 유효성 검사 실패. 잘못된 Apple 토큰입니다. {}", claims);
            throw new AppleOauthException("e.getMessage()");
        }

        Map<String, String> appleResponse = extractAppleResponse(claims);

        Optional<User> optionalUser = userRepository.findBySocialId(appleResponse.get("sub"));

        return optionalUser.map(this::loginWithApple)
                .orElseGet(() ->
                        Collections.unmodifiableMap(
                                loginWithAppleCreateMember(appleResponse, appleTokenResponse.refreshToken())
                        )
                );
    }

    // id-token에서 sub, name 추출
    @Transactional
    public Map<String, String> extractAppleResponse(Claims claims) {
        log.debug("Apple Claims에서 응답 추출. Claims: {}", claims);

        Map<String, String> appleResponse = new HashMap<>();

        appleResponse.put("sub", Objects.toString(claims.get("sub"), "N/A"));
        appleResponse.put("name", Objects.toString(claims.get("name"), "N/A"));
        log.debug("추출된 Apple 응답: {}", appleResponse);

        return appleResponse;
    }

//    // 애플 연결해제, 회원 탈퇴 처리
//    @Transactional
//    public boolean unlinkAndDeleteMember(Long memberNo, String clientSecret) throws InterruptedException {
//        log.info("애플 회원 탈퇴 요청. 회원 번호: {}", memberNo);
//
//        User member = userRepository.findById(memberNo)
//                .orElseThrow(() -> {
//                    log.error("회원 번호 {}에 해당하는 회원을 찾을 수 없습니다.", memberNo);
//                    return new EntityNotFoundException();
//                });
//
//        try {
//            ResponseEntity<String> response = appleClient.revokeToken(
//                    clientId,
//                    member.getAppleRefreshToken(),
//                    clientSecret,
//                    "refresh_token"
//            );
//
//            if (response.getStatusCode() == HttpStatus.OK) {
//                member.deactivateMember();
//                memberRepository.save(member);
//                log.info("애플 회원 탈퇴 및 비활성화 성공. 회원 번호: {}", memberNo);
//
//                return member.isActive();
//            } else {
//                log.error("애플 회원 탈퇴 실패. 응답 코드: {}", response.getStatusCode());
//                throw new AppleOauthUnlinkException();
//            }
//        } catch (Exception e) {
//            log.error("회원 탈퇴 중 오류 발생. 오류: {}", e.getMessage(), e);
//            throw new AppleOauthUnlinkException();
//        }
//
//    }

    // 새로운 토큰 생성 & 반환
    private Map<String, String> generateTokens(User user, boolean isNewMember) {
        log.info("새로운 토큰 생성 요청. 회원 번호: {}", user.getId());

        PrincipalDetails principal = new PrincipalDetails(user);

        String accessToken = jwtProvider.createAccessToken(principal, user.getId());
        String refreshToken = jwtProvider.createRefreshToken(principal, user.getId());

        jwtProvider.reissueToken(refreshToken);

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);

//        if (isNewMember)
        response.put("userId", user.getId().toString());

        log.info("토큰 생성 완료. 응답: {}", response);

        return response;
    }

    // 로그인 메서드
    private Map<String, String> loginWithApple(User user) {
        log.info("애플 로그인 처리. 회원 번호: {}", user.getId());

//        member.activateMember();  // 멤버의 활성화 상태를 true로 변경, deactivateDate를 null로 설정

        return generateTokens(user, false);
    }

    // 회원 생성 및 로그인 메서드
    private Map<String, String> loginWithAppleCreateMember(Map<String, String> appleResponse, String appleRefreshToken) {
        log.info("새로운 애플 회원 생성 및 로그인 처리. 애플 응답: {}", appleResponse);

        User user = User.builder()
                .socialId(appleResponse.get("sub"))
                .nickname(NicknameGenerator.generate())
//                .isActive(true)
//                .isBlacklisted(false)
//                .isTermsAgreed(false)
                .socialType(SocialType.APPLE)
                .role(UserRole.NORMAL)
                .image(null)
                .region(null)
                .build();

        userRepository.saveAndFlush(user);
        log.info("새로운 애플 회원 생성 완료. 회원 번호: {}", user.getId());

        return generateTokens(user, true);
    }
}
