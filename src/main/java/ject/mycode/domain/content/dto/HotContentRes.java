package ject.mycode.domain.content.dto;

import ject.mycode.domain.content.enums.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotContentRes {

    private Long contentId;
    private String title;
    private String image;
    private ContentType contentType;
    private String address;
    private double longitude;
    private double latitude;
    private String startDate;
    private String endDate;
}
