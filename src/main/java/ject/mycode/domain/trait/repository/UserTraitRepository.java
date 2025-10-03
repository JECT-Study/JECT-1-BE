package ject.mycode.domain.trait.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ject.mycode.domain.trait.entity.UserTrait;
import ject.mycode.domain.user.entity.User;

public interface UserTraitRepository extends JpaRepository<UserTrait, Long> {
	Optional<UserTrait> findByUserAndTraitId(User user, Long traitId);
}
