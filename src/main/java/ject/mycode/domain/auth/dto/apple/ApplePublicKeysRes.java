package ject.mycode.domain.auth.dto.apple;

import ject.mycode.global.exception.AppleOauthException;

import java.util.List;

public record ApplePublicKeysRes(
        List<ApplePublicKeyRes> keys

) {
    public ApplePublicKeysRes {
        keys = List.copyOf(keys);
    }

    public ApplePublicKeyRes getMatchingKey(final String alg, final String kid) {
        return keys.stream()
                .filter(key -> key.isSameAlg(alg) && key.isSameKid(kid))
                .findFirst()
                .orElseThrow(() -> new AppleOauthException("public-key 형태가 잘못되었습니다."));
    }
}