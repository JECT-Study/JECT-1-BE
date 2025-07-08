package ject.mycode.domain.schedule.service;

import ject.mycode.domain.schedule.dto.AddScheduleReq;
import ject.mycode.domain.user.entity.User;

public interface ScheduleService {
	public Long addSchedule(User user, Long contentId, AddScheduleReq addScheduleReq);
}
