package ject.mycode.domain.content.dto;

import ject.mycode.domain.content.entity.Content;
import ject.mycode.domain.content.enums.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContentRecommendRes {

    private Long contentId;
    private String title;
    private String image;
    private ContentType contentType;
    private String address;
    private double longitude;
    private double latitude;
    private String startDate;
    private String endDate;

    public static ContentRecommendRes fromEntity(Content content) {
        String imageUrl = content.getImages().isEmpty() ? null : content.getImages().get(0).getImageUrl();

        return new ContentRecommendRes(
                content.getId(),
                content.getTitle(),
                imageUrl,
                content.getContentType(),
                content.getAddress(),
                content.getLongitude(),
                content.getLatitude(),
                content.getStartDate().toString(),
                content.getEndDate().toString()
        );
    }
}
