package ject.mycode.domain.content.repository.custom;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ject.mycode.domain.content.dto.ContentDetailsRes;
import ject.mycode.domain.content.dto.FavoritesRes;
import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.user.dto.MySchedulesRes;

public interface ContentQueryRepository {
	ContentDetailsRes findDetailsByContentId(Long contentId);

	Page<FavoritesRes> findFavoritesByUserId(Long userId, ContentType contentType, Pageable pageable);

	Page<MySchedulesRes> findMySchedulesByUserId(Long userId, LocalDate day, Pageable pageable);
}
