package ject.mycode.domain.auth.client;

import ject.mycode.domain.auth.dto.apple.ApplePublicKeysRes;
import ject.mycode.domain.auth.dto.apple.AppleTokenRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "apple-oauth", url = "https://appleid.apple.com")
public interface AppleClient {

    @PostMapping("/auth/token")
    AppleTokenRes appleAuth(
            @RequestParam("client_id") String clientId,
            @RequestParam("code") String code,
            @RequestParam("grant_type") String grantType,
            @RequestParam("client_secret") String clientSecret
    );

    @GetMapping("/auth/keys")
    ApplePublicKeysRes getApplePublicKeys();

}