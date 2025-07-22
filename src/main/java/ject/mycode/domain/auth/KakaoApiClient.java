package ject.mycode.domain.auth;

import ject.mycode.domain.auth.dto.KakaoUserRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kakaoApiClient", url = "https://kapi.kakao.com")
public interface KakaoApiClient {

    @GetMapping(value = "/v2/user/me")
    KakaoUserRes getUserInformation(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken);
}
