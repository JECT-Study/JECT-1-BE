package ject.mycode.domain.user.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SchedulesInfoRes {
	// TODO : contentID 말고 ScheduleID로 대체
	private Long contentId;
	private String title;
	private String image;
	private String address;
	private LocalDate startDate;
	private LocalDate endDate;
}
