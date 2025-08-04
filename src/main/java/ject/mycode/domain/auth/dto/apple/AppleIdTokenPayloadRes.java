package ject.mycode.domain.auth.dto.apple;

import lombok.Getter;

@Getter
public class AppleIdTokenPayloadRes {
    private String sub;
    private String email;
    private boolean emailVerified;
    private long authTime;
}
