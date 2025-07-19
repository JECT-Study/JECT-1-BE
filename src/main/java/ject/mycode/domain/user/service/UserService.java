package ject.mycode.domain.user.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ject.mycode.domain.content.dto.FavoritesRes;
import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.user.dto.MySchedulesRes;
import ject.mycode.domain.user.entity.User;

public interface UserService {
	Page<FavoritesRes> getUserFavorites(User user, ContentType contentType, Pageable pageable);

	Page<MySchedulesRes> getMySchedules(User user, LocalDate day, Pageable pageable);

	List<LocalDate> getDaysWithSchedules(User user, YearMonth month);
}
