package ject.mycode.domain.schedule.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ject.mycode.domain.content.entity.Content;
import ject.mycode.domain.content.repository.ContentRepository;
import ject.mycode.domain.content.repository.custom.ContentQueryRepositoryImpl;
import ject.mycode.domain.schedule.dto.AddScheduleReq;
import ject.mycode.domain.schedule.entity.Schedule;
import ject.mycode.domain.schedule.repository.ScheduleRepository;
import ject.mycode.domain.user.dto.SchedulesInfoRes;
import ject.mycode.domain.user.entity.User;
import ject.mycode.global.exception.CustomException;
import ject.mycode.global.response.BaseResponseCode;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

	private final ScheduleRepository scheduleRepository;
	private final ContentRepository contentRepository;
	private final ContentQueryRepositoryImpl contentQueryRepository;

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

	@Override
	@Transactional(readOnly = true)
	public Page<SchedulesInfoRes> getSchedules(Pageable pageable, LocalDate day) {
		return contentQueryRepository.findSchedulesByDate(pageable, day);
	}
}
