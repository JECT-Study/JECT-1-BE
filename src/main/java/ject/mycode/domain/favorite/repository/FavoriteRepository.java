package ject.mycode.domain.favorite.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ject.mycode.domain.favorite.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
}
