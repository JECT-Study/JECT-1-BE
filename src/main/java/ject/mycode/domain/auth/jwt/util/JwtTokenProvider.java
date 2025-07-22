package ject.mycode.domain.auth.jwt.util;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import ject.mycode.domain.auth.dto.TokenRes;
import ject.mycode.domain.auth.jwt.enums.JwtValidationType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private static final String MEMBER_ID = "memberId";
    private static final Long TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000L;

    @Value("${jwt.secret}")
    private String JWT_SECRET;

    @PostConstruct
    protected void init() {
        //base64 라이브러리에서 encodeToString을 이용해서 byte[] 형식을 String 형식으로 변환
        JWT_SECRET = Base64.getEncoder().encodeToString(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
    }

//    public TokenRes generateToken(Authentication authentication) {
//        final Date now = new Date();
//
//        final Claims claims = Jwts.claims()
//                .setIssuedAt(now)
//                .setExpiration(new Date(now.getTime() + TOKEN_EXPIRATION_TIME));  // 만료 시간 설정
//
//        claims.put(MEMBER_ID, authentication.getPrincipal());
//
//
//        String accessToken = Jwts.builder()
//                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
//                .setClaims(claims)
//                .signWith(getSigningKey())
//                .compact();
//
//        String refreshToken = Jwts.builder()
//                .setExpiration(new Date(now.getTime() + TOKEN_EXPIRATION_TIME * 2))
//                .signWith(getSigningKey())
//                .compact();
//
//        return new TokenRes(accessToken, refreshToken);
//    }

    private SecretKey getSigningKey() {
        String encodedKey = Base64.getEncoder().encodeToString(JWT_SECRET.getBytes()); //SecretKey 통해 서명 생성
        return Keys.hmacShaKeyFor(encodedKey.getBytes());   //일반적으로 HMAC (Hash-based Message Authentication Code) 알고리즘 사용
    }

    public JwtValidationType validateToken(String token) {
        try {
            final Claims claims = getBody(token);
            return JwtValidationType.VALID_JWT;
        } catch (MalformedJwtException ex) {
            return JwtValidationType.INVALID_JWT_TOKEN;
        } catch (ExpiredJwtException ex) {
            return JwtValidationType.EXPIRED_JWT_TOKEN;
        } catch (UnsupportedJwtException ex) {
            return JwtValidationType.UNSUPPORTED_JWT_TOKEN;
        } catch (IllegalArgumentException ex) {
            return JwtValidationType.EMPTY_JWT;
        }
    }

    private Claims getBody(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserFromJwt(String token) {
        Claims claims = getBody(token);
        return Long.valueOf(claims.get(MEMBER_ID).toString());
    }
}

