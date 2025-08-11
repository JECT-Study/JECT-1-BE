package ject.mycode.global.config;

import ject.mycode.domain.auth.jwt.filter.JwtFilter;
import ject.mycode.domain.auth.jwt.handler.JwtAccessDeniedHandler;
import ject.mycode.domain.auth.jwt.handler.JwtAuthenticationEntryPoint;
import ject.mycode.domain.auth.jwt.userdetails.PrincipalDetailsService;
import ject.mycode.domain.auth.jwt.util.JwtProvider;
import ject.mycode.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtProvider jwtProvider;
    private final RedisUtil redisUtil;
    private final PrincipalDetailsService principalDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/swagger-ui.html",
                                "/auth/login/kakao/**",
                                "/auth/**"
                        ).permitAll()
                        .requestMatchers("/api/auth/**", "/**", "/aws").permitAll()
                        .anyRequest().authenticated())
                        //JwtAuthFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
                        .addFilterBefore(new JwtFilter(jwtProvider, redisUtil, principalDetailsService), UsernamePasswordAuthenticationFilter.class)
                        // 예외 처리 설정
                        .exceptionHandling(exception -> exception
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler)
                        );
//                .userDetailsService(customUserDetailsService)
//                .exceptionHandling(exception ->
//                {
//                    exception.authenticationEntryPoint(customJwtAuthenticationEntryPoint);
//                    exception.accessDeniedHandler(customAccessDeniedHandler);
//                })
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
