package ject.mycode.domain.auth.service;

import ject.mycode.domain.auth.dto.AuthReq;
import ject.mycode.domain.user.converter.UserConverter;
import ject.mycode.domain.user.entity.User;
import ject.mycode.domain.user.repository.UserRepository;
import ject.mycode.global.exception.AuthHandler;
import ject.mycode.global.response.ErrorResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthCommandServiceImpl implements AuthCommandService {
    private final UserRepository userRepository;

    @Override
    public User signup(AuthReq.SignupDTO request) {
        if(!request.getPassword().equals(request.getPasswordCheck())) {
            throw new AuthHandler(ErrorResponseCode.PASSWORD_NOT_EQUAL);
        }

        User user = UserConverter.toUser(request);
        return userRepository.save(user);
    }

    @Override
    public boolean checkNickname(AuthReq.CheckNicknameDTO request) {
        System.out.println(request.getNickname());
        return userRepository.existsByNickname(request.getNickname());
    }

    @Override
    public boolean checkId(AuthReq.CheckIdDTO request) {
        return userRepository.existsById(request.getId());
    }
}
