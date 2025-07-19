package ject.mycode.domain.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ject.mycode.domain.content.dto.FavoritesRes;
import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.user.entity.User;

public interface UserService {
	Page<FavoritesRes> getUserFavorites(User user, ContentType contentType, Pageable pageable);
}
