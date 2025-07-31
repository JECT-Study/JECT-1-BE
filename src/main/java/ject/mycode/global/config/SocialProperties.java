package ject.mycode.global.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.social") // yml 파일 'spring.social' 아래 값을 바인딩
public class SocialProperties {
    private ProviderProperties kakao;

    @Data
    public static class ProviderProperties {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private String tokenUri;
        private String userInfoUri;
    }
}