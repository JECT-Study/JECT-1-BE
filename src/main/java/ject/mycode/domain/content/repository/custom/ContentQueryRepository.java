package ject.mycode.domain.content.repository.custom;

import java.time.LocalDate;
import java.util.List;

import ject.mycode.domain.content.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.user.dto.SchedulesInfoRes;
import ject.mycode.domain.user.entity.User;

public interface ContentQueryRepository {
	ContentDetailsRes findDetailsByContentId(User user, Long contentId);

	Page<FavoritesRes> findFavoritesByUserId(Long userId, ContentType contentType, Pageable pageable);

	Page<SchedulesInfoRes> findMySchedulesByUserId(Long userId, LocalDate day, Pageable pageable);

	List<ContentRecommendRes> findRecommendedContents(ContentType contentType);
  
	List<LocalDate> findContentsByUserIdAndDateRange(Long id, LocalDate start, LocalDate end);

	List<HotContentRes> findHotContentsThisMonth(ContentType contentType);

	List<WeeklyContentRes> findContentsByDate(LocalDate date);

	List<ContentCategoryRes> findContentsByCategory(ContentType contentType);

	Page<SchedulesInfoRes> findSchedulesByDate(Pageable pageable, LocalDate day);

	List<ContentRegionRes> findRecommendedByUserRegion(Long userId);
}
