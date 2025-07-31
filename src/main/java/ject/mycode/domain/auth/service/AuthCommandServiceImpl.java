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
import ject.mycode.domain.user.repository.UserRepository;
import ject.mycode.global.exception.AuthHandler;
import ject.mycode.global.response.ErrorResponseCode;
import ject.mycode.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    public AuthRes.LoginResultDTO login(AuthReq.LoginDTO request) {
        User user = userRepository.findById(Long.valueOf(request.getUserId())).orElseThrow(() -> new AuthHandler(ErrorResponseCode.USER_NOT_FOUND));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthHandler(ErrorResponseCode.PASSWORD_NOT_EQUAL);
        }

        PrincipalDetails memberDetails = new PrincipalDetails(user);

        // 로그인 성공 시 토큰 생성
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
            redisUtil.delete(jwtProvider.getEmail(accessToken));
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
