package ject.mycode.domain.auth.jwt.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import ject.mycode.domain.auth.jwt.dto.JwtRes;
import ject.mycode.domain.auth.jwt.userdetails.PrincipalDetails;
import ject.mycode.domain.auth.jwt.userdetails.PrincipalDetailsService;
import ject.mycode.global.exception.AuthHandler;
import ject.mycode.global.response.BaseResponseCode;
import ject.mycode.global.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JwtProvider {
    private final PrincipalDetailsService userDetailsService;
    private final SecretKey secret;
    private final Long accessExpiration;
    private final Long refreshExpiration;
    private final RedisUtil redisUtil;

    public JwtProvider(
            PrincipalDetailsService userDetailsService,
            @Value("${spring.jwt.secret}") String secret,
            @Value("${spring.jwt.token.access-expiration-time}") Long accessExpiration,
            @Value("${spring.jwt.token.refresh-expiration-time}") Long refreshExpiration,
            RedisUtil redisUtil) {
        this.userDetailsService = userDetailsService;
        this.secret = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
        this.redisUtil = redisUtil;
    }

    // AccessToken 생성
    public String createAccessToken(PrincipalDetails userDetails, Long userId) {
        Instant issuedAt = Instant.now();
        Instant expiredAt = issuedAt.plusMillis(accessExpiration);

        return Jwts.builder()
                .setHeader(Map.of("alg", "HS256", "typ", "JWT"))
                .setSubject(userDetails.getUsername()) // 이메일
                .claim("id", userId)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiredAt))
                .signWith(secret, SignatureAlgorithm.HS256)
                .compact();
    }

    // RefreshToken 생성
    public String createRefreshToken(PrincipalDetails userDetails, Long userId) {
        Instant issuedAt = Instant.now();
        Instant expiredAt = issuedAt.plusMillis(refreshExpiration);

        String refreshToken = Jwts.builder()
                .setHeader(Map.of("alg", "HS256", "typ", "JWT"))
                .setSubject(userDetails.getUsername()) // 소셜아이디
                .claim("id", userId)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiredAt))
                .signWith(secret, SignatureAlgorithm.HS256)
                .compact();
        redisUtil.set(userDetails.getUsername(), refreshToken);
        redisUtil.expire(userDetails.getUsername(), refreshExpiration, TimeUnit.MILLISECONDS);
        return refreshToken;
    }

    // 헤더에서 토큰 추출
    public String resolveAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.split(" ")[1];
    }

    // AccessToken 유효성 확인
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false;  // Expired
        } catch (JwtException e) {
            return false;  // Invalid
        }
    }


    // RefreshToken 유효성 확인
    public void validateRefreshToken(String refreshToken) {
        try {
            getClaims(refreshToken);  // 만료되면 ExpiredJwtException 발생
        } catch (ExpiredJwtException e) {
            throw new AuthHandler(BaseResponseCode.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new AuthHandler(BaseResponseCode.INVALID_TOKEN);
        }

        String username = getSocialId(refreshToken);
        String storedToken = redisUtil.get(username).toString();

        // Redis에 저장된 refreshToken과 동일한지 검사
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new AuthHandler(BaseResponseCode.INVALID_TOKEN);
        }
    }


    public String getSocialId(String token) {
        return getClaims(token).getBody().getSubject();
    }

    //id(PK) 추출
    public Long getId(String token) { return getClaims(token).getBody().get("id", Long.class); }

    //토큰의 클레임 가져오는 메서드
    public Jws<Claims> getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token);  // 예외는 그대로 던지기
    }


    // 토큰 재발급
    public JwtRes reissueToken(String refreshToken) throws SignatureException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getSocialId(refreshToken));
        Long userId = getId(refreshToken);

        return new JwtRes(
                createAccessToken((PrincipalDetails) userDetails, userId),
                createRefreshToken((PrincipalDetails)userDetails, userId)
        );
    }

    // 토큰 유효시간 반환
    public Long getExpTime(String token) {
        return getClaims(token).getPayload().getExpiration().getTime();
    }
}
