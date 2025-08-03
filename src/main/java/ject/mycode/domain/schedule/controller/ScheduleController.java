package ject.mycode.domain.schedule.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import ject.mycode.domain.auth.jwt.annotation.CurrentUser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ject.mycode.domain.schedule.dto.AddScheduleReq;
import ject.mycode.domain.schedule.service.ScheduleServiceImpl;
import ject.mycode.domain.user.dto.SchedulesInfoRes;
import ject.mycode.domain.user.entity.User;
import ject.mycode.global.response.BaseResponse;
import ject.mycode.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ScheduleController {

	private final ScheduleServiceImpl scheduleService;

	@PostMapping("/contents/{contentId}/my-schedules")
	public BaseResponse<Long> addSchedule(@CurrentUser User user, @PathVariable Long contentId, @RequestBody
		AddScheduleReq addScheduleReq) {

		return new BaseResponse<>(BaseResponseCode.ADD_MY_SCHEDULE_SUCCESS,
			scheduleService.addSchedule(user, contentId, addScheduleReq));
	}

	@GetMapping("/schedules")
	public BaseResponse<Page<SchedulesInfoRes>> getSchedules(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int limit,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {

		Pageable pageable = PageRequest.of(page, limit);
		return new BaseResponse<>(BaseResponseCode.GET_SCHEDULE_SUCCESS, scheduleService.getSchedules(pageable, day));
	}
}
