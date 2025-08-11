package ject.mycode.domain.auth.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ject.mycode.domain.auth.jwt.userdetails.PrincipalDetailsService;
import ject.mycode.domain.auth.jwt.util.JwtProvider;
import ject.mycode.global.exception.AuthHandler;
import ject.mycode.global.exception.GeneralException;
import ject.mycode.global.response.BaseResponse;
import ject.mycode.global.response.BaseResponseCode;
import ject.mycode.global.util.RedisUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final RedisUtil redisUtil;
    private final PrincipalDetailsService principalDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String accessToken =  jwtProvider.resolveAccessToken(request);

            //JWT 유효성 검증
            if(accessToken != null && jwtProvider.validateToken(accessToken)) {
                String blackListValue = (String) redisUtil.get(accessToken);
                if (blackListValue != null && blackListValue.equals("logout")) {
                    throw new AuthHandler(BaseResponseCode.TOKEN_LOGGED_OUT);
                }
                String socialId = jwtProvider.getSocialId(accessToken);

                //유저와 토큰 일치 시 userDetails 생성
                UserDetails userDetails = principalDetailsService.loadUserByUsername(socialId);
                if (userDetails != null) {
                    //userDetails, password, role -> 접근 권한 인증 Token 생성
                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

                    //현재 Request의 Security Context에 접근 권한 설정
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new AuthHandler(BaseResponseCode.USER_NOT_FOUND);
                }
            }
            // 다음 필터로 넘기기
            filterChain.doFilter(request, response);
        } catch (GeneralException e) {
            BaseResponseCode code = e.getCode();  // 예외로부터 에러 코드 객체 받아옴

            response.setContentType("application/json; charset=UTF-8");
            response.setStatus(code.getHttpStatus().value());

            BaseResponse<Object> errorResponse = new BaseResponse<>(code);

            ObjectMapper om = new ObjectMapper();
            om.writeValue(response.getOutputStream(), errorResponse);
        }
    }
}
