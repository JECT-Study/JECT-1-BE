package ject.mycode.domain.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import ject.mycode.domain.auth.dto.AuthReq;
import ject.mycode.domain.auth.dto.AuthRes;
import ject.mycode.domain.auth.jwt.dto.JwtRes;
import ject.mycode.domain.auth.jwt.userdetails.PrincipalDetails;
import ject.mycode.domain.auth.jwt.util.JwtProvider;
import ject.mycode.domain.user.converter.UserConverter;
import ject.mycode.domain.user.entity.User;
import ject.mycode.domain.user.enums.UserRole;
import ject.mycode.domain.user.repository.UserRepository;
import ject.mycode.global.exception.AuthHandler;
import ject.mycode.global.response.ErrorResponseCode;
import ject.mycode.global.util.NicknameGenerator;
import ject.mycode.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisUtil redisUtil;

    @Override
    public User signup(AuthReq.SignupDTO request) {
        if(!request.getPassword().equals(request.getPasswordCheck())) {
            throw new AuthHandler(ErrorResponseCode.PASSWORD_NOT_EQUAL);
        }
        User newUser = UserConverter.toUser(request);
        newUser.encodePassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(newUser);
    }

    @Override
    public AuthRes.LoginResultDTO login(AuthReq.SocialLoginDTO request) {
        // socialId + socialType 기준으로 사용자 조회
        Optional<User> optionalUser = userRepository.findBySocialIdAndSocialType(
                request.getSocialId(), request.getSocialType()
        );

        User user;

        if (optionalUser.isPresent()) {
            // 이미 존재하는 사용자 → 로그인 처리
            user = optionalUser.get();
        } else {
            // 사용자 없으면 → 회원가입 처리
            user = User.builder()
                    .socialId(request.getSocialId())
                    .socialType(request.getSocialType())
                    .nickname(NicknameGenerator.generate()) // 랜덤 닉네임
                    .role(UserRole.NORMAL) // 권한 기본값
                    .region(null)
                    .image(null)
                    .build();

            userRepository.save(user);
        }

        // 토큰 생성
        PrincipalDetails memberDetails = new PrincipalDetails(user);
        String accessToken = jwtProvider.createAccessToken(memberDetails, user.getId());
        String refreshToken = jwtProvider.createRefreshToken(memberDetails, user.getId());

        return UserConverter.toLoginResultDTO(user, accessToken, refreshToken);
    }


    @Override
    public JwtRes reissueToken(String refreshToken) {
        try {
            jwtProvider.validateRefreshToken(refreshToken);
            return jwtProvider.reissueToken(refreshToken);
        } catch (ExpiredJwtException eje) {
            throw new AuthHandler(ErrorResponseCode.TOKEN_EXPIRED);
        } catch (IllegalArgumentException iae) {
            throw new AuthHandler(ErrorResponseCode.INVALID_TOKEN);
        }
    }

    @Override
    public void logout(HttpServletRequest request) {
        try {
            String accessToken = jwtProvider.resolveAccessToken(request);
            // 블랙리스트에 저장
            redisUtil.set(accessToken, "logout");
            redisUtil.expire(accessToken, jwtProvider.getExpTime(accessToken), TimeUnit.MILLISECONDS);
            // RefreshToken 삭제
            redisUtil.delete(jwtProvider.getSocialId(accessToken));
        } catch (ExpiredJwtException e) {
            throw new AuthHandler(ErrorResponseCode.TOKEN_EXPIRED);
        }
    }

    @Override
    public boolean checkNickname(AuthReq.CheckNicknameDTO request) {
        System.out.println(request.getNickname());
        return userRepository.existsByNickname(request.getNickname());
    }

    @Override
    public boolean checkId(AuthReq.CheckIdDTO request) {
        return userRepository.existsById(request.getId());
    }
}
