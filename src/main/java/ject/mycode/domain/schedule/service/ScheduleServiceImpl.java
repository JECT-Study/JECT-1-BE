package ject.mycode.domain.schedule.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ject.mycode.domain.content.entity.Content;
import ject.mycode.domain.content.repository.ContentRepository;
import ject.mycode.domain.schedule.dto.AddScheduleReq;
import ject.mycode.domain.schedule.entity.Schedule;
import ject.mycode.domain.schedule.repository.ScheduleRepository;
import ject.mycode.domain.user.entity.User;
import ject.mycode.global.exception.CustomException;
import ject.mycode.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

	private final ScheduleRepository scheduleRepository;
	private final ContentRepository contentRepository;

	@Override
	@Transactional
	public Long addSchedule(User user, Long contentId, AddScheduleReq addScheduleReq) {
		Content findContent = contentRepository.findById(contentId)
			.orElseThrow(() -> new CustomException(BaseResponseCode.CONTENT_NOT_EXIST));

		Schedule schedule = Schedule.builder()
			.scheduleDate(addScheduleReq.getDate())
			.user(user)
			.content(findContent)
			.build();

		return scheduleRepository.save(schedule).getId();
	}
}
