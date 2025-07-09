package ject.mycode.domain.content.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ject.mycode.domain.content.entity.Content;
import ject.mycode.domain.content.repository.ContentRepository;
import ject.mycode.domain.favorite.entity.Favorite;
import ject.mycode.domain.favorite.repository.FavoriteRepository;
import ject.mycode.domain.user.entity.User;
import ject.mycode.global.exception.CustomException;
import ject.mycode.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

	private final ContentRepository contentRepository;
	private final FavoriteRepository favoriteRepository;

	@Override
	@Transactional
	public Long addFavorite(User user, Long contentId) {
		Content findContent = contentRepository.findById(contentId)
			.orElseThrow(() -> new CustomException(BaseResponseCode.CONTENT_NOT_EXIST));

		Favorite favorite = Favorite.builder()
			.content(findContent)
			.user(user)
			.build();

		return favoriteRepository.save(favorite).getId();
	}
}
