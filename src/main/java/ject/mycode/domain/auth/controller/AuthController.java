package ject.mycode.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import ject.mycode.domain.auth.dto.AuthReq;
import ject.mycode.domain.auth.dto.AuthRes;
import ject.mycode.domain.auth.service.AuthCommandService;
import ject.mycode.domain.user.converter.UserConverter;
import ject.mycode.global.response.BaseResponse;
import ject.mycode.global.response.BaseResponseCode;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthCommandService authCommandService;

    @Operation(summary = "회원가입", description = "회원가입 기능입니다.")
    @PostMapping("/signup")
    public BaseResponse<AuthRes.SignupResultDTO> signup(@RequestBody @Valid AuthReq.SignupDTO request) {
        return new BaseResponse<>(BaseResponseCode.LOGIN_SUCCESS, UserConverter.toSignupResultDTO(authCommandService.signup(request)));
    }

}
