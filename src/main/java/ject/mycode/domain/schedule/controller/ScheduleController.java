package ject.mycode.domain.schedule.controller;

import ject.mycode.domain.auth.jwt.annotation.CurrentUser;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ject.mycode.domain.schedule.dto.AddScheduleReq;
import ject.mycode.domain.schedule.service.ScheduleServiceImpl;
import ject.mycode.domain.user.entity.User;
import ject.mycode.global.response.BaseResponse;
import ject.mycode.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ScheduleController {

	private final ScheduleServiceImpl scheduleServiceImpl;

	@PostMapping("/contents/{contentId}/schedules")
	public BaseResponse<Long> addSchedule(@CurrentUser User user, @PathVariable Long contentId, @RequestBody
		AddScheduleReq addScheduleReq) {

		return new BaseResponse<>(BaseResponseCode.ADD_SCHEDULE_SUCCESS,
			scheduleServiceImpl.addSchedule(user, contentId, addScheduleReq));
	}
}
