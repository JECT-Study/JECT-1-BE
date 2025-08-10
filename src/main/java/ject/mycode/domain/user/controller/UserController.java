package ject.mycode.domain.user.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import ject.mycode.domain.auth.jwt.annotation.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ject.mycode.domain.content.dto.FavoritesRes;
import ject.mycode.domain.content.enums.ContentType;
import ject.mycode.domain.user.dto.SchedulesInfoRes;
import ject.mycode.domain.user.entity.User;
import ject.mycode.domain.user.repository.UserRepository;
import ject.mycode.domain.user.service.UserServiceImpl;
import ject.mycode.global.response.BaseResponse;
import ject.mycode.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {
	private final UserServiceImpl userService;
	private final UserRepository userRepository;

	@GetMapping("/users/favorites")
	public BaseResponse<Page<FavoritesRes>> getUserFavorites(@CurrentUser User user,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int limit,
		@RequestParam(required = false)ContentType contentType) {

		Pageable pageable = PageRequest.of(page, limit);
		return new BaseResponse<>(BaseResponseCode.GET_FAVORITES,
			userService.getUserFavorites(user, contentType, pageable));
	}

	@GetMapping("/users/schedules")
	public BaseResponse<Page<SchedulesInfoRes>> getMySchedules(@CurrentUser User user,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int limit,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {

		Pageable pageable = PageRequest.of(page, limit);
		return new BaseResponse<>(BaseResponseCode.GET_MY_SCHEDULES, userService.getMySchedules(user, day, pageable));
	}

	@GetMapping("/users/schedules/check")
	public BaseResponse<List<LocalDate>> getScheduledDays(
		@CurrentUser User user,
		@RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month
	) {
		return new BaseResponse<>(BaseResponseCode.GET_SCHEDULED_DATES, userService.getDaysWithSchedules(user, month));
	}

	@PatchMapping("/users/profile")
	public BaseResponse<Void> changeUserProfile(@CurrentUser User user,
		@RequestPart(value = "image", required = false) MultipartFile image,
		@RequestPart("nickname") String nickname) {
		userService.changeUserProfile(user, image, nickname);
		return new BaseResponse<>(BaseResponseCode.CHANGE_PROFILE_SUCCESS);
	}
}
