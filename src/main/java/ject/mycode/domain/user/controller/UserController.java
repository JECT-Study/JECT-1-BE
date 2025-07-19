package ject.mycode.domain.user.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ject.mycode.domain.content.dto.FavoritesRes;
import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.user.dto.MySchedulesRes;
import ject.mycode.domain.user.entity.User;
import ject.mycode.domain.user.service.UserServiceImpl;
import ject.mycode.global.response.BaseResponse;
import ject.mycode.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
	private final UserServiceImpl userService;

	@GetMapping("/users/favorites")
	public BaseResponse<Page<FavoritesRes>> getUserFavorites(@AuthenticationPrincipal User user,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int limit,
		@RequestParam(required = false)ContentType contentType) {

		Pageable pageable = PageRequest.of(page, limit);
		return new BaseResponse<>(BaseResponseCode.GET_FAVORITES,
			userService.getUserFavorites(user, contentType, pageable));
	}

	@GetMapping("/users/schedules")
	public BaseResponse<Page<MySchedulesRes>> getMySchedules(@AuthenticationPrincipal User user,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int limit,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {

		Pageable pageable = PageRequest.of(page, limit);
		return new BaseResponse<>(BaseResponseCode.GET_MY_SCHEDULES, userService.getMySchedules(user, day, pageable));
	}

	@GetMapping("/users/check/schedules")
	public BaseResponse<List<LocalDate>> getScheduledDays(
		@AuthenticationPrincipal User user,
		@RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month
	) {
		return new BaseResponse<>(BaseResponseCode.GET_SCHEDULED_DATES, userService.getDaysWithSchedules(user, month));
	}
}
