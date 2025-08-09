package ject.mycode.domain.content.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ContentDetailsRes {
	private Long contentId;
	private Object likeId;
	private Object scheduleId;
	private String title;
	private List<String> images;
	private List<String> tags;
	private String placeName;
	private LocalDate startDate;
	private LocalDate endDate;
	private Long likes;
	private Boolean isAlwaysOpen;
	private LocalTime openingHour;
	private LocalTime closedHour;
	private String address;
	private String introduction;
	private String description;
	private Double longitude;
	private Double latitude;
}
