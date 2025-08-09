package ject.mycode.domain.favorite.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ject.mycode.domain.favorite.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
	long countByContentId(Long contentId);

	Optional<Favorite> findByUserIdAndContentId(Long userId, Long contentId);
}
