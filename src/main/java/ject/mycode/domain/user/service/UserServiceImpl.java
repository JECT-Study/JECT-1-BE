package ject.mycode.domain.user.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ject.mycode.domain.content.dto.FavoritesRes;
import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.content.repository.custom.ContentQueryRepositoryImpl;
import ject.mycode.domain.user.entity.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final ContentQueryRepositoryImpl contentQueryRepository;

	@Override
	public Page<FavoritesRes> getUserFavorites(User user, ContentType contentType, Pageable pageable) {
		Page<FavoritesRes> findFavorites = contentQueryRepository.findFavoritesByUserId(user.getId(), contentType,
			pageable);

		return findFavorites;
	}
}
