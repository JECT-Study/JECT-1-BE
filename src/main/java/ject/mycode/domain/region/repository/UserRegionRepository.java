package ject.mycode.domain.region.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ject.mycode.domain.region.entity.UserRegion;
import ject.mycode.domain.user.entity.User;

public interface UserRegionRepository extends JpaRepository<UserRegion, Long> {
	List<UserRegion> findByUser(User user);

	void deleteByUser(User user);

	List<UserRegion> findAllByUserId(Long userId);
}
