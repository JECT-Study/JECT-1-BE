package ject.mycode.domain.user.converter;

import ject.mycode.domain.auth.dto.AuthReq;
import ject.mycode.domain.auth.dto.AuthRes;
import ject.mycode.domain.user.entity.User;
import ject.mycode.domain.user.enums.SocialType;
import ject.mycode.domain.user.enums.UserRole;

import java.time.LocalDateTime;

public class UserConverter {
    public static User toUser(AuthReq.SignupDTO request) {
        return User.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .socialId("temp")
                .socialType(SocialType.NONE)
                .image(null)
                .role(UserRole.NORMAL)
                .region(null)
                .build();
    }

    public static AuthRes.SignupResultDTO toSignupResultDTO(User user) {
        return AuthRes.SignupResultDTO.builder()
                .id(user.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static AuthRes.CheckNicknameResultDTO toCheckNicknameResultDTO(boolean isDuplicated) {
        return AuthRes.CheckNicknameResultDTO.builder()
                .isDuplicated(isDuplicated)
                .build();
    }

    public static AuthRes.CheckIdResultDTO toCheckIdResultDTO(boolean isDuplicated) {
        return AuthRes.CheckIdResultDTO.builder()
                .isDuplicated(isDuplicated)
                .build();
    }

//    public static AuthResponseDTO.VerifyCodeResultDTO toVerifyCodeResultDTO(boolean isVerified) {
//        return AuthResponseDTO.VerifyCodeResultDTO.builder()
//                .isVerified(isVerified)
//                .build();
//    }
}
