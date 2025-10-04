package ject.mycode.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class AuthRes {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserRegionDTO {
        private Long id;
        private String name;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LoginResultDTO {
        Long id;
        String accessToken;
        String refreshToken;
        String image;
        String nickname;
        private List<UserRegionDTO> userRegions;
    }
}