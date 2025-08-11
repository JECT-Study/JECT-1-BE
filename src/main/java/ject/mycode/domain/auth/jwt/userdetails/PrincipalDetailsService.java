package ject.mycode.domain.auth.jwt.userdetails;

import ject.mycode.domain.user.entity.User;
import ject.mycode.domain.user.repository.UserRepository;
import ject.mycode.global.exception.AuthHandler;
import ject.mycode.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String socialId) throws UsernameNotFoundException {
        User user = userRepository.findBySocialId(socialId).orElseThrow(() -> new AuthHandler(BaseResponseCode.USER_NOT_FOUND));
        return new PrincipalDetails(user);
    }

}
