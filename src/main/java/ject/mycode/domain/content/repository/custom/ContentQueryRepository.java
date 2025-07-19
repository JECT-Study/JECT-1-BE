package ject.mycode.domain.content.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ject.mycode.domain.content.dto.ContentDetailsRes;
import ject.mycode.domain.content.dto.FavoritesRes;
import ject.mycode.domain.content.enums.ContentType;

public interface ContentQueryRepository {
	ContentDetailsRes findDetailsByContentId(Long contentId);

	Page<FavoritesRes> findFavoritesByUserId(Long userId, ContentType contentType, Pageable pageable);
}
