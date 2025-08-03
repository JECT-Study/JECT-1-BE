package ject.mycode.domain.schedule.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ject.mycode.domain.schedule.dto.AddScheduleReq;
import ject.mycode.domain.user.dto.SchedulesInfoRes;
import ject.mycode.domain.user.entity.User;

public interface ScheduleService {
	Long addSchedule(User user, Long contentId, AddScheduleReq addScheduleReq);

	Page<SchedulesInfoRes> getSchedules(Pageable pageable, LocalDate day);
}
