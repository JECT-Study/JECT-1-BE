package ject.mycode.domain.auth.jwt.util;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AppleClaimsValidator {

    private static final String NONCE_KEY = "nonce";
    private final String iss;
    private final String clientId;

    public AppleClaimsValidator(
            @Value("${apple.iss}") String iss,
            @Value("${apple.client-id}") String clientId
    ) {
        this.iss = iss;
        this.clientId = clientId;
    }

    public boolean isValid(Claims claims) {
        // exp, nonce, iss, aud 검증
        Date expiration = claims.getExpiration();
        Date currentDate = new Date();

        return expiration.before(currentDate) &&
                claims.getIssuer().contains(iss) &&
                claims.getAudience().equals(clientId);
    }
}