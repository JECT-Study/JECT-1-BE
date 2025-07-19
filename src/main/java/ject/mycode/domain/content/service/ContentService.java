package ject.mycode.domain.content.service;

import ject.mycode.domain.content.dto.ContentDetailsRes;
import ject.mycode.domain.content.dto.ContentRecommendRes;
import ject.mycode.domain.content.dto.HotContentRes;
import ject.mycode.domain.content.dto.WeeklyContentRes;
import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.user.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface ContentService {
	Long addFavorite(User user, Long contentId);

	ContentDetailsRes getContentDetails(Long contentId);

	List<ContentRecommendRes> getRecommendedContents(ContentType contentType);

	List<HotContentRes> getHotContents(ContentType contentType);

	List<WeeklyContentRes> getContentsByDate(LocalDate date);
}
