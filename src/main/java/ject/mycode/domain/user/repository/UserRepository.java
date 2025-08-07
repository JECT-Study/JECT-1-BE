package ject.mycode.domain.user.repository;


import ject.mycode.domain.user.entity.User;
import ject.mycode.domain.user.enums.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialId(String socialId);
    boolean existsById(Long userId);
    Optional<User> findById(Long userId);
    Optional<User> findBySocialIdAndSocialType(String socialId, SocialType socialType);

}