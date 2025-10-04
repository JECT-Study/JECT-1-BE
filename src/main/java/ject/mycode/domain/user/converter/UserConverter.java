package ject.mycode.domain.user.converter;

import ject.mycode.domain.auth.dto.AuthRes;
import ject.mycode.domain.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserConverter {
    public static AuthRes.LoginResultDTO toLoginResultDTO(User user, String accessToken, String refreshToken) {
        // 1. User 엔티티의 userRegions 리스트를 UserRegionDTO 리스트로 변환
        List<AuthRes.UserRegionDTO> userRegionDTOList = user.getUserRegions().stream()
                // UserRegion 엔티티에서 Region 엔티티를 거쳐 ID와 Name을 추출
                .map(userRegion -> AuthRes.UserRegionDTO.builder()
                        .id(userRegion.getRegion().getId())
                        .name(userRegion.getRegion().getName()) // Region 엔티티에 getName()이 있다고 가정
                        .build())
                .collect(Collectors.toList());

        // 2. DTO를 빌드할 때 변환된 리스트를 넣어줍니다.
        return AuthRes.LoginResultDTO.builder()
                .id(user.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .image(user.getImage())
                .nickname(user.getNickname())
                .userRegions(userRegionDTOList) // <--- List<UserRegionDTO>로 변경
                .build();
    }
}