package ject.mycode.domain.trait.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ject.mycode.domain.trait.entity.UserTrait;

public interface UserTraitRepository extends JpaRepository<UserTrait, Long> {
}
