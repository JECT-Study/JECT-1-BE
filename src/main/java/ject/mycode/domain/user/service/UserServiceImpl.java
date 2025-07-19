package ject.mycode.domain.user.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ject.mycode.domain.content.dto.FavoritesRes;
import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.content.repository.custom.ContentQueryRepositoryImpl;
import ject.mycode.domain.user.dto.MySchedulesRes;
import ject.mycode.domain.user.entity.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final ContentQueryRepositoryImpl contentQueryRepository;

	@Override
	@Transactional(readOnly = true)
	public Page<FavoritesRes> getUserFavorites(User user, ContentType contentType, Pageable pageable) {

		return contentQueryRepository.findFavoritesByUserId(user.getId(), contentType,
			pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<MySchedulesRes> getMySchedules(User user, LocalDate day, Pageable pageable) {

		return contentQueryRepository.findMySchedulesByUserId(1L, day,
			pageable);
	}
}
