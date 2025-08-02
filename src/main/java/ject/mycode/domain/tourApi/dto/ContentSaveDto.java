package ject.mycode.domain.tourApi.dto;

import lombok.Builder;
import lombok.Data;
import ject.mycode.domain.content.enums.ContentType;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ContentSaveDto {
	private String title;
	private String placeName;
	private String introduction;
	private String address;
	private LocalDate startDate;
	private LocalDate endDate;
	private Double latitude;
	private Double longitude;
	private String homepageUrl;
	private ContentType contentType;
	private Long regionId;
	private List<String> imageUrls;
	private String apiId;
	private String description;
}
